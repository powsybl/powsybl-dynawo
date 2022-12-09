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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
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

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, Model connected) throws XMLStreamException {
        macroConnector.writeMacroConnect(writer, getMacroConnectFromAttributes(), connected.getMacroConnectToAttributes());
    }

    protected List<Pair<String, String>> getMacroConnectFromAttributes() {
        return List.of(Pair.of("id1", getDynamicModelId()));
    }

    @Override
    public List<Pair<String, String>> getMacroConnectToAttributes() {
        return List.of(Pair.of("id2", getDynamicModelId()));
    }

    @Override
    public String getParFile(DynaWaltzContext context) {
        return context.getParFile();
    }

    protected void writeDynamicAttributes(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", getParFile(context));
        writer.writeAttribute("parId", getParameterSetId());
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        if (staticId != null) {
            writeBlackBoxModel(writer, context);
        } else {
            writePureDynamicBlackBoxModel(writer, context);
        }
    }

    protected void writeBlackBoxModel(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "blackBoxModel");
        writeDynamicAttributes(writer, context);
        writer.writeAttribute("staticId", staticId);
        MacroStaticReference.writeMacroStaticRef(writer, getLib());
        writer.writeEndElement();
    }

    protected void writePureDynamicBlackBoxModel(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writeDynamicAttributes(writer, context);
    }
}
