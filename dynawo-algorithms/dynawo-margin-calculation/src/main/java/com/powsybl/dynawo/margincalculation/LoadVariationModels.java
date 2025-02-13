/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadVariationAreaAutomationSystem;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.DydDataSupplier;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class LoadVariationModels implements DydDataSupplier {

    private final LoadVariationAreaAutomationSystem loadVariationArea;
    private final List<MacroConnect> macroConnectList;
    private final Map<String, MacroConnector> macroConnectorsMap;
    private final String parFileName;

    public static LoadVariationModels createFrom(BlackBoxModelSupplier bbmSupplier, LoadVariationAreaAutomationSystem loadVariationArea,
                                                 Consumer<ParametersSet> parametersAdder, ParametersSet networkParameters,
                                                 String simulationParFile, ReportNode reportNode) {
        List<MacroConnect> macroConnectList = new ArrayList<>();
        Map<String, MacroConnector> macroConnectorsMap = new LinkedHashMap<>();
        MacroConnectionsAdder adder = new MacroConnectionsAdder(bbmSupplier::getStaticIdBlackBoxModel,
                bbmSupplier::getPureDynamicModel, macroConnectList::add, macroConnectorsMap::computeIfAbsent, reportNode);
        loadVariationArea.createMacroConnections(adder);
        loadVariationArea.createDynamicModelParameters(parametersAdder);
        loadVariationArea.createNetworkParameter(networkParameters);

        return new LoadVariationModels(loadVariationArea, macroConnectList, macroConnectorsMap, simulationParFile);
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
