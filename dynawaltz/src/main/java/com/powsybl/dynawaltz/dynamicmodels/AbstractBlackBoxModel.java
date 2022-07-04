/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroStaticReference;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public abstract class AbstractBlackBoxModel implements DynamicModel, BlackBoxModel {

    public AbstractBlackBoxModel(String dynamicModelId, String staticId, String parameterSetId) {
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
        this.staticId = Objects.requireNonNull(staticId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    public String getDynamicModelId() {
        return dynamicModelId;
    }

    public String getStaticId() {
        return staticId;
    }

    public String getParameterSetId() {
        return parameterSetId;
    }

    public void writeParameters(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        //Empty method to be redefined by specific models
    }

    protected void writeBlackBoxModel(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        // Write the blackBoxModel object
        writer.writeStartElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
        writer.writeAttribute("staticId", getStaticId());
        MacroStaticReference.writeMacroStaticRef(writer, getLib());
        writer.writeEndElement();
    }

    protected void writeAutomatonBlackBoxModel(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        // Write the blackBoxModel object
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
    }

    private final String dynamicModelId;
    private final String staticId;
    private final String parameterSetId;
}
