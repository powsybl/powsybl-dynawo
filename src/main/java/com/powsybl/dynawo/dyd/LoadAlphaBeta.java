/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.MACRO_CONNECTOR_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.MACRO_STATIC_REFERENCE_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.NETWORK;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.xml.DynawoXmlContext;
import com.powsybl.dynawo.xml.MacroConnectorXml;
import com.powsybl.dynawo.xml.MacroStaticReferenceXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LoadAlphaBeta extends AbstractBlackBoxModel {

    public LoadAlphaBeta(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadAlphaBeta";
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        if (context.getIndex(getLib(), true) == 0) {
            // Write the macroStaticReference object
            writer.writeStartElement(DYN_URI, "macroStaticReference");
            writer.writeAttribute("id", MACRO_STATIC_REFERENCE_PREFIX + getLib());
            MacroStaticReferenceXml.writeStaticRef(writer, "load_PPu", "p");
            MacroStaticReferenceXml.writeStaticRef(writer, "load_QPu", "q");
            MacroStaticReferenceXml.writeStaticRef(writer, "load_state", "state");
            writer.writeEndElement();

            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib());
            MacroConnectorXml.writeConnect(writer, "load_terminal", "@STATIC_ID@@NODE@_ACPIN");
            MacroConnectorXml.writeConnect(writer, "load_switchOffSignal1", "@STATIC_ID@@NODE@_switchOff");
            writer.writeEndElement();
        }

        // Write the blackBoxModel object
        writer.writeStartElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
        writer.writeAttribute("staticId", getStaticId());
        MacroStaticReferenceXml.writeMacroStaticRef(writer, MACRO_STATIC_REFERENCE_PREFIX + getLib());
        writer.writeEndElement();

        // Write the connect object
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib(), getDynamicModelId(), NETWORK);
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
    }
}
