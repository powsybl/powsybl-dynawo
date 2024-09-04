/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security;

import com.powsybl.commons.PowsyblException;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.algorithms.DynawoAlgorithmsContext;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.events.ContextDependentEvent;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class SecurityAnalysisContext extends DynawoSimulationContext implements DynawoAlgorithmsContext {

    private final List<Contingency> contingencies;
    private final List<ContingencyEventModels> contingencyEventModels;

    public SecurityAnalysisContext(Network network, String workingVariantId,
                                   List<BlackBoxModel> dynamicModels,
                                   DynamicSecurityAnalysisParameters parameters,
                                   DynawoSimulationParameters dynawoSimulationParameters,
                                   List<Contingency> contingencies) {
        super(network, workingVariantId, dynamicModels, List.of(), Collections.emptyList(),
                parameters.getDynamicSimulationParameters(), dynawoSimulationParameters);
        double contingenciesStartTime = parameters.getDynamicContingenciesParameters().getContingenciesStartTime();
        this.contingencies = contingencies;
        //TODO use ContingencyEventModelsHandler instead
        this.contingencyEventModels = contingencies.stream()
                .map(c -> {
                    ContingencyEventModels cem = new ContingencyEventModels(c, c.getElements().stream()
                                    .map(ce -> createContingencyEventModel(ce, contingenciesStartTime))
                                    .toList());
                    // Set Contingencies connections and parameters
                    macroConnectionsAdder.setMacroConnectorAdder(cem.macroConnectorsMap()::computeIfAbsent);
                    macroConnectionsAdder.setMacroConnectAdder(cem.macroConnectList()::add);
                    cem.eventModels().forEach(em -> {
                        em.createMacroConnections(macroConnectionsAdder);
                        em.createDynamicModelParameters(this, cem.eventParameters()::add);
                    });
                    return cem;
                })
                .collect(Collectors.toList());
    }

    private BlackBoxModel createContingencyEventModel(ContingencyElement element, double contingenciesStartTime) {
        BlackBoxModel bbm = EventDisconnectionBuilder.of(network)
                .staticId(element.getId())
                .startTime(contingenciesStartTime)
                .build();
        if (bbm == null) {
            throw new PowsyblException("Contingency element " + element.getType() + " not supported");
        }
        if (bbm instanceof ContextDependentEvent cde) {
            cde.setEquipmentHasDynamicModel(this);
        }
        return bbm;
    }

    public List<Contingency> getContingencies() {
        return contingencies;
    }

    @Override
    public List<ContingencyEventModels> getContingencyEventModels() {
        return contingencyEventModels;
    }
}
