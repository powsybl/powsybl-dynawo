/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzParametersDatabase;
import com.powsybl.dynawaltz.xml.DynawaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroConnectorXml;
import com.powsybl.dynawaltz.xml.ParametersXml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Objects;

import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.INT;
import static com.powsybl.dynawaltz.xml.DynawaltzXmlConstants.*;

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

    public OmegaRef(String generatorDynamicModelId) {
        super(OMEGA_REF_ID + "_" + generatorDynamicModelId, "", OMEGA_REF_PARAMETER_SET_ID);
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

    public String getGeneratorDynamicModelId() {
        return generatorDynamicModelId;
    }

    @Override
    public void write(XMLStreamWriter writer, DynawaltzXmlContext context) throws XMLStreamException {
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
            writer.writeAttribute("id", OMEGA_REF_ID);
            writer.writeAttribute("lib", getLib());
            writer.writeAttribute("parFile", context.getSimulationParFile());
            writer.writeAttribute("parId", getParameterSetId());
        }

        // This instance of DYNModelOmegaRef has a reference to one generator, write its connect and the subsequent connect to the NETWORK model
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_GENERATOR_SUFFIX, OMEGA_REF_ID, index, generatorDynamicModelId);
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib() + MACRO_CONNECTOR_TO_NUMCCMACHINE_SUFFIX, OMEGA_REF_ID, index, NETWORK, getStaticId(context, generatorDynamicModelId));
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynawaltzXmlContext context) throws XMLStreamException {
        int index = context.getIndex(getLib(), true);
        if (index == 0) {
            DynaWaltzParametersDatabase parDB = context.getParametersDatabase();

            writer.writeStartElement(DYN_URI, "set");
            writer.writeAttribute("id", getParameterSetId());

            long count = 0;
            // Black box models returned by the context should follow the same order
            // of the dynamic models supplier returned by the dynamic models supplier.
            // The dynamic models are declared in the DYD following the order of dynamic models supplier.
            // The OmegaRef parameters index the weight of each generator according to that declaration order.
            for (AbstractBlackBoxModel model : context.getBlackBoxModels()) {
                if (model instanceof OmegaRef) {
                    AbstractBlackBoxModel generatorModel = context.getBlackBoxModel(((OmegaRef) model).getGeneratorDynamicModelId());
                    double h = parDB.getDouble(generatorModel.getParameterSetId(), "generator_H");
                    double snom = parDB.getDouble(generatorModel.getParameterSetId(), "generator_SNom");

                    ParametersXml.writeParameter(writer, DOUBLE, "weight_gen_" + count, Double.toString(h * snom));
                    count++;
                }
            }
            ParametersXml.writeParameter(writer, INT, "nbGen", Long.toString(count));

            writer.writeEndElement();
        }
    }

    private static String getStaticId(DynawaltzXmlContext context, String dynamicModelId) {
        AbstractBlackBoxModel dynamicModel = context.getBlackBoxModel(dynamicModelId);
        if (dynamicModel == null) {
            throw new PowsyblException("BlackBoxModel '" + dynamicModelId + "' not found");
        }
        return dynamicModel.getStaticId();
    }

    private final String generatorDynamicModelId;
}
