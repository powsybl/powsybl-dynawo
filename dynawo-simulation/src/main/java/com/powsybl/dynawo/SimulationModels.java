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
import com.powsybl.dynawo.xml.DydDataSupplier;
import com.powsybl.dynawo.xml.MacroStaticReference;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class SimulationModels implements DydDataSupplier {

    private final List<BlackBoxModel> dynamicModels;
    private final List<BlackBoxModel> eventModels;
    private final Map<String, MacroStaticReference> macroStaticReferences;
    private final List<MacroConnect> macroConnectList;
    private final Map<String, MacroConnector> macroConnectorsMap;

    public static SimulationModels createFrom(BlackBoxModelSupplier bbmSupplier, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                                              Consumer<ParametersSet> parametersAdder, ParametersSet networkParameters,
                                              ReportNode reportNode) {

        List<MacroConnect> macroConnectList = new ArrayList<>();
        Map<String, MacroConnector> macroConnectorsMap = new LinkedHashMap<>();
        Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
        MacroConnectionsAdder adder = new MacroConnectionsAdder(bbmSupplier::getStaticIdBlackBoxModel,
                bbmSupplier::getPureDynamicModel, macroConnectList::add, macroConnectorsMap::computeIfAbsent, reportNode);
        // Write macro connection
        for (BlackBoxModel bbm : dynamicModels) {
            macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));
            bbm.createMacroConnections(adder);
            bbm.createDynamicModelParameters(parametersAdder);
            bbm.createDynamicModelInfoExtension();
        }
        for (BlackBoxModel bbem : eventModels) {
            bbem.createMacroConnections(adder);
            bbem.createDynamicModelParameters(parametersAdder);
            bbem.createNetworkParameter(networkParameters);
        }

        return new SimulationModels(dynamicModels, eventModels, macroConnectList,
                macroConnectorsMap, macroStaticReferences);
    }

    private SimulationModels(List<BlackBoxModel> dynamicModels,
                             List<BlackBoxModel> eventModels,
                             List<MacroConnect> macroConnectList,
                             Map<String, MacroConnector> macroConnectorsMap,
                             Map<String, MacroStaticReference> macroStaticReferences) {
        this.dynamicModels = dynamicModels;
        this.eventModels = eventModels;
        this.macroConnectList = macroConnectList;
        this.macroConnectorsMap = macroConnectorsMap;
        this.macroStaticReferences = macroStaticReferences;
    }

    @Override
    public List<BlackBoxModel> getBlackBoxDynamicModels() {
        return dynamicModels;
    }

    @Override
    public List<BlackBoxModel> getBlackBoxEventModels() {
        return eventModels;
    }

    @Override
    public Collection<MacroConnector> getMacroConnectors() {
        return macroConnectorsMap.values();
    }

    public boolean hasMacroConnector(String name) {
        return macroConnectorsMap.containsKey(name);
    }

    @Override
    public Collection<MacroStaticReference> getMacroStaticReferences() {
        return macroStaticReferences.values();
    }

    public boolean hasMacroStaticReference(BlackBoxModel bbm) {
        return macroStaticReferences.containsKey(bbm.getName());
    }

    @Override
    public List<MacroConnect> getMacroConnectList() {
        return macroConnectList;
    }
}
