/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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

    protected List<Pair<String, String>> getMacroConnectFromAttributes(Integer index) {
        Pair<String, String> idAttribute = Pair.of("id1", getDynamicModelId());
        if (index == null) {
            return List.of(idAttribute);
        } else {
            return List.of(idAttribute, Pair.of("index1", String.valueOf(index)));
        }
    }

    @Override
    public List<Pair<String, String>> getMacroConnectToAttributes() {
        return List.of(Pair.of("id2", getDynamicModelId()));
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

    protected final <T extends Model> void createMacroConnectionsWithIndex1(List<String> staticIds, Class<T> modelClass, boolean defaultIfNotFound, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        int index = 0;
        for (String id : staticIds) {
            T connectedModel = context.getDynamicModel(id, modelClass, defaultIfNotFound);
            createMacroConnections(connectedModel, varConnectionsSupplier, index, context);
            index++;
        }
    }

    protected final <T extends Model> void createMacroConnectionsWithIndex1(List<T> models, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        int index = 0;
        for (T model : models) {
            createMacroConnections(model, varConnectionsSupplier, index, context);
            index++;
        }
    }

    protected final <T extends Model> void createMacroConnections(String modelStaticId, Class<T> modelClass, boolean defaultIfNotFound, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass, defaultIfNotFound);
        createMacroConnections(connectedModel, varConnectionsSupplier, null, context);
    }

    protected final <T extends Model> void createMacroConnections(T model, Function<T, List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        createMacroConnections(model, varConnectionsSupplier, null, context);
    }

    protected <T extends Model> void createMacroConnections(T connectedModel, Function<T, List<VarConnection>> varConnectionsSupplier, Integer index, DynaWaltzContext context) {
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), varConnectionsSupplier.apply(connectedModel));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(index), connectedModel.getMacroConnectToAttributes());
    }

    protected <T extends Model> void createMacroConnectionsWithParametrizedConnector(String modelStaticId, Class<T> modelClass, boolean defaultIfNotFound, BiFunction<T, String, List<VarConnection>> varConnectionsSupplier, String parametrizedName, DynaWaltzContext context) {
        T connectedModel = context.getDynamicModel(modelStaticId, modelClass, defaultIfNotFound);
        String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), parametrizedName, varConnectionsSupplier.apply(connectedModel, parametrizedName));
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(null), connectedModel.getMacroConnectToAttributes());
    }

    protected final void createStaticMacroConnections(Supplier<List<VarConnection>> varConnectionsSupplier, DynaWaltzContext context) {
        String macroConnectorId = context.addMacroConnector(getName(), varConnectionsSupplier.get());
        context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(null));
    }
}
