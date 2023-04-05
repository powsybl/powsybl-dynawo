/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import com.powsybl.iidm.network.IdentifiableType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractBlackBoxModel implements BlackBoxModel {

    private final String dynamicModelId;
    private final String staticId;
    private final String parameterSetId;

    protected AbstractBlackBoxModel(String dynamicModelId, String staticId, String parameterSetId) {
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
        this.staticId = staticId;
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    public Optional<String> getStaticId() {
        return Optional.ofNullable(staticId);
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
        return context.getParFile();
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

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        boolean hasVarMapping = !getVarsMapping().isEmpty();
        if (hasVarMapping) {
            writer.writeStartElement(DYN_URI, "blackBoxModel");
        } else {
            writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        }
        writeDynamicAttributes(writer, context);
        writer.writeAttribute("staticId", getStaticId().orElseThrow());
        if (hasVarMapping) {
            MacroStaticReference.writeMacroStaticRef(writer, getLib());
            writer.writeEndElement();
        }
    }

    protected final void createMacroConnections(List<VarConnection> varConnections, DynaWaltzContext context) {
        String macroConnectorId = context.addMacroConnector(getName(), varConnections);
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes());
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

    protected <T extends Model> void createMacroConnections(String modelStaticId, IdentifiableType equipmentType, Class<T> modelClass, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        T connectedModel = context.getDynamicModel(modelStaticId, equipmentType, modelClass);
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

    /**
     * Suffixes MacroConnector id with string
     */
    protected <T extends Model> void createMacroConnections(String modelStaticId, Class<T> modelClass, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, String parametrizedName) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), parametrizedName, varConnectionsSupplier.apply(connectedModel, parametrizedName));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
    }

    protected final <T extends Model> void createMacroConnections(String modelStaticId, Class<T> modelClass, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context, String parametrizedName, MacroConnectAttribute... connectFromAttributes) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), parametrizedName, varConnectionsSupplier.apply(connectedModel, parametrizedName));
        List<MacroConnectAttribute> fromAttributes = Stream.concat(getMacroConnectFromAttributes().stream(), Arrays.stream(connectFromAttributes)).collect(Collectors.toList());
        context.addMacroConnect(macroConnectorId, fromAttributes, connectedModel.getMacroConnectToAttributes());
    }
}
