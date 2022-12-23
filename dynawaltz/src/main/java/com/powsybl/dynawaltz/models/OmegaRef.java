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
import com.powsybl.dynawaltz.models.generators.GeneratorConnectedToOmegaRefModel;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousModel;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Generator;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.INT;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

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
    private final Map<GeneratorConnectedToOmegaRefModel, Integer> synchronousGenerators = new LinkedHashMap<>();
    private final Map<BusModel, List<GeneratorConnectedToOmegaRefModel>> busGeneratorListMap = new LinkedHashMap<>();

    public OmegaRef(List<GeneratorConnectedToOmegaRefModel> synchronousGenerators) {
        super(OMEGA_REF_ID, OMEGA_REF_PARAMETER_SET_ID);
        int i = 0;
        for (GeneratorConnectedToOmegaRefModel synchronousGenerator : synchronousGenerators) {
            this.synchronousGenerators.put(synchronousGenerator, i++);
        }
    }

    public boolean isEmpty() {
        return synchronousGenerators.isEmpty();
    }

    @Override
    public String getLib() {
        return "DYNModelOmegaRef";
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        DynaWaltzParametersDatabase parDB = context.getParametersDatabase();

        writer.writeStartElement(DYN_URI, "set");
        writer.writeAttribute("id", getParameterSetId());

        // The dynamic models are declared in the DYD following the order of dynamic models supplier.
        // The OmegaRef parameters index the weight of each generator according to that declaration order.
        for (Map.Entry<GeneratorConnectedToOmegaRefModel, Integer> e : synchronousGenerators.entrySet()) {
            GeneratorConnectedToOmegaRefModel generator = e.getKey();
            double h = parDB.getDouble(generator.getParameterSetId(), "generator_H");
            double snom = parDB.getDouble(generator.getParameterSetId(), "generator_SNom");

            ParametersXml.writeParameter(writer, DOUBLE, "weight_gen_" + e.getValue(), Double.toString(h * snom));
        }

        ParametersXml.writeParameter(writer, INT, "nbGen", Long.toString(synchronousGenerators.size()));

        writer.writeEndElement();
    }

    @Override
    public List<VarConnection> getVarConnectionsWith(Model connected) {
        if (connected instanceof GeneratorSynchronousModel) {
            GeneratorSynchronousModel connectedGeneratorModel = (GeneratorSynchronousModel) connected;
            return Arrays.asList(
                    new VarConnection("omega_grp_@INDEX@", connectedGeneratorModel.getOmegaPuVarName()),
                    new VarConnection("omegaRef_grp_@INDEX@", connectedGeneratorModel.getOmegaRefPuVarName()),
                    new VarConnection("running_grp_@INDEX@", connectedGeneratorModel.getRunningVarName())
            );
        } else if (connected instanceof GeneratorConnectedToOmegaRefModel) {
            GeneratorConnectedToOmegaRefModel connectedGeneratorConnectedToOmegaRefModel = (GeneratorConnectedToOmegaRefModel) connected;
            return Arrays.asList(
                    new VarConnection("omegaRef_grp_@INDEX@", connectedGeneratorConnectedToOmegaRefModel.getOmegaRefPuVarName()),
                    new VarConnection("running_grp_@INDEX@", connectedGeneratorConnectedToOmegaRefModel.getRunningVarName())
            );
        } else if (connected instanceof BusModel) {
            return List.of(new VarConnection("numcc_node_@INDEX@", ((BusModel) connected).getNumCCVarName()));
        } else {
            throw new PowsyblException("OmegaRef can only connect to GeneratorModel and BusModel");
        }
    }

    @Override
    public List<Model> getModelsConnectedTo(DynaWaltzContext context) throws PowsyblException {
        for (GeneratorConnectedToOmegaRefModel g : synchronousGenerators.keySet()) {
            BusModel bus = getBusAssociatedTo(g, context);
            busGeneratorListMap.computeIfAbsent(bus, k -> new ArrayList<>()).add(g);
        }
        return Stream.concat(synchronousGenerators.keySet().stream(), busGeneratorListMap.keySet().stream())
                .collect(Collectors.toList());
    }

    private BusModel getBusAssociatedTo(GeneratorConnectedToOmegaRefModel generatorModel, DynaWaltzContext context) {
        Generator generator = generatorModel.getStaticId().map(staticId -> context.getNetwork().getGenerator(staticId)).orElse(null);
        if (generator == null) {
            throw new PowsyblException("Generator " + generatorModel.getLib() + " not found in DynaWaltz context. Id : " + generatorModel.getDynamicModelId());
        }
        String connectedStaticId = generator.getTerminal().getBusBreakerView().getConnectableBus().getId();
        BusModel busModel = (BusModel) context.getStaticIdBlackBoxModelMap().get(connectedStaticId);
        if (busModel == null) {
            busModel = context.getNetworkModel().getDefaultBusModel(connectedStaticId);
        }
        return busModel;
    }

    @Override
    public String getParFile(DynaWaltzContext context) {
        return context.getSimulationParFile();
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, Model connected) throws XMLStreamException {
        if (connected instanceof GeneratorConnectedToOmegaRefModel) {
            List<Pair<String, String>> attributesConnectFrom = getAttributesConnectFrom((GeneratorConnectedToOmegaRefModel) connected);
            macroConnector.writeMacroConnect(writer, attributesConnectFrom, connected.getMacroConnectToAttributes());
        } else if (connected instanceof BusModel) {
            BusModel bus = (BusModel) connected;
            for (GeneratorConnectedToOmegaRefModel g : busGeneratorListMap.get(bus)) {
                List<Pair<String, String>> attributesConnectFrom = getAttributesConnectFrom(g);
                macroConnector.writeMacroConnect(writer, attributesConnectFrom, bus.getMacroConnectToAttributes());
            }
        } else {
            throw new PowsyblException("OmegaRef can only connect to GeneratorConnectedToOmegaRefModel and BusModel");
        }
    }

    private List<Pair<String, String>> getAttributesConnectFrom(GeneratorConnectedToOmegaRefModel generator) {
        int index = synchronousGenerators.get(generator);
        return List.of(
                Pair.of("id1", OMEGA_REF_ID),
                Pair.of("index1", Integer.toString(index)));
    }
}
