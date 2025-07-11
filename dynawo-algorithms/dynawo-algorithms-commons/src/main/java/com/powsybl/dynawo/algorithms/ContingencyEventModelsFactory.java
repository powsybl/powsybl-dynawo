/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.contingency.SidedContingencyElement;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.events.ContextDependentEvent;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

import java.util.*;
import java.util.function.Predicate;

import static com.powsybl.dynawo.algorithms.DynawoAlgorithmsReports.createContingencyVoltageIdNotFoundReportNode;
import static com.powsybl.dynawo.algorithms.DynawoAlgorithmsReports.createNotSupportedContingencyTypeReportNode;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ContingencyEventModelsFactory {

    /**
     * Creates ContingencyEventModels from contingency list and context
     * The hasMacroConnector predicate is needed in order to verify if a macro connector used by a contingency is already defined in the base simulation model
     */
    public static List<ContingencyEventModels> createFrom(List<Contingency> contingencies,
                                                          double contingenciesStartTime,
                                                          Network network,
                                                          BlackBoxModelSupplier bbmSupplier,
                                                          Predicate<String> hasMacroConnector,
                                                          ReportNode reportNode) {
        return contingencies.stream()
                .map(c -> createFrom(c, contingenciesStartTime, network, bbmSupplier, hasMacroConnector, reportNode))
                .filter(Objects::nonNull)
                .toList();
    }

    public static ContingencyEventModels createFrom(Contingency contingency, double contingenciesStartTime,
                                                    Network network,
                                                    BlackBoxModelSupplier bbmSupplier,
                                                    Predicate<String> hasMacroConnector,
                                                    ReportNode reportNode) {
        List<BlackBoxModel> eventModels = createContingencyEventModelList(contingency, contingenciesStartTime, network, bbmSupplier, reportNode);
        if (eventModels.isEmpty()) {
            return null;
        }
        Map<String, MacroConnector> macroConnectorsMap = new HashMap<>();
        List<MacroConnect> macroConnectList = new ArrayList<>();
        List<ParametersSet> eventParameters = new ArrayList<>(eventModels.size());
        // Set Contingencies connections and parameters
        MacroConnectionsAdder macroConnectionsAdder = new MacroConnectionsAdder(bbmSupplier::getEquipmentDynamicModel,
                bbmSupplier::getPureDynamicModel, macroConnectList::add,
                (n, f) -> {
                    if (!hasMacroConnector.test(n)) {
                        macroConnectorsMap.computeIfAbsent(n, f);
                    }
                },
                reportNode);
        eventModels.forEach(em -> {
            em.createMacroConnections(macroConnectionsAdder);
            em.createDynamicModelParameters(eventParameters::add);
        });
        return new ContingencyEventModels(contingency, eventModels, macroConnectorsMap, macroConnectList, eventParameters);
    }

    private static List<BlackBoxModel> createContingencyEventModelList(Contingency contingency,
                                                                       double contingenciesStartTime,
                                                                       Network network,
                                                                       BlackBoxModelSupplier bbmSupplier,
                                                                       ReportNode reportNode) {
        return contingency.getElements().stream()
                .map(ce -> createContingencyEventModel(ce, contingenciesStartTime, network, bbmSupplier, reportNode))
                .filter(Objects::nonNull)
                .toList();
    }

    private static BlackBoxModel createContingencyEventModel(ContingencyElement element,
                                                             double contingenciesStartTime,
                                                             Network network,
                                                             BlackBoxModelSupplier bbmSupplier,
                                                             ReportNode reportNode) {
        EventDisconnectionBuilder builder = EventDisconnectionBuilder.of(network)
                .staticId(element.getId())
                .startTime(contingenciesStartTime);
        if (element instanceof SidedContingencyElement sidedElement && sidedElement.getVoltageLevelId() != null) {
            TwoSides side = SidedContingencyElement.getContingencySide(network, sidedElement);
            if (side != null) {
                builder.disconnectOnly(side);
            } else {
                createContingencyVoltageIdNotFoundReportNode(reportNode,
                        sidedElement.getId(), sidedElement.getVoltageLevelId());
                return null;
            }
        }
        BlackBoxModel bbm = builder.build();
        if (bbm == null) {
            createNotSupportedContingencyTypeReportNode(reportNode, element.getType().toString());
        }
        if (bbm instanceof ContextDependentEvent cde) {
            cde.setEquipmentHasDynamicModel(bbmSupplier.hasDynamicModel(cde.getEquipment()));
        }
        return bbm;
    }

    private ContingencyEventModelsFactory() {
    }
}
