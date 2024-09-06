/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.commons.PowsyblException;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.events.ContextDependentEvent;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class ContingencyEventModelsFactory {

    public static List<ContingencyEventModels> createFrom(List<Contingency> contingencies, DynawoSimulationContext context,
                                                           MacroConnectionsAdder macroConnectionsAdder, double contingenciesStartTime) {
        return contingencies.stream()
                .map(c -> {
                    ContingencyEventModels cem = new ContingencyEventModels(c, c.getElements().stream()
                            .map(ce -> createContingencyEventModel(ce, context, contingenciesStartTime))
                            .toList());
                    // Set Contingencies connections and parameters
                    macroConnectionsAdder.setMacroConnectorAdder(cem.macroConnectorsMap()::computeIfAbsent);
                    macroConnectionsAdder.setMacroConnectAdder(cem.macroConnectList()::add);
                    cem.eventModels().forEach(em -> {
                        em.createMacroConnections(macroConnectionsAdder);
                        em.createDynamicModelParameters(context, cem.eventParameters()::add);
                    });
                    return cem;
                })
                .collect(Collectors.toList());
    }

    private static BlackBoxModel createContingencyEventModel(ContingencyElement element, DynawoSimulationContext context, double contingenciesStartTime) {
        BlackBoxModel bbm = EventDisconnectionBuilder.of(context.getNetwork())
                .staticId(element.getId())
                .startTime(contingenciesStartTime)
                .build();
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
