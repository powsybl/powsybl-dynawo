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
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.events.ContextDependentEvent;
import com.powsybl.dynawaltz.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnect;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnector;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class SecurityAnalysisContext extends DynaWaltzContext {

    private final List<Contingency> contingencies;
    private final List<ContingencyEventModels> contingencyEventModels;

    public SecurityAnalysisContext(Network network, String workingVariantId,
                                   List<BlackBoxModel> dynamicModels,
                                   DynamicSecurityAnalysisParameters parameters,
                                   DynaWaltzParameters dynaWaltzParameters,
                                   List<Contingency> contingencies) {
        super(network, workingVariantId, dynamicModels, List.of(), Collections.emptyList(),
                parameters.getDynamicSimulationParameters(), dynaWaltzParameters);
        double contingenciesStartTime = parameters.getDynamicContingenciesParameters().getContingenciesStartTime();
        this.contingencies = contingencies;
        this.contingencyEventModels = contingencies.stream()
                .map(c -> {
                    List<BlackBoxModel> contEventModels = c.getElements().stream()
                            .map(ce -> {
                                BlackBoxModel bbm = this.createContingencyEventModel(ce, contingenciesStartTime);
                                if (bbm instanceof ContextDependentEvent cde) {
                                    cde.setEquipmentHasDynamicModel(this);
                                }
                                return bbm;
                            })
                            .collect(Collectors.toList());
                    Map<String, MacroConnector> macroConnectorsMap = new HashMap<>();
                    List<MacroConnect> macroConnects = new ArrayList<>();
                    List<ParametersSet> parametersSets = new ArrayList<>(contEventModels.size());
                    macroConnectionsAdder.setMacroConnectorAdder(macroConnectorsMap::computeIfAbsent);
                    macroConnectionsAdder.setMacroConnectAdder(macroConnects::add);
                    for (BlackBoxModel bbm : contEventModels) {
                        bbm.createMacroConnections(macroConnectionsAdder);
                        bbm.createDynamicModelParameters(this, parametersSets::add);
                    }
                    return new ContingencyEventModels(c, contEventModels, macroConnectorsMap, macroConnects, parametersSets);
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
        return bbm;
    }

    public List<Contingency> getContingencies() {
        return contingencies;
    }

    public List<ContingencyEventModels> getContingencyEventModels() {
        return contingencyEventModels;
    }
}
