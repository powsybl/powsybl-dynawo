/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.xml.DynawoXmlContext;
import com.powsybl.dynawo.xml.MacroConnectorXml;
import com.powsybl.dynawo.xml.ParameterXml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoConstants.OMEGAREF_PAR_FILENAME;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.*;

/**
 * OmegaRef is a special dynamic model: its role is to synchronize the generators' frequency, there will be multiple Java
 * instances of the OmegaRef dynamic model, one for each generator's dynamic model connected to it. The corresponding black
 * box model XML entry is serialized only once. For each generator synchronised through the OmegaRef model, there will be
 * one XML entry for the connection with the generator's dynamic model, and one XML entry for the connection with the
 * NETWORK dynamic model. There are thus two macroConnectors defined for OmegaRef: one to connect it to a generator's
 * dynamic model and one to connect it to the NETWORK model
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class OmegaRef extends AbstractBlackBoxModel {

    public static final String OMEGA_REF_ID = "OMEGA_REF";
    private static final String OMEGA_REF_PARAMETER_SET_ID = "OMEGA_REF";
    private static final String MACRO_CONNECTOR_TO_GENERATOR_SUFFIX = "ToGenerator";
    private static final String MACRO_CONNECTOR_TO_NUMCCMACHINE_SUFFIX = "ToNumCCMachine";
    private static final double OMEGA_REF_TEMP_WEIGHT_NOT_CALCULATED = 10.0;

    public OmegaRef(String generatorDynamicModelId) {
        super(OMEGA_REF_ID, "", OMEGA_REF_PARAMETER_SET_ID, null);
        this.generatorDynamicModelId = Objects.requireNonNull(generatorDynamicModelId);
    }

    @Override
    public String getLib() {
        return "DYNModelOmegaRef";
    }

    @Override
    public String getStaticId() {
        throw new UnsupportedOperationException("OmegaRef is not bound to a static equipment");
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

        // This instance of DYNModelOmegaRef has a reference to one generator, write its connect and the subsequent connect to the NETWORK model
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_GENERATOR_SUFFIX, getDynamicModelId(), index, generatorDynamicModelId);
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_NUMCCMACHINE_SUFFIX, getDynamicModelId(), index, NETWORK, getStaticId(context, generatorDynamicModelId));
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        int index = context.getIndex(getLib(), true);
        if (index == 0) {
            writer.writeStartElement(DYN_URI, "set");
            writer.writeAttribute("id", getParameterSetId());
            ParameterXml.writeParameter(writer, "INT", "nbGen", Long.toString(context.getOmegaRefCount()));
        }

        double weight = OMEGA_REF_TEMP_WEIGHT_NOT_CALCULATED;
        AbstractBlackBoxModel dynamicModel = context.getBlackBoxModel(generatorDynamicModelId);
        if (dynamicModel instanceof GeneratorSynchronousFourWindingsProportionalRegulations) {
            GeneratorSynchronousFourWindingsProportionalRegulations.Parameters parameters = (GeneratorSynchronousFourWindingsProportionalRegulations.Parameters) dynamicModel.getParameters();
            if (parameters != null) {
                weight = Double.parseDouble(parameters.getGeneratorH()) * Double.parseDouble(parameters.getGeneratorSNom());
            }
        }
        ParameterXml.writeParameter(writer, "DOUBLE", "weight_gen_" + index, Double.toString(weight));
        if (index == context.getOmegaRefCount() - 1) {
            writer.writeEndElement();
        }
    }

    private static String getStaticId(DynawoXmlContext context, String dynamicModelId) {
        AbstractBlackBoxModel dynamicModel = context.getBlackBoxModel(dynamicModelId);
        if (dynamicModel == null) {
            throw new PowsyblException("BlackBoxModel '" + dynamicModelId + "' not found");
        }
        return dynamicModel.getStaticId();
    }

    private final String generatorDynamicModelId;
}
