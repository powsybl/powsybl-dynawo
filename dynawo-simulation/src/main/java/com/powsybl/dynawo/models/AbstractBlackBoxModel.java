/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawo.parameters.ParametersSet;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Luma Zamarreño {@literal <zamarrenolm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractBlackBoxModel implements BlackBoxModel {

    private final String dynamicModelId;
    private String parameterSetId;

    protected AbstractBlackBoxModel(String dynamicModelId, String parameterSetId) {
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    protected AbstractBlackBoxModel(String dynamicModelId) {
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
        this.parameterSetId = dynamicModelId;
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

    public void setParameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId;
    }

    @Override
    public void createDynamicModelParameters(DynawoSimulationContext context, Consumer<ParametersSet> parametersAdder) {
        // method empty by default to be redefined by specific models
    }

    @Override
    public void createNetworkParameter(ParametersSet networkParameters) {
        // method empty by default to be redefined by specific models
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectFromAttributes() {
        return List.of(MacroConnectAttribute.of("id1", getDynamicModelId()));
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        return List.of(MacroConnectAttribute.of("id2", getDynamicModelId()));
    }

    @Override
    public void write(XMLStreamWriter writer) throws XMLStreamException {
        write(writer, getDefaultParFile());
    }

    @Override
    public String getDefaultParFile() {
        return DynawoSimulationParameters.MODELS_OUTPUT_PARAMETERS_FILE;
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return Collections.emptyList();
    }

    protected void writeDynamicAttributes(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", parFileName);
        writer.writeAttribute("parId", getParameterSetId());
    }
}
