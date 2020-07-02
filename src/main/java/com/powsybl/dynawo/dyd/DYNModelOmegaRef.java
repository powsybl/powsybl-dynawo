/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.MACRO_CONNECTOR_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.MACRO_CONNECTOR_TO_GENERATOR;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.MACRO_CONNECTOR_TO_NUMCCMACHINE;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.NETWORK;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.xml.DynawoXmlContext;
import com.powsybl.dynawo.xml.MacroConnectorXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DYNModelOmegaRef extends AbstractBlackBoxModel {

    public DYNModelOmegaRef(String modelId, String parameterSetId) {
        super(modelId, "", parameterSetId);
        this.generatorModelIds = new ArrayList<>();
    }

    public void addGenerator(String generatorModelId) {
        generatorModelIds.add(generatorModelId);
    }

    @Override
    public String getLib() {
        return "DYNModelOmegaReg";
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        if (context.getIndex(getLib(), true) == 0) {
            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_GENERATOR);
            MacroConnectorXml.writeConnect(writer, "omega_grp_@INDEX@", "generator_omegaPu");
            MacroConnectorXml.writeConnect(writer, "omegaRef_grp_@INDEX@", "generator_omegaRefPu");
            MacroConnectorXml.writeConnect(writer, "running_grp_@INDEX@", "generator_running");
            writer.writeEndElement();

            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_NUMCCMACHINE);
            MacroConnectorXml.writeConnect(writer, "numcc_node_@INDEX@", "@@NAME@@@NODE@_numcc");
            writer.writeEndElement();
        }

        // Write the blackBoxModel object
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());

        // Write the connect object
        int index = 0;
        for (String generatorModelId : generatorModelIds) {
            writeConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_GENERATOR, getId(), index, generatorModelId);
            writeConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_NUMCCMACHINE, getId(), index, NETWORK, getStaticId(context, generatorModelId));
            index++;
        }
    }

    private String getStaticId(DynawoXmlContext context, String modelId) {
        DynamicModel dynamicModel = context.getDynamicModel(modelId);
        AbstractBlackBoxModel bbm = (AbstractBlackBoxModel) dynamicModel;
        return bbm.getStaticId();
    }

    private void writeConnect(XMLStreamWriter writer, String connector, String id1, int index1, String id2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", connector);
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("index1", Integer.toString(index1));
        writer.writeAttribute("id2", id2);
    }

    private void writeConnect(XMLStreamWriter writer, String connector, String id1, int index1, String id2, String name2) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "macroConnect");
        writer.writeAttribute("connector", connector);
        writer.writeAttribute("id1", id1);
        writer.writeAttribute("index1", Integer.toString(index1));
        writer.writeAttribute("id2", id2);
        writer.writeAttribute("name2", name2);
    }

    private List<String> generatorModelIds;

}
