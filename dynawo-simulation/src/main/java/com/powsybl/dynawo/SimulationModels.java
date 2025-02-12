/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.models.events.ContextDependentEvent;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.DydDataSupplier;
import com.powsybl.dynawo.xml.MacroStaticReference;
import com.powsybl.iidm.network.Identifiable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class SimulationModels implements DydDataSupplier, BlackBoxModelSupplier {

    private final List<BlackBoxModel> dynamicModels;
    private final List<BlackBoxModel> eventModels;
    private final Map<String, MacroStaticReference> macroStaticReferences;
    private final List<MacroConnect> macroConnectList;
    private final Map<String, MacroConnector> macroConnectorsMap;
    //TODO store it in the builder instead ?
    private final Map<String, EquipmentBlackBoxModel> staticIdBlackBoxModelMap;
    private final Map<String, BlackBoxModel> pureDynamicModelMap;

    public static SimulationModels createFrom(List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                                              Consumer<ParametersSet> parametersAdder, ParametersSet networkParameters,
                                              ReportNode reportNode) {

        Map<String, EquipmentBlackBoxModel> staticIdBlackBoxModelMap = dynamicModels.stream()
                .filter(EquipmentBlackBoxModel.class::isInstance)
                .map(EquipmentBlackBoxModel.class::cast)
                .collect(Collectors.toMap(EquipmentBlackBoxModel::getStaticId, Function.identity()));
        Map<String, BlackBoxModel> pureDynamicModelMap = dynamicModels.stream()
                .filter(AbstractPureDynamicBlackBoxModel.class::isInstance)
                .collect(Collectors.toMap(BlackBoxModel::getDynamicModelId, Function.identity()));

        // Late init on ContextDependentEvents
        eventModels.stream()
                .filter(ContextDependentEvent.class::isInstance)
                .map(ContextDependentEvent.class::cast)
                .forEach(e -> e.setEquipmentHasDynamicModel(staticIdBlackBoxModelMap.containsKey(e.getEquipment().getId())));

        List<MacroConnect> macroConnectList = new ArrayList<>();
        Map<String, MacroConnector> macroConnectorsMap = new LinkedHashMap<>();
        Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
        MacroConnectionsAdder adder = new MacroConnectionsAdder(staticIdBlackBoxModelMap::get, pureDynamicModelMap::get,
                macroConnectList::add, macroConnectorsMap::computeIfAbsent, reportNode);
        // Write macro connection
        for (BlackBoxModel bbm : dynamicModels) {
            macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));
            bbm.createMacroConnections(adder);
            bbm.createDynamicModelParameters(parametersAdder);
        }
        for (BlackBoxModel bbem : eventModels) {
            bbem.createMacroConnections(adder);
            bbem.createDynamicModelParameters(parametersAdder);
            bbem.createNetworkParameter(networkParameters);
        }

        return new SimulationModels(dynamicModels, eventModels, macroConnectList,
                macroConnectorsMap, macroStaticReferences, staticIdBlackBoxModelMap, pureDynamicModelMap);
    }

    private SimulationModels(List<BlackBoxModel> dynamicModels,
                             List<BlackBoxModel> eventModels,
                             List<MacroConnect> macroConnectList,
                             Map<String, MacroConnector> macroConnectorsMap,
                             Map<String, MacroStaticReference> macroStaticReferences,
                             Map<String, EquipmentBlackBoxModel> staticIdBlackBoxModelMap,
                             Map<String, BlackBoxModel> pureDynamicModelMap) {
        this.dynamicModels = dynamicModels;
        this.eventModels = eventModels;
        this.macroConnectList = macroConnectList;
        this.macroConnectorsMap = macroConnectorsMap;
        this.macroStaticReferences = macroStaticReferences;
        this.staticIdBlackBoxModelMap = staticIdBlackBoxModelMap;
        this.pureDynamicModelMap = pureDynamicModelMap;
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

    public EquipmentBlackBoxModel getStaticIdBlackBoxModel(String id) {
        return staticIdBlackBoxModelMap.get(id);
    }

    public BlackBoxModel getPureDynamicModel(String id) {
        return pureDynamicModelMap.get(id);
    }

    @Override
    public boolean hasDynamicModel(Identifiable<?> equipment) {
        return staticIdBlackBoxModelMap.containsKey(equipment.getId());
    }
}
