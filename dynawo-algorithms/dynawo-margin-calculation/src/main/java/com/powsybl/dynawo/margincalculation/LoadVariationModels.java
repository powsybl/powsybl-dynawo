/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadVariationAreaAutomationSystem;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.xml.DydDataSupplier;

import java.util.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadVariationModels implements DydDataSupplier {

    private final LoadVariationAreaAutomationSystem loadVariationArea;
    private final List<MacroConnect> macroConnectList;
    private final Map<String, MacroConnector> macroConnectorsMap;
    private final String parFileName;

    public static LoadVariationModels createFrom(MarginCalculationContext context, LoadVariationAreaAutomationSystem loadVariationArea) {
        List<MacroConnect> macroConnectList = new ArrayList<>();
        Map<String, MacroConnector> macroConnectorsMap = new LinkedHashMap<>();
        MacroConnectionsAdder adder = MacroConnectionsAdder.createFrom(context,
                macroConnectList::add,
                macroConnectorsMap::computeIfAbsent);
        loadVariationArea.createMacroConnections(adder);
        loadVariationArea.createDynamicModelParameters(context.getDynamicModelsParameters()::add);
        loadVariationArea.createNetworkParameter(context.getDynawoSimulationParameters().getNetworkParameters());

        return new LoadVariationModels(loadVariationArea, macroConnectList, macroConnectorsMap,
                DynawoSimulationConstants.getSimulationParFile(context.getNetwork()));
    }

    private LoadVariationModels(LoadVariationAreaAutomationSystem loadVariationArea,
                                List<MacroConnect> macroConnectList,
                                Map<String, MacroConnector> macroConnectorsMap,
                                String parFileName) {
        this.loadVariationArea = loadVariationArea;
        this.macroConnectList = macroConnectList;
        this.macroConnectorsMap = macroConnectorsMap;
        this.parFileName = parFileName;
    }

    @Override
    public List<BlackBoxModel> getBlackBoxDynamicModels() {
        return List.of(loadVariationArea);
    }

    @Override
    public Collection<MacroConnector> getMacroConnectors() {
        return macroConnectorsMap.values();
    }

    @Override
    public List<MacroConnect> getMacroConnectList() {
        return macroConnectList;
    }

    @Override
    public String getParFileName() {
        return parFileName;
    }
}
