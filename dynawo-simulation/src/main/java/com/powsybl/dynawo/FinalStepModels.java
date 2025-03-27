/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.DynawoData;
import com.powsybl.dynawo.xml.MacroStaticReference;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class FinalStepModels implements DynawoData {

    private final List<BlackBoxModel> dynamicModels;
    private final Map<String, MacroStaticReference> macroStaticReferences;
    private final List<MacroConnect> macroConnectList;
    private final Map<String, MacroConnector> macroConnectorsMap;

    public static FinalStepModels createFrom(BlackBoxModelSupplier blackBoxModelSupplier, SimulationModels simulationModels,
                                             List<BlackBoxModel> dynamicModels, Consumer<ParametersSet> parametersAdder, ReportNode reportNode) {
        BlackBoxModelSupplier finalStepBbmSupplier = BlackBoxModelSupplier.createFrom(blackBoxModelSupplier, dynamicModels);
        Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
        List<MacroConnect> macroConnectList = new ArrayList<>();
        Map<String, MacroConnector> macroConnectorsMap = new LinkedHashMap<>();
        MacroConnectionsAdder macroConnectionsAdder = new MacroConnectionsAdder(
                finalStepBbmSupplier::getStaticIdBlackBoxModel,
                finalStepBbmSupplier::getPureDynamicModel,
                macroConnectList::add,
                (n, f) -> {
                    if (!simulationModels.hasMacroConnector(n)) {
                        macroConnectorsMap.computeIfAbsent(n, f);
                    }
                },
                reportNode);
        // Write macro connection
        for (BlackBoxModel bbm : dynamicModels) {
            if (!simulationModels.hasMacroStaticReference(bbm)) {
                macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));
            }
            bbm.createMacroConnections(macroConnectionsAdder);
            bbm.createDynamicModelParameters(parametersAdder);
        }
        return new FinalStepModels(dynamicModels, macroConnectList, macroConnectorsMap, macroStaticReferences);
    }

    private FinalStepModels(List<BlackBoxModel> dynamicModels,
                            List<MacroConnect> macroConnectList,
                            Map<String, MacroConnector> macroConnectorsMap,
                            Map<String, MacroStaticReference> macroStaticReferences) {
        this.dynamicModels = dynamicModels;
        this.macroConnectList = macroConnectList;
        this.macroConnectorsMap = macroConnectorsMap;
        this.macroStaticReferences = macroStaticReferences;
    }

    @Override
    public List<BlackBoxModel> getBlackBoxDynamicModels() {
        return dynamicModels;
    }

    @Override
    public Collection<MacroConnector> getMacroConnectors() {
        return macroConnectorsMap.values();
    }

    @Override
    public Collection<MacroStaticReference> getMacroStaticReferences() {
        return macroStaticReferences.values();
    }

    @Override
    public List<MacroConnect> getMacroConnectList() {
        return macroConnectList;
    }
}
