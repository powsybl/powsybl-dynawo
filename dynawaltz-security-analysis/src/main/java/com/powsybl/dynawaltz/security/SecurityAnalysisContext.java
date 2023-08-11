/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.security;

import com.powsybl.commons.PowsyblException;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.ContingencyElement;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.MacroConnect;
import com.powsybl.dynawaltz.models.MacroConnector;
import com.powsybl.dynawaltz.models.events.EventInjectionDisconnection;
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection;
import com.powsybl.dynawaltz.security.xml.ContingenciesParXml;
import com.powsybl.iidm.network.Network;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class SecurityAnalysisContext extends DynaWaltzContext {

    private final List<ContingencyEventModels> contingencyEventModels;
    private final DynamicSecurityAnalysisParameters.DynamicContingenciesParameters dynamicContingenciesParameters;

    public SecurityAnalysisContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                                   DynamicSecurityAnalysisParameters parameters, DynaWaltzParameters dynaWaltzParameters, List<Contingency> contingencies) {

        super(network, workingVariantId, dynamicModels, eventModels, Collections.emptyList(), parameters.getDynamicSimulationParameters(), dynaWaltzParameters);
        this.dynamicContingenciesParameters = parameters.getDynamicContingenciesParameters();
        this.contingencyEventModels = contingencies.stream()
                .map(c -> {
                    List<BlackBoxModel> contEventModels = c.getElements().stream()
                            .map(ce -> this.createContingencyEventModel(ce, ContingenciesParXml.createParFileName(c)))
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

    private BlackBoxModel createContingencyEventModel(ContingencyElement element, String parFileName) {
        switch (element.getType()) {
            case GENERATOR:
                return new EventInjectionDisconnection(network.getGenerator(element.getId()), dynamicContingenciesParameters.getContingenciesStartTime(), parFileName);
            case LOAD:
                return new EventInjectionDisconnection(network.getLoad(element.getId()), dynamicContingenciesParameters.getContingenciesStartTime(), parFileName);
            case LINE:
            case TWO_WINDINGS_TRANSFORMER:
                return new EventQuadripoleDisconnection(network.getBranch(element.getId()), dynamicContingenciesParameters.getContingenciesStartTime(), parFileName);
            default:
                throw new PowsyblException("Contingency element " + element.getType() + " not supported");
        }
    }

    public List<ContingencyEventModels> getContingencyEventModels() {
        return contingencyEventModels;
    }
}
