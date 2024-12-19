/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadVariationAreaAutomationSystem;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.algorithms.ContingencyEventModels;
import com.powsybl.dynawo.algorithms.ContingencyEventModelsFactory;
import com.powsybl.dynawo.xml.DydDataSupplier;
import com.powsybl.iidm.network.Network;

import java.util.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class MarginCalculationContext extends DynawoSimulationContext {

    private final MarginCalculationParameters marginCalculationParameters;
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
        this(network, workingVariantId, dynamicModels, parameters, dynawoSimulationParameters, contingencies, loadsVariations, ReportNode.NO_OP);
    }

    public MarginCalculationContext(Network network, String workingVariantId,
                                    List<BlackBoxModel> dynamicModels,
                                    MarginCalculationParameters parameters,
                                    DynawoSimulationParameters dynawoSimulationParameters,
                                    List<Contingency> contingencies,
                                    List<LoadsVariation> loadsVariations,
                                    ReportNode reportNode) {
        super(network, workingVariantId, dynamicModels, List.of(), Collections.emptyList(),
                //TODO fix
                //TODO calculate phase 2 predicate - hande dyna version
                new DynamicSimulationParameters(parameters.getStartTime(), parameters.getStopTime()), dynawoSimulationParameters, null, DynawoConstants.VERSION_MIN, reportNode);
        this.marginCalculationParameters = parameters;
        double contingenciesStartTime = parameters.getContingenciesStartTime();
        this.contingencyEventModels = ContingencyEventModelsFactory.createFrom(contingencies, this, macroConnectionsAdder, contingenciesStartTime);
        this.loadVariationArea = new LoadVariationAreaAutomationSystem(loadsVariations, parameters.getLoadIncreaseStartTime(), parameters.getLoadIncreaseStopTime());

        macroConnectionsAdder.setMacroConnectorAdder(loadVariationMacroConnectorsMap::computeIfAbsent);
        macroConnectionsAdder.setMacroConnectAdder(loadVariationMacroConnectList::add);
        loadVariationArea.createMacroConnections(macroConnectionsAdder);
        loadVariationArea.createDynamicModelParameters(this, getDynamicModelsParameters()::add);
    }

    private static void splitDynamicModels(List<BlackBoxModel> dynamicModels) {
        //TODO
    }

    public MarginCalculationParameters getMarginCalculationParameters() {
        return marginCalculationParameters;
    }

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
