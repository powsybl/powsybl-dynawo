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
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Generator;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.INT;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.*;

/**
 * OmegaRef is a special model: its role is to synchronize the generators' frequency. The corresponding black
 * box model XML entry is serialized only once. For each generator synchronised through the OmegaRef model, there will be
 * one XML entry for the connection with the generator's dynamic model, and one XML entry for the connection with the
 * NETWORK dynamic model. There are thus two macroConnectors defined for OmegaRef: one to connect it to a generator's
 * dynamic model and one to connect it to the NETWORK model.
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class OmegaRef extends AbstractBlackBoxModel {

    public static final String OMEGA_REF_ID = "OMEGA_REF";
    private static final String OMEGA_REF_PARAMETER_SET_ID = "OMEGA_REF";
    private final List<GeneratorSynchronousModel> synchronousGenerators;
    private List<Pair<GeneratorSynchronousModel, BusModel>> connectedModels = new ArrayList<>();

    public OmegaRef(List<GeneratorSynchronousModel> synchronousGenerators) {
        super(OMEGA_REF_ID, "", OMEGA_REF_PARAMETER_SET_ID);
        this.synchronousGenerators = synchronousGenerators;
    }

    public List<GeneratorSynchronousModel> getSynchronousGenerators() {
        return Collections.unmodifiableList(synchronousGenerators);
    }

    @Override
    public String getLib() {
        return "DYNModelOmegaRef";
    }

    @Override
    public List<Pair<String, String>> getVarsMapping() {
        return Collections.emptyList();
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", OMEGA_REF_ID);
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getSimulationParFile());
        writer.writeAttribute("parId", getParameterSetId());
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        DynaWaltzParametersDatabase parDB = context.getParametersDatabase();

        writer.writeStartElement(DYN_URI, "set");
        writer.writeAttribute("id", getParameterSetId());

        long count = 0;
        // The dynamic models are declared in the DYD following the order of dynamic models supplier.
        // The OmegaRef parameters index the weight of each generator according to that declaration order.
        for (GeneratorSynchronousModel blackBoxModel : synchronousGenerators) {
            double h = parDB.getDouble(blackBoxModel.getParameterSetId(), "generator_H");
            double snom = parDB.getDouble(blackBoxModel.getParameterSetId(), "generator_SNom");

            ParametersXml.writeParameter(writer, DOUBLE, "weight_gen_" + count, Double.toString(h * snom));
            count++;
        }

        ParametersXml.writeParameter(writer, INT, "nbGen", Long.toString(count));

        writer.writeEndElement();
    }

    @Override
    public List<Pair<String, String>> getVarsConnect(BlackBoxModel connected) {
        if (connected instanceof GeneratorSynchronousModel) {
            GeneratorSynchronousModel connectedGeneratorModel = (GeneratorSynchronousModel) connected;
            return Arrays.asList(
                    Pair.of("omega_grp_@INDEX@", connectedGeneratorModel.getOmegaPuVarName()),
                    Pair.of("omegaRef_grp_@INDEX@", connectedGeneratorModel.getOmegaRefPuVarName()),
                    Pair.of("running_grp_@INDEX@", connectedGeneratorModel.getRunningVarName())
            );
        } else if (connected instanceof BusModel) {
            return List.of(Pair.of("numcc_node_@INDEX@", ((BusModel) connected).getNumCCVarName()));
        } else {
            throw new PowsyblException("OmegaRef can only connect to GeneratorModel and BusModel");
        }
    }

    @Override
    public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext context) throws PowsyblException {
        List<BlackBoxModel> lGenAndBuses = new ArrayList<>();

        for (BlackBoxModel generatorModel : synchronousGenerators.stream().map(BlackBoxModel.class::cast).collect(Collectors.toList())) {
            Generator generator = context.getNetwork().getGenerator(generatorModel.getStaticId());
            if (generator == null) {
                throw new PowsyblException("Generator " + generatorModel.getLib() + " not found in DynaWaltz context. Id : " + generatorModel.getDynamicModelId());
            }
            lGenAndBuses.add(generatorModel);
            String connectedStaticId = generator.getTerminal().getBusBreakerView().getConnectableBus().getId();
            BlackBoxModel busModel = context.getStaticIdBlackBoxModelMap().get(connectedStaticId);
            if (busModel == null) {
                busModel = context.getNetworkModel().getDefaultBusModel(connectedStaticId);
            }
            lGenAndBuses.add(busModel);

            if (connectedModels.size() < synchronousGenerators.size()) {
                connectedModels.add(Pair.of((GeneratorSynchronousModel) generatorModel, (BusModel) busModel));
            }
        }

        return lGenAndBuses;
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, BlackBoxModel connected) throws XMLStreamException {
        int index1 = 0;
        while (index1 < connectedModels.size()) {
            Pair<GeneratorSynchronousModel, BusModel> currentPair = connectedModels.get(index1);
            if (currentPair.getLeft().equals(connected) ||
                    currentPair.getRight().equals(connected)) {
                break;
            }
            index1++;
        }

        List<Pair<String, String>> attributesConnectFrom = List.of(
                Pair.of("id1", getDynamicModelId()),
                Pair.of("index1", Integer.toString(index1))
        );
        macroConnector.writeMacroConnect(writer, attributesConnectFrom, connected.getAttributesConnectTo());
    }
}
