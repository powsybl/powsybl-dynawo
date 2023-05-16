/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.PowsyblException;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.MacroConnect;
import com.powsybl.dynawaltz.models.MacroConnector;
import com.powsybl.dynawaltz.models.events.EventInjectionDisconnection;
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection;
import com.powsybl.iidm.network.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class SecurityAnalysisContext extends DynaWaltzContext {

    //TODO discuss start time
    private static final double START_TIME = 10.0;
    private final List<ContingencyEventModels> contingencyEventModels;

    public SecurityAnalysisContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels, List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters, List<Contingency> contingencies) {
        super(network, workingVariantId, dynamicModels, eventModels, curves, parameters, dynaWaltzParameters);
        this.contingencyEventModels = contingencies.stream()
                .map(c -> {
                    List<BlackBoxModel> contEventModels = c.getElements().stream()
                            .map(this::createContingencyEventModel)
                            .collect(Collectors.toList());
                    Map<String, MacroConnector> macroConnectorsMap = new HashMap<>();
                    List<MacroConnect> macroConnects = new ArrayList<>();
                    macroConnectionsAdder.setMacroConnectorsMap(macroConnectorsMap);
                    macroConnectionsAdder.setMacroConnectList(macroConnects);
                    for (BlackBoxModel bbm : contEventModels) {
                        bbm.createMacroConnections(macroConnectionsAdder);
                    }
                    return new ContingencyEventModels(c, contEventModels, macroConnectorsMap, macroConnects);
                })
                .collect(Collectors.toList());
    }

    private BlackBoxModel createContingencyEventModel(ContingencyElement element) {
        switch (element.getType()) {
            case GENERATOR:
                return new EventInjectionDisconnection(network.getGenerator(element.getId()), START_TIME);
            case LOAD:
                return new EventInjectionDisconnection(network.getLoad(element.getId()), START_TIME);
            case LINE:
            case TWO_WINDINGS_TRANSFORMER:
                return new EventQuadripoleDisconnection(network.getBranch(element.getId()), START_TIME);
            default:
                throw new PowsyblException("Contingency element " + element.getType() + " not supported");
        }
    }

    public List<ContingencyEventModels> getContingencyEventModels() {
        return contingencyEventModels;
    }
}
