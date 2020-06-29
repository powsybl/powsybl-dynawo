/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.xml.DynawoXmlContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public abstract class AbstractBlackBoxModel implements DynamicModel {

    public AbstractBlackBoxModel(String id, String staticId, String parameterSetId) {
        this.id = Objects.requireNonNull(id);
        this.staticId = Objects.requireNonNull(staticId);
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    public String getId() {
        return id;
    }

    public abstract String getLib();

    public String getStaticId() {
        return staticId;
    }

    public String getParameterSetId() {
        return parameterSetId;
    }

    public abstract void write(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException;

    protected static void writeStaticRef(XMLStreamWriter writer, String var, String staticVar) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "staticRef");
        writer.writeAttribute("var", var);
        writer.writeAttribute("staticVar", staticVar);
    }

    protected static void writeMacroStaticRef(XMLStreamWriter writer, String id) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroStaticRef");
        writer.writeAttribute("id", id);
    }

    protected void writeMacroConnect(XMLStreamWriter writer, String connector, String id1, String id2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", connector);
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("id2", id2);
    }

    protected static void writeMacroConnectorConnect(XMLStreamWriter writer, String var1, String var2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "connect");
        writer.writeAttribute("var1", var1);
        writer.writeAttribute("var2", var2);
    }

    private final String id;
    private final String staticId;
    private final String parameterSetId;
}
