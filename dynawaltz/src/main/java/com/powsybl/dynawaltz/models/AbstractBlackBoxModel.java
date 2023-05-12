/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

    @Override
    public List<MacroConnectAttribute> getMacroConnectFromAttributes() {
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
}
