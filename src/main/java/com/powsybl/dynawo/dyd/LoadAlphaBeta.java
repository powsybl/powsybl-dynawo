/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

import java.util.Collections;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.xml.DynamicModelsXml.DydXmlWriterContext;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LoadAlphaBeta extends AbstractDynawoDynamicModel {

    public LoadAlphaBeta(String modelId, String staticId, String parameterSetId) {
        super(modelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadAlphaBeta";
    }

    @Override
    public List<MacroConnect> getMacroConnects() {
        return Collections.singletonList(new MacroConnect("LoadToNode", getId(), "NETWORK"));
    }

    @Override
    public List<String> getMacroStaticRefs() {
        return Collections.singletonList("Load");
    }

    @Override
    public void write(XMLStreamWriter writer, DydXmlWriterContext dydXmlWriterContext) throws XMLStreamException {

        writer.writeStartElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", dydXmlWriterContext.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
        writer.writeAttribute("staticId", getStaticId());

        for (String macroStaticRef : getMacroStaticRefs()) {
            writer.writeEmptyElement(DYN_URI, "macroStaticRef");
            writer.writeAttribute("id", macroStaticRef);
            dydXmlWriterContext.addMacroStaticReferencesUsed(macroStaticRef);
        }
        writer.writeEndElement();

        // Write all macroConnects related to the dynamic models
        for (MacroConnect macroConnect : getMacroConnects()) {
            writeMacroConnect(writer, dydXmlWriterContext, macroConnect);
        }

    }

    private void writeMacroConnect(XMLStreamWriter writer, DydXmlWriterContext dydXmlWriterContext, MacroConnect macroConnect) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", macroConnect.getId());
        writer.writeAttribute("id1", macroConnect.getId1());
        writer.writeAttribute("id2", macroConnect.getId2());
        dydXmlWriterContext.addMacroConnectorsUsed(macroConnect.getId());
    }
}
