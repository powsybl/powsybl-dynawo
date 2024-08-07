/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.macroconnections;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.models.automationsystems.ConnectionStatefulModel;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.iidm.network.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class MacroConnectionsAdder {

    @FunctionalInterface
    public interface DynamicModelGetter {
        <T extends Model> T getDynamicModel(Identifiable<?> equipment, Class<T> connectableClass, boolean throwException);
    }

    @FunctionalInterface
    public interface PureDynamicModelGetter {
        <T extends Model> T getPureDynamicModel(String dynamicId, Class<T> connectableClass, boolean throwException);
    }

    private final ReportNode reportNode;
    private final DynamicModelGetter dynamicModelGetter;
    private final PureDynamicModelGetter pureDynamicModelGetter;
    private Consumer<MacroConnect> macroConnectAdder;
    private BiConsumer<String, Function<String, MacroConnector>> macroConnectorAdder;

    public MacroConnectionsAdder(DynamicModelGetter dynamicModelGetter, PureDynamicModelGetter pureDynamicModelGetter, Consumer<MacroConnect> macroConnectAdder,
                                 BiConsumer<String, Function<String, MacroConnector>> macroConnectorAdder, ReportNode reportNode) {
        this.dynamicModelGetter = dynamicModelGetter;
        this.pureDynamicModelGetter = pureDynamicModelGetter;
        this.macroConnectAdder = macroConnectAdder;
        this.macroConnectorAdder = macroConnectorAdder;
        this.reportNode = reportNode;
    }

    /**
     * Creates macro connection from equipment and model class
     */
    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, Identifiable<?> equipment, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier) {
        T connectedModel = dynamicModelGetter.getDynamicModel(equipment, modelClass, true);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName());
        MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
        addMacroConnections(connectedModel, macroConnectorId, mc, varConnectionsSupplier);
    }

    /**
     * Creates macro connection from equipment and model class
     * Skip the instantiation is the equipment does not correspond to the model
     */
    public <T extends Model> boolean createMacroConnectionsOrSkip(BlackBoxModel originModel, Identifiable<?> equipment, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier) {
        T connectedModel = dynamicModelGetter.getDynamicModel(equipment, modelClass, false);
        if (connectedModel != null) {
            String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName());
            MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
            addMacroConnections(connectedModel, macroConnectorId, mc, varConnectionsSupplier);
            return false;
        }
        return true;
    }

    /**
     * Creates macro connection from model classes
     */
    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, T connectedModel, List<VarConnection> varConnections, MacroConnectAttribute... connectFromAttributes) {
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName());
        List<MacroConnectAttribute> fromAttributes = Stream.concat(originModel.getMacroConnectFromAttributes().stream(), Arrays.stream(connectFromAttributes)).collect(Collectors.toList());
        MacroConnect mc = new MacroConnect(macroConnectorId, fromAttributes, connectedModel.getMacroConnectToAttributes());
        macroConnectAdder.accept(mc);
        macroConnectorAdder.accept(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnections));
        macroConnectorAdder.accept(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnections));
    }

    /**
     * Creates macro connection from equipment and model class
     * Add MacroConnectAttribute "from" attributes
     */
    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, Identifiable<?> equipment, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, MacroConnectAttribute... connectFromAttributes) {
        T connectedModel = dynamicModelGetter.getDynamicModel(equipment, modelClass, true);
        createMacroConnections(originModel, connectedModel, varConnectionsSupplier.apply(connectedModel), connectFromAttributes);
    }

    /**
     * Creates macro connection from equipment and model class
     * Skip the instantiation is the equipment does not correspond to the model
     * Add MacroConnectAttribute "from" attributes
     */
    public <T extends Model> boolean createMacroConnectionsOrSkip(BlackBoxModel originModel, Identifiable<?> equipment, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, MacroConnectAttribute... connectFromAttributes) {
        T connectedModel = dynamicModelGetter.getDynamicModel(equipment, modelClass, false);
        if (connectedModel != null) {
            createMacroConnections(originModel, connectedModel, varConnectionsSupplier.apply(connectedModel), connectFromAttributes);
            return false;
        }
        return true;
    }

    /**
     * Creates macro connection from equipment and model class
     * Suffixes MacroConnector id with side name
     */
    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, Identifiable<?> equipment, Class<T> modelClass, BiFunction<T, TwoSides, List<VarConnection>> varConnectionsSupplier, TwoSides side) {
        T connectedModel = dynamicModelGetter.getDynamicModel(equipment, modelClass, true);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName(), side);
        MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
        macroConnectAdder.accept(mc);
        macroConnectorAdder.accept(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnectionsSupplier.apply(connectedModel, side)));
    }

    /**
     * Creates macro connection from equipment and model class
     * Suffixes MacroConnector id with string
     */
    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, Identifiable<?> equipment, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, String parametrizedName) {
        T connectedModel = dynamicModelGetter.getDynamicModel(equipment, modelClass, true);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName(), parametrizedName);
        MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
        addMacroConnections(connectedModel, macroConnectorId, mc, varConnectionsSupplier);
    }

    /**
     * Creates macro connection from equipment and model class
     * Suffixes MacroConnector id and connection with MacroConnectionSuffix (different id and connection suffix)
     */
    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, Identifiable<?> equipment, Class<T> modelClass, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, MacroConnectionSuffix suffix) {
        T connectedModel = dynamicModelGetter.getDynamicModel(equipment, modelClass, true);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName(), suffix.getIdSuffix());
        MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
        addMacroConnections(connectedModel, macroConnectorId, mc, varConnectionsSupplier, suffix.getConnectionSuffix());
    }

    /**
     * Creates macro connection from equipment and model class
     * Add MacroConnectAttribute "from" attributes
     * Use a parametrized macro connector name
     */
    public <T extends Model> void createMacroConnections(BlackBoxModel originModel, Identifiable<?> equipment, Class<T> modelClass, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, String parametrizedName, MacroConnectAttribute... connectFromAttributes) {
        T connectedModel = dynamicModelGetter.getDynamicModel(equipment, modelClass, true);
        String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName(), parametrizedName);
        List<MacroConnectAttribute> fromAttributes = Stream.concat(originModel.getMacroConnectFromAttributes().stream(), Arrays.stream(connectFromAttributes)).collect(Collectors.toList());
        MacroConnect mc = new MacroConnect(macroConnectorId, fromAttributes, connectedModel.getMacroConnectToAttributes());
        addMacroConnections(connectedModel, macroConnectorId, mc, varConnectionsSupplier, parametrizedName);
    }

    /**
     * Creates macro connection with a bus from a terminal
     */
    public void createTerminalMacroConnections(BlackBoxModel originModel, Terminal terminal, Function<EquipmentConnectionPoint, List<VarConnection>> varConnectionsSupplier) {
        createMacroConnections(originModel, BusUtils.getConnectableBus(terminal), EquipmentConnectionPoint.class, varConnectionsSupplier);
    }

    /**
     * Creates macro connection with a bus from a terminal
     * Suffixes MacroConnector id with side name
     */
    public void createTerminalMacroConnections(BlackBoxModel originModel, Terminal terminal, BiFunction<EquipmentConnectionPoint, TwoSides, List<VarConnection>> varConnectionsSupplier, TwoSides side) {
        createMacroConnections(originModel, BusUtils.getConnectableBus(terminal), EquipmentConnectionPoint.class, varConnectionsSupplier, side);
    }

    /**
     * Creates macro connection with a bus from an HVDC
     * Suffixes MacroConnector id with side name
     */
    public void createTerminalMacroConnections(BlackBoxModel originModel, HvdcLine hvdc, BiFunction<EquipmentConnectionPoint, TwoSides, List<VarConnection>> varConnectionsSupplier, TwoSides side) {
        HvdcConverterStation<?> station = hvdc.getConverterStation(side);
        createTerminalMacroConnections(originModel, station.getTerminal(), varConnectionsSupplier, side);
    }

    /**
     * Creates macro connection with pure dynamic model from dynamic id
     */
    public <T extends Model & ConnectionStatefulModel> boolean createMacroConnectionsOrSkip(BlackBoxModel originModel, String dynamicModelId, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier) {
        T connectedModel = pureDynamicModelGetter.getPureDynamicModel(dynamicModelId, modelClass, false);
        if (connectedModel != null && connectedModel.connect(this)) {
            String macroConnectorId = MacroConnector.createMacroConnectorId(originModel.getName(), connectedModel.getName());
            MacroConnect mc = new MacroConnect(macroConnectorId, originModel.getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
            macroConnectAdder.accept(mc);
            macroConnectorAdder.accept(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnectionsSupplier.apply(connectedModel)));
            return false;
        }
        return true;
    }

    /**
     * Verifies if a connection can be created with the specified equipment/model combination without creating macro connections
     */
    public <T extends Model> boolean checkMacroConnections(Identifiable<?> equipment, Class<T> modelClass) {
        return dynamicModelGetter.getDynamicModel(equipment, modelClass, false) != null;
    }

    private <T extends Model> void addMacroConnections(T connectedModel, String macroConnectorId, MacroConnect mc, Function<T, List<VarConnection>> varConnectionsSupplier) {
        macroConnectAdder.accept(mc);
        macroConnectorAdder.accept(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnectionsSupplier.apply(connectedModel)));
    }

    private <T extends Model> void addMacroConnections(T connectedModel, String macroConnectorId, MacroConnect mc, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, String parametrizedName) {
        macroConnectAdder.accept(mc);
        macroConnectorAdder.accept(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnectionsSupplier.apply(connectedModel, parametrizedName)));
    }

    public void setMacroConnectAdder(Consumer<MacroConnect> macroConnectAdder) {
        this.macroConnectAdder = macroConnectAdder;
    }

    public void setMacroConnectorAdder(BiConsumer<String, Function<String, MacroConnector>> macroConnectorAdder) {
        this.macroConnectorAdder = macroConnectorAdder;
    }

    public ReportNode getReportNode() {
        return reportNode;
    }
}
