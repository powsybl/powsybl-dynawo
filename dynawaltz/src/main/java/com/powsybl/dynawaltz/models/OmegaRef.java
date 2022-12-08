/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParametersDatabase;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousModel;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Generator;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.*;

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
public class OmegaRef extends AbstractPureDynamicBlackBoxModel {

    public static final String OMEGA_REF_ID = "OMEGA_REF";
    private static final String OMEGA_REF_PARAMETER_SET_ID = "OMEGA_REF";
    private final List<GeneratorSynchronousModel> synchronousGenerators;
    private Map<Model, Integer> indexPerModel;

    public OmegaRef(List<GeneratorSynchronousModel> synchronousGenerators) {
        super(OMEGA_REF_ID, OMEGA_REF_PARAMETER_SET_ID);
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
    public List<Pair<String, String>> getVarsConnect(Model connected) {
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
    public List<Model> getModelsConnectedTo(DynaWaltzContext context) throws PowsyblException {
        return new ArrayList<>(getConnectedModelsIndices(context).keySet());
    }

    private BusModel getBusAssociatedTo(GeneratorSynchronousModel generatorModel, DynaWaltzContext context) {
        Generator generator = context.getNetwork().getGenerator(generatorModel.getStaticId());
        if (generator == null) {
            throw new PowsyblException("Generator " + generatorModel.getLib() + " not found in DynaWaltz context. Id : " + generatorModel.getDynamicModelId());
        }
        String connectedStaticId = generator.getTerminal().getBusBreakerView().getConnectableBus().getId();
        BusModel busModel = (BusModel) context.getStaticIdBlackBoxModelMap().get(connectedStaticId);
        if (busModel == null) {
            busModel = (BusModel) context.getNetworkModel().getDefaultBusModel(connectedStaticId);
        }
        return busModel;
    }

    private Map<Model, Integer> getConnectedModelsIndices(DynaWaltzContext context) {
        if (indexPerModel == null) {
            indexPerModel = new LinkedHashMap<>();
            int index = 0;
            for (GeneratorSynchronousModel generatorSynchronousModel : synchronousGenerators) {
                BusModel busModel = getBusAssociatedTo(generatorSynchronousModel, context);
                indexPerModel.put(generatorSynchronousModel, index);
                indexPerModel.put(busModel, index);
                index++;
            }
        }
        return indexPerModel;
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, Model connected) throws XMLStreamException {
        Map<Model, Integer> indicesPerModel = getConnectedModelsIndices(context);

        List<Pair<String, String>> attributesConnectFrom = List.of(
                Pair.of("id1", getDynamicModelId()),
                Pair.of("index1", Integer.toString(indicesPerModel.get(connected)))
        );
        macroConnector.writeMacroConnect(writer, attributesConnectFrom, connected.getAttributesConnectTo());
    }
}
