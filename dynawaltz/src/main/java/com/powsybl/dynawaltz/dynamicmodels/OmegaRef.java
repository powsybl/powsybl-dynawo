/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParametersDatabase;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Generator;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.INT;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.*;

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

    private final String generatorDynamicModelId;

    public OmegaRef(String generatorDynamicModelId) {
        // All OmegaRef instances have the same dynamicId, as all instances of DYNModelOmegaRef refer in fact to a single Dynawo BlackBoxModel
        super(OMEGA_REF_ID, "", OMEGA_REF_PARAMETER_SET_ID);
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
    public List<Pair<String, String>> getVarsMapping() {
        return Collections.emptyList();
    }

    public String getGeneratorDynamicModelId() {
        return generatorDynamicModelId;
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        // Special magic here:
        // All instances of DYNModelOmegaRef refer in fact to a single Dynawo BlackBoxModel, hence all have the same dynamicId
        // Therefore this is called only once, and the blackBoxModel is written in the output XML only once
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", OMEGA_REF_ID);
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getSimulationParFile());
        writer.writeAttribute("parId", getParameterSetId());
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        DynaWaltzParametersDatabase parDB = context.getParametersDatabase();

        writer.writeStartElement(DYN_URI, "set");
        writer.writeAttribute("id", getParameterSetId());

        long count = 0;
        // Black box models returned by the context should follow the same order
        // of the dynamic models supplier returned by the dynamic models supplier.
        // The dynamic models are declared in the DYD following the order of dynamic models supplier.
        // The OmegaRef parameters index the weight of each generator according to that declaration order.
        for (BlackBoxModel model : context.getBlackBoxModels()) {
            if (model instanceof OmegaRef) {
                BlackBoxModel generatorModel = context.getBlackBoxModel(((OmegaRef) model).getGeneratorDynamicModelId());
                double h = parDB.getDouble(generatorModel.getParameterSetId(), "generator_H");
                double snom = parDB.getDouble(generatorModel.getParameterSetId(), "generator_SNom");

                ParametersXml.writeParameter(writer, DOUBLE, "weight_gen_" + count, Double.toString(h * snom));
                count++;
            }
        }
        ParametersXml.writeParameter(writer, INT, "nbGen", Long.toString(count));

        writer.writeEndElement();
    }

    @Override
    public List<Pair<String, String>> getVarsConnect(BlackBoxModel connected) {
        if (connected instanceof GeneratorModel) {
            GeneratorModel connectedGeneratorModel = (GeneratorModel) connected;
            return Arrays.asList(
                    Pair.of("omega_grp_@INDEX@", connectedGeneratorModel.getOmegaPuVarName()),
                    Pair.of("omegaRef_grp_@INDEX@", connectedGeneratorModel.getOmegaRefPuVarName()),
                    Pair.of("running_grp_@INDEX@", connectedGeneratorModel.getRunningVarName())
            );
        } else if (connected instanceof BusModel) {
            return List.of(Pair.of("numcc_node_@INDEX@", ((BusModel) connected).getNumCCVarName()));
        } else {
            throw new PowsyblException("OmegaRef can only connect to GeneratorModel and ConnectedComponentModel");
        }
    }

    @Override
    public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext context) {
        BlackBoxModel generatorModel = context.getBlackBoxModelFromDynamicId(generatorDynamicModelId);
        if (generatorModel == null) {
            generatorModel = context.getNetworkModel().getDefaultGeneratorModel();
        }
        if (!(generatorModel instanceof GeneratorModel)) {
            throw new PowsyblException("Generator dynamic id does not correspond to a generator: " + generatorDynamicModelId
                    + " corresponds to " + generatorModel.getClass().getSimpleName());
        }

        Generator generator = context.getNetwork().getGenerator(generatorModel.getStaticId());
        if (generator == null) {
            throw new PowsyblException("Generator static id unknown: " + getStaticId());
        }
        String connectedStaticId = generator.getTerminal().getBusBreakerView().getConnectableBus().getId();
        BlackBoxModel busModel = context.getStaticIdBlackBoxModelMap().get(connectedStaticId);
        if (busModel == null) {
            busModel = context.getNetworkModel().getDefaultBusModel();
        }

        return List.of(generatorModel, busModel);
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzXmlContext xmlContext, MacroConnector macroConnector, BlackBoxModel connected) throws XMLStreamException {
        int index = xmlContext.getLibIndex(this);
        if (connected instanceof GeneratorModel) {
            macroConnector.writeMacroConnect(writer, OMEGA_REF_ID, index, connected.getDynamicModelId());
        } else if (connected instanceof BusModel) {
            macroConnector.writeMacroConnect(writer, OMEGA_REF_ID, index, NETWORK, connected.getStaticId());
        }
    }
}
