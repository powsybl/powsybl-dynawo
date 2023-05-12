/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.*;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Identifiable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class MacroConnectionsAdder {

    private final DynaWaltzContext context;
    private List<MacroConnect> macroConnectList;
    private Map<String, MacroConnector> macroConnectorsMap;

    public MacroConnectionsAdder(DynaWaltzContext context, List<MacroConnect> macroConnectList, Map<String, MacroConnector> macroConnectorsMap) {
        this.context = context;
        this.macroConnectList = macroConnectList;
        this.macroConnectorsMap = macroConnectorsMap;
    }

    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, String modelStaticId, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName());
        MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
        addMacroConnections(connectedModel, macroConnectorId, mc, varConnectionsSupplier);
    }

    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, T connectedModel, List<VarConnection> varConnections, MacroConnectAttribute... connectFromAttributes) {
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName());
        List<MacroConnectAttribute> fromAttributes = Stream.concat(originModel.getMacroConnectFromAttributes().stream(), Arrays.stream(connectFromAttributes)).collect(Collectors.toList());
        MacroConnect mc = new MacroConnect(macroConnectorId, fromAttributes, connectedModel.getMacroConnectToAttributes());
        macroConnectList.add(mc);
        macroConnectorsMap.computeIfAbsent(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnections));
    }

    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, String modelStaticId, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, MacroConnectAttribute... connectFromAttributes) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        createMacroConnections(originModel, connectedModel, varConnectionsSupplier.apply(connectedModel), connectFromAttributes);
    }

    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, Identifiable<?> equipment, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier) {
        T connectedModel = context.getDynamicModel(equipment, modelClass);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName());
        MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
        addMacroConnections(connectedModel, macroConnectorId, mc, varConnectionsSupplier);
    }

    /**
     * Suffixes MacroConnector id with side name
     */
    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, String modelStaticId, Class<T> modelClass, BiFunction<T, Side, List<VarConnection>> varConnectionsSupplier, Side side) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName(), side);
        MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
        macroConnectList.add(mc);
        macroConnectorsMap.computeIfAbsent(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnectionsSupplier.apply(connectedModel, side)));
    }

    /**
     * Suffixes MacroConnector id with string
     */
    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, String modelStaticId, Class<T> modelClass, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, String parametrizedName) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName(), parametrizedName);
        MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
        addMacroConnections(connectedModel, macroConnectorId, mc, varConnectionsSupplier, parametrizedName);
    }

    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, String modelStaticId, Class<T> modelClass, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, String parametrizedName, MacroConnectAttribute... connectFromAttributes) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName(), parametrizedName);
        List<MacroConnectAttribute> fromAttributes = Stream.concat(originModel.getMacroConnectFromAttributes().stream(), Arrays.stream(connectFromAttributes)).collect(Collectors.toList());
        MacroConnect mc = new MacroConnect(macroConnectorId, fromAttributes, connectedModel.getMacroConnectToAttributes());
        addMacroConnections(connectedModel, macroConnectorId, mc, varConnectionsSupplier, parametrizedName);
    }

    private <T extends Model> void addMacroConnections(T connectedModel, String macroConnectorId, MacroConnect mc, Function<T, List<VarConnection>> varConnectionsSupplier) {
        macroConnectList.add(mc);
        macroConnectorsMap.computeIfAbsent(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnectionsSupplier.apply(connectedModel)));
    }

    private <T extends Model> void addMacroConnections(T connectedModel, String macroConnectorId, MacroConnect mc, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, String parametrizedName) {
        macroConnectList.add(mc);
        macroConnectorsMap.computeIfAbsent(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnectionsSupplier.apply(connectedModel, parametrizedName)));
    }

    public void checkLinkedDynamicModels(Bus bus) {
        bus.getConnectedTerminalStream()
                .map(t -> t.getConnectable().getId())
                .filter(context::isWithoutBlackBoxDynamicModel)
                .findAny()
                .ifPresent(id -> {
                    throw new PowsyblException(String.format("The equipment %s linked to the standard bus %s does not possess a dynamic model",
                            id, bus.getId()));
                });
    }

    public void setMacroConnectList(List<MacroConnect> macroConnectList) {
        this.macroConnectList = macroConnectList;
    }

    public void setMacroConnectorsMap(Map<String, MacroConnector> macroConnectorsMap) {
        this.macroConnectorsMap = macroConnectorsMap;
    }
}
