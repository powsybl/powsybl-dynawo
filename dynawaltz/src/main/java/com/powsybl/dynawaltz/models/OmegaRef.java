/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousModel;
import com.powsybl.dynawaltz.models.generators.OmegaRefGeneratorModel;
import com.powsybl.dynawaltz.xml.ParametersXml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynawaltz.parameters.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.parameters.ParameterType.INT;
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
    private final List<OmegaRefGeneratorModel> omegaRefGenerators;

    public OmegaRef(List<OmegaRefGeneratorModel> omegaRefGenerators) {
        super(OMEGA_REF_ID, OMEGA_REF_PARAMETER_SET_ID);
        this.omegaRefGenerators = omegaRefGenerators;
    }

    public boolean isEmpty() {
        return omegaRefGenerators.isEmpty();
    }

    @Override
    public String getLib() {
        return "DYNModelOmegaRef";
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        DynaWaltzParameters dynaWaltzParameters = context.getDynaWaltzParameters();

        writer.writeStartElement(DYN_URI, "set");
        writer.writeAttribute("id", getParameterSetId());

        // The dynamic models are declared in the DYD following the order of dynamic models' supplier.
        // The OmegaRef parameters index the weight of each generator according to that declaration order.
        int index = 0;
        for (OmegaRefGeneratorModel generator : omegaRefGenerators) {
            double weightGen = 0;
            if (generator instanceof GeneratorSynchronousModel) {
                double h = dynaWaltzParameters.getModelParameters(generator.getParameterSetId()).getDouble("generator_H");
                double snom = dynaWaltzParameters.getModelParameters(generator.getParameterSetId()).getDouble("generator_SNom");
                weightGen = h * snom;
            }
            ParametersXml.writeParameter(writer, DOUBLE, "weight_gen_" + index, Double.toString(weightGen));
            index++;
        }

        ParametersXml.writeParameter(writer, INT, "nbGen", Long.toString(omegaRefGenerators.size()));

        writer.writeEndElement();
    }

    private List<VarConnection> getVarConnectionsWithOmegaRefGenerator(OmegaRefGeneratorModel connected) {
        return connected.getOmegaRefVarConnections();
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        return connected.getNumCCVarName()
                .map(numCCVarName -> List.of(new VarConnection("numcc_node_@INDEX@", numCCVarName)))
                .orElse(Collections.emptyList());
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) throws PowsyblException {
        int index = 0;
        for (OmegaRefGeneratorModel gen : omegaRefGenerators) {
            adder.createMacroConnections(this, gen, getVarConnectionsWithOmegaRefGenerator(gen), MacroConnectAttribute.ofIndex1(index));
            adder.createMacroConnections(this, gen.getConnectableBusId(), BusModel.class, this::getVarConnectionsWithBus, MacroConnectAttribute.ofIndex1(index));
            index++;
        }
    }

    @Override
    public String getParFile(DynaWaltzContext context) {
        return context.getSimulationParFile();
    }
}
