/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.contingency.SidedContingencyElement;
import com.powsybl.dynawo.DynawoSimulationContext;
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
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class ContingencyEventModelsFactory {

    public static List<ContingencyEventModels> createFrom(List<Contingency> contingencies, DynawoSimulationContext context,
                                                           MacroConnectionsAdder macroConnectionsAdder, double contingenciesStartTime,
                                                          ReportNode reportNode) {
        return contingencies.stream()
                .map(c -> {
                    List<BlackBoxModel> eventModels = c.getElements().stream()
                            .map(ce -> createContingencyEventModel(ce, context, contingenciesStartTime, reportNode))
                            .filter(Objects::nonNull)
                            .toList();
                    if (eventModels.isEmpty()) {
                        return null;
                    }
                    Map<String, MacroConnector> macroConnectorsMap = new HashMap<>();
                    List<MacroConnect> macroConnectList = new ArrayList<>();
                    List<ParametersSet> eventParameters = new ArrayList<>(eventModels.size());
                    // Set Contingencies connections and parameters
                    macroConnectionsAdder.setMacroConnectorAdder(macroConnectorsMap::computeIfAbsent);
                    macroConnectionsAdder.setMacroConnectAdder(macroConnectList::add);
                    eventModels.forEach(em -> {
                        em.createMacroConnections(macroConnectionsAdder);
                        em.createDynamicModelParameters(context, eventParameters::add);
                    });
                    return new ContingencyEventModels(c, eventModels, macroConnectorsMap, macroConnectList, eventParameters);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static BlackBoxModel createContingencyEventModel(ContingencyElement element, DynawoSimulationContext context,
                                                             double contingenciesStartTime, ReportNode reportNode) {
        Network network = context.getNetwork();
        EventDisconnectionBuilder builder = EventDisconnectionBuilder.of(network)
                .staticId(element.getId())
                .startTime(contingenciesStartTime);
        if (element instanceof SidedContingencyElement sidedElement && sidedElement.getVoltageLevelId() != null) {
            TwoSides side = SidedContingencyElement.getContingencySide(network, sidedElement);
            if (side != null) {
                builder.disconnectOnly(side);
            } else {
                DynamicSecurityAnalysisReports.createContingencyVoltageIdNotFoundReportNode(reportNode,
                        sidedElement.getId(), sidedElement.getVoltageLevelId());
                return null;
            }
        }
        BlackBoxModel bbm = builder.build();
        if (bbm == null) {
            throw new PowsyblException("Contingency element " + element.getType() + " not supported");
        }
        if (bbm instanceof ContextDependentEvent cde) {
            cde.setEquipmentHasDynamicModel(context);
        }
        return bbm;
    }

    private ContingencyEventModelsFactory() {
    }
}
