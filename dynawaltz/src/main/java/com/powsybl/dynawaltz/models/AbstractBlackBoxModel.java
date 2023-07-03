/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.models.buses.ConnectionPoint;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.dynawaltz.models.utils.SideConverter;
import com.powsybl.iidm.network.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractBlackBoxModel implements BlackBoxModel {

    private final String dynamicModelId;
    private final String parameterSetId;

    protected AbstractBlackBoxModel(String dynamicModelId, String parameterSetId) {
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    @Override
    public String getName() {
        return getLib();
    }

    public String getDynamicModelId() {
        return dynamicModelId;
    }

    public String getParameterSetId() {
        return parameterSetId;
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        // method empty by default to be redefined by specific models
    }

    protected List<MacroConnectAttribute> getMacroConnectFromAttributes() {
        return List.of(MacroConnectAttribute.of("id1", getDynamicModelId()));
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        return List.of(MacroConnectAttribute.of("id2", getDynamicModelId()));
    }

    @Override
    public String getParFile(DynaWaltzContext context) {
        return DynaWaltzParameters.MODELS_OUTPUT_PARAMETERS_FILE;
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return Collections.emptyList();
    }

    protected void writeDynamicAttributes(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", getParFile(context));
        writer.writeAttribute("parId", getParameterSetId());
    }

    protected <T extends Model> void createMacroConnections(String modelStaticId, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), varConnectionsSupplier.apply(connectedModel));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    protected final <T extends Model> void createMacroConnections(T connectedModel, List<VarConnection> varConnections, DynaWaltzContext context, MacroConnectAttribute... connectFromAttributes) {
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), varConnections);
        List<MacroConnectAttribute> fromAttributes = Stream.concat(getMacroConnectFromAttributes().stream(), Arrays.stream(connectFromAttributes)).collect(Collectors.toList());
        context.addMacroConnect(macroConnectorId, fromAttributes, connectedModel.getMacroConnectToAttributes());
    }

    protected final <T extends Model> void createMacroConnections(String modelStaticId, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, MacroConnectAttribute... connectFromAttributes) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        createMacroConnections(connectedModel, varConnectionsSupplier.apply(connectedModel), context, connectFromAttributes);
    }

    protected final <T extends Model> void createMacroConnections(Identifiable<?> equipment, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, MacroConnectAttribute... connectFromAttributes) {
        T connectedModel = context.getDynamicModel(equipment, modelClass);
        createMacroConnections(connectedModel, varConnectionsSupplier.apply(connectedModel), context, connectFromAttributes);
    }

    protected <T extends Model> void createMacroConnections(Identifiable<?> equipment, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        T connectedModel = context.getDynamicModel(equipment, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), varConnectionsSupplier.apply(connectedModel));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    /**
     * Suffixes MacroConnector id with side name
     */
    protected final <T extends Model> void createMacroConnections(String modelStaticId, Class<T> modelClass, BiFunction<T, Side, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, Side side) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), side, varConnectionsSupplier.apply(connectedModel, side));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    protected final <T extends Model> void createMacroConnections(Identifiable<?> equipment, Class<T> modelClass, BiFunction<T, Side, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, Side side) {
        T connectedModel = context.getDynamicModel(equipment, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), side, varConnectionsSupplier.apply(connectedModel, side));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    /**
     * Suffixes MacroConnector id with string
     */
    protected <T extends Model> void createMacroConnections(Identifiable<?> equipment, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, String parametrizedName) {
        T connectedModel = context.getDynamicModel(equipment, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), parametrizedName, varConnectionsSupplier.apply(connectedModel));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    protected <T extends Model> void createMacroConnections(Identifiable<?> equipment, Class<T> modelClass, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, MacroConnectionSuffix suffix) {
        T connectedModel = context.getDynamicModel(equipment, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), suffix.getIdSuffix(), varConnectionsSupplier.apply(connectedModel, suffix.getConnectionSuffix()));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    protected <T extends Model> void createPureDynamicMacroConnections(String dynamicId, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        T connectedModel = context.getPureDynamicModel(dynamicId, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), varConnectionsSupplier.apply(connectedModel));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    protected final <T extends Model> void createMacroConnections(Identifiable<?> equipment, Class<T> modelClass, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, String parametrizedName, MacroConnectAttribute... connectFromAttributes) {
        T connectedModel = context.getDynamicModel(equipment, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), parametrizedName, varConnectionsSupplier.apply(connectedModel, parametrizedName));
        List<MacroConnectAttribute> fromAttributes = Stream.concat(getMacroConnectFromAttributes().stream(), Arrays.stream(connectFromAttributes)).collect(Collectors.toList());
        context.addMacroConnect(macroConnectorId, fromAttributes, connectedModel.getMacroConnectToAttributes());
    }

    protected void createTerminalMacroConnections(Bus bus, Function<ConnectionPoint, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        ConnectionPoint connectedModel = context.getConnectionPointDynamicModel(bus.getId());
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), varConnectionsSupplier.apply(connectedModel));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    protected void createTerminalMacroConnections(Injection<?> equipment, Function<ConnectionPoint, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        ConnectionPoint connectedModel = context.getConnectionPointDynamicModel(BusUtils.getConnectableBusStaticId(equipment));
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), varConnectionsSupplier.apply(connectedModel));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    protected final void createTerminalMacroConnections(Terminal terminal, BiFunction<ConnectionPoint, Side, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, Side side) {
        ConnectionPoint connectedModel = context.getConnectionPointDynamicModel(BusUtils.getConnectableBusStaticId(terminal));
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), side, varConnectionsSupplier.apply(connectedModel, side));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    protected final void createTerminalMacroConnections(HvdcLine hvdc, BiFunction<ConnectionPoint, Side, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, Side side) {
        HvdcConverterStation<?> station = hvdc.getConverterStation(SideConverter.convert(side));
        createTerminalMacroConnections(station.getTerminal(), varConnectionsSupplier, context, side);
    }
}
