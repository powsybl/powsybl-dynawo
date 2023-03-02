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
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.iidm.network.Generator;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.DynaWaltzParametersDatabase.ParameterType.INT;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * OmegaRef is a special model: its role is to synchronize the generators' frequency. The corresponding black
 * box model XML entry is serialized only once. For each generator synchronized through the OmegaRef model, there will be
 * one XML entry for the connection with the generator's dynamic model, and one XML entry for the connection with the
 * NETWORK dynamic model. There are thus two macroConnectors defined for OmegaRef: one to connect it to a generator's
 * dynamic model and one to connect it to the NETWORK model.
 *
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class OmegaRef extends AbstractPureDynamicBlackBoxModel {

    public static final String OMEGA_REF_ID = "OMEGA_REF";
    private static final String OMEGA_REF_PARAMETER_SET_ID = "OMEGA_REF";
    private final List<GeneratorSynchronousModel> synchronousGenerators;

    public OmegaRef(List<GeneratorSynchronousModel> synchronousGenerators) {
        super(OMEGA_REF_ID, OMEGA_REF_PARAMETER_SET_ID);
        this.synchronousGenerators = synchronousGenerators;
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

        // The dynamic models are declared in the DYD following the order of dynamic models' supplier.
        // The OmegaRef parameters index the weight of each generator according to that declaration order.
        int index = 0;
        for (GeneratorSynchronousModel generator : synchronousGenerators) {
            double h = parDB.getDouble(generator.getParameterSetId(), "generator_H");
            double snom = parDB.getDouble(generator.getParameterSetId(), "generator_SNom");
            ParametersXml.writeParameter(writer, DOUBLE, "weight_gen_" + index, Double.toString(h * snom));
            index++;
        }

        ParametersXml.writeParameter(writer, INT, "nbGen", Long.toString(synchronousGenerators.size()));

        writer.writeEndElement();
    }

    private List<VarConnection> getVarConnectionsWithGeneratorSynchronous(GeneratorSynchronousModel connected) {
        return Arrays.asList(
                new VarConnection("omega_grp_@INDEX@", connected.getOmegaPuVarName()),
                new VarConnection("omegaRef_grp_@INDEX@", connected.getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@", connected.getRunningVarName())
        );
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        return List.of(new VarConnection("numcc_node_@INDEX@", connected.getNumCCVarName()));
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) throws PowsyblException {
        List<String> busStaticIds = synchronousGenerators.stream().map(g -> getBusAssociatedTo(g, context)).collect(Collectors.toList());
        createMacroConnectionsWithIndex1(synchronousGenerators, this::getVarConnectionsWithGeneratorSynchronous, context);
        createMacroConnectionsWithIndex1(busStaticIds, BusModel.class, true, this::getVarConnectionsWithBus, context);
    }

    private String getBusAssociatedTo(GeneratorSynchronousModel generatorModel, DynaWaltzContext context) {
        Generator generator = generatorModel.getStaticId().map(staticId -> context.getNetwork().getGenerator(staticId)).orElse(null);
        if (generator == null) {
            throw new PowsyblException("Generator " + generatorModel.getLib() + " not found in DynaWaltz context. Id : " + generatorModel.getDynamicModelId());
        }
        return BusUtils.getConnectableBusStaticId(generator);
    }

    @Override
    public String getParFile(DynaWaltzContext context) {
        return context.getSimulationParFile();
    }
}
