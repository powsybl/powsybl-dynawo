/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import static com.powsybl.dynawo.xml.DynawoConstants.OMEGAREF_PAR_FILENAME;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.MACRO_CONNECTOR_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.NETWORK;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.xml.DynawoXmlContext;
import com.powsybl.dynawo.xml.MacroConnectorXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DYNModelOmegaRef extends AbstractBlackBoxModel {

    private static final String OMEGA_REF_ID = "OMEGA_REF";
    private static final String OMEGA_REF_PARAMETER_SET_ID = "OMEGA_REF";
    private static final String MACRO_CONNECTOR_TO_GENERATOR_SUFFIX = "ToGenerator";
    private static final String MACRO_CONNECTOR_TO_NUMCCMACHINE_SUFFIX = "ToNumCCMachine";

    // OmegaRef is a magic model
    // There will be multiple instances of OmegaRef dynamic model,
    // each one having only one reference to a generator dynamic model
    // When all these OmegaRef objects are serialized to XML,
    // Only one blackBoxModel must be present in the XML output,
    // connected to all generator dynamic models that have been specified
    public DYNModelOmegaRef(String generatorDynamicModelId) {
        super(OMEGA_REF_ID, "", OMEGA_REF_PARAMETER_SET_ID);
        this.generatorDynamicModelId = generatorDynamicModelId;
    }

    @Override
    public String getLib() {
        return "DYNModelOmegaRef";
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        int index = context.getIndex(getLib(), true);
        if (index == 0) {
            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_GENERATOR_SUFFIX);
            MacroConnectorXml.writeConnect(writer, "omega_grp_@INDEX@", "generator_omegaPu");
            MacroConnectorXml.writeConnect(writer, "omegaRef_grp_@INDEX@", "generator_omegaRefPu");
            MacroConnectorXml.writeConnect(writer, "running_grp_@INDEX@", "generator_running");
            writer.writeEndElement();

            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_NUMCCMACHINE_SUFFIX);
            MacroConnectorXml.writeConnect(writer, "numcc_node_@INDEX@", "@@NAME@@@NODE@_numcc");
            writer.writeEndElement();

            // Special magic here:
            // All instances of DYNModelOmegaRef refer in fact to a single Dynawo BlackBoxModel
            // We write the blackBoxModel in the output XML only once
            writer.writeEmptyElement(DYN_URI, "blackBoxModel");
            writer.writeAttribute("id", getDynamicModelId());
            writer.writeAttribute("lib", getLib());
            writer.writeAttribute("parFile", OMEGAREF_PAR_FILENAME);
            writer.writeAttribute("parId", getParameterSetId());
        }
        // This instance of DYNModelOmegaRef has a reference to one generator, write its connect
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_GENERATOR_SUFFIX, getDynamicModelId(), index, generatorDynamicModelId);
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_NUMCCMACHINE_SUFFIX, getDynamicModelId(), index, NETWORK, getStaticId(context, generatorDynamicModelId));
    }

    private String getStaticId(DynawoXmlContext context, String dynamicModelId) {
        DynamicModel dynamicModel = context.getDynamicModel(dynamicModelId);
        if (dynamicModel == null) {
            return dynamicModelId;
        }
        AbstractBlackBoxModel bbm = (AbstractBlackBoxModel) dynamicModel;
        return bbm.getStaticId();
    }

    private final String generatorDynamicModelId;

}
