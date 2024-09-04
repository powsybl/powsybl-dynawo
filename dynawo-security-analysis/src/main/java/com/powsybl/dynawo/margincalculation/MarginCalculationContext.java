/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.algorithms.DynawoAlgorithmsContext;
import com.powsybl.dynawo.xml.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.security.ContingencyEventModels;
import com.powsybl.dynawo.security.ContingencyEventModelsHandler;
import com.powsybl.dynawo.xml.DydDataSupplier;
import com.powsybl.iidm.network.Network;

import java.util.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class MarginCalculationContext extends DynawoSimulationContext implements DynawoAlgorithmsContext {

    private final MarginCalculationParameters marginCalculationParameters;
    private final List<Contingency> contingencies;
    private final List<ContingencyEventModels> contingencyEventModels;
    private final LoadVariationAreaAutomationSystem loadVariationArea;
    private final List<MacroConnect> loadVariationMacroConnectList = new ArrayList<>();
    private final Map<String, MacroConnector> loadVariationMacroConnectorsMap = new LinkedHashMap<>();

    public MarginCalculationContext(Network network, String workingVariantId,
                                    List<BlackBoxModel> dynamicModels,
                                    MarginCalculationParameters parameters,
                                    DynawoSimulationParameters dynawoSimulationParameters,
                                    List<Contingency> contingencies,
                                    List<LoadsVariation> loadsVariations) {
        super(network, workingVariantId, dynamicModels, List.of(), Collections.emptyList(),
                //TODO fix
                new DynamicSimulationParameters(parameters.getStartTime(), parameters.getStopTime()), dynawoSimulationParameters);
        this.marginCalculationParameters = parameters;
        double contingenciesStartTime = parameters.getContingenciesStartTime();

        this.contingencies = contingencies;
        this.contingencyEventModels = ContingencyEventModelsHandler.createFrom(contingencies, this, contingenciesStartTime).getContingencyEventModels();
        this.loadVariationArea = new LoadVariationAreaAutomationSystem(loadsVariations, parameters.getLoadIncreaseStartTime(), parameters.getLoadIncreaseStopTime());

        macroConnectionsAdder.setMacroConnectorAdder(loadVariationMacroConnectorsMap::computeIfAbsent);
        macroConnectionsAdder.setMacroConnectAdder(loadVariationMacroConnectList::add);
        loadVariationArea.createMacroConnections(macroConnectionsAdder);
        loadVariationArea.createDynamicModelParameters(this, getDynamicModelsParameters()::add);
    }

    private static void splitDynamicModels(List<BlackBoxModel> dynamicModels) {

    }

    public MarginCalculationParameters getMarginCalculationParameters() {
        return marginCalculationParameters;
    }

    public List<Contingency> getContingencies() {
        return contingencies;
    }

    @Override
    public List<ContingencyEventModels> getContingencyEventModels() {
        return contingencyEventModels;
    }

    public DydDataSupplier getLoadVariationAreaDydData() {
        return new DydDataSupplier() {

            @Override
            public List<BlackBoxModel> getBlackBoxDynamicModels() {
                return List.of(loadVariationArea);
            }

            @Override
            public Collection<MacroConnector> getMacroConnectors() {
                return loadVariationMacroConnectorsMap.values();
            }

            @Override
            public List<MacroConnect> getMacroConnectList() {
                return loadVariationMacroConnectList;
            }

            @Override
            public String getParFileName() {
                return DynawoSimulationConstants.getSimulationParFile(getNetwork());
            }
        };
    }
}
