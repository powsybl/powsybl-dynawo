/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.events.ContextDependentEvent;
import com.powsybl.dynawo.models.events.NodeFaultEventBuilder;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Network;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationReports.createFailedNodeFaultReportNode;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class NodeFaultEventModelsFactory {

    /**
     * Creates NodeFaultEventModels from List<NodeFaultEventObject> list and context
     * The hasMacroConnector predicate is needed in order to verify if a macro connector used by a contingency is already defined in the base simulation model
     */
    public static List<NodeFaultEventModels> createFrom(List<NodeFaultEventData> nodeFaultsList,
                                                          Network network,
                                                          BlackBoxModelSupplier bbmSupplier,
                                                          Predicate<String> hasMacroConnector,
                                                          ReportNode reportNode) {

        return IntStream.range(0, nodeFaultsList.size())
                .mapToObj(i -> createFrom(i, nodeFaultsList.get(i), network, bbmSupplier, hasMacroConnector, reportNode))
                .filter(Objects::nonNull)
                .toList();
    }

    public static NodeFaultEventModels createFrom(int index, NodeFaultEventData nodeFaultEventData,
                                                    Network network,
                                                    BlackBoxModelSupplier bbmSupplier,
                                                    Predicate<String> hasMacroConnector,
                                                    ReportNode reportNode) {
        List<BlackBoxModel> eventModels = createContingencyEventModel(nodeFaultEventData, network, bbmSupplier, reportNode);
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
        return new NodeFaultEventModels(index, nodeFaultEventData, eventModels, macroConnectorsMap, macroConnectList, eventParameters);
    }

    private static List<BlackBoxModel> createContingencyEventModel(NodeFaultEventData nodeFaultEventData,
                                                                   Network network,
                                                                   BlackBoxModelSupplier bbmSupplier,
                                                                   ReportNode reportNode) {
        NodeFaultEventBuilder builder = NodeFaultEventBuilder.of(network, reportNode)
                .staticId(nodeFaultEventData.getStaticId())
                .startTime(nodeFaultEventData.getStartTime())
                .faultTime(nodeFaultEventData.getFaultTime())
                .rPu(nodeFaultEventData.getRPu())
                .xPu(nodeFaultEventData.getXPu());

        BlackBoxModel bbm = builder.build();
        if (bbm == null) {
            createFailedNodeFaultReportNode(reportNode);
            return List.of();
        }
        if (bbm instanceof ContextDependentEvent cde) {
            cde.setEquipmentModelType(bbmSupplier.hasDynamicModel(cde.getEquipment()));
        }
        return List.of(bbm);
    }

    private NodeFaultEventModelsFactory() {
    }
}
