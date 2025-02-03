/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.frequencysynchronizers;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.BusOfFrequencySynchronizedModel;
import com.powsybl.dynawo.models.buses.DefaultBusOfFrequencySynchronized;
import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.parameters.ParametersSet;

import java.util.List;
import java.util.function.Consumer;

import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;
import static com.powsybl.dynawo.parameters.ParameterType.INT;

/**
 * OmegaRef is a special model: its role is to synchronize the generators' frequency. The corresponding black
 * box model XML entry is serialized only once. For each generator synchronized through the OmegaRef model, there will be
 * one XML entry for the connection with the generator's dynamic model, and one XML entry for the connection with the
 * NETWORK dynamic model. There are thus two macroConnectors defined for OmegaRef: one to connect it to a generator's
 * dynamic model and one to connect it to the NETWORK model.
 *
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class OmegaRef extends AbstractFrequencySynchronizer {

    private static final ModelConfig MODEL_CONFIG = new ModelConfig("DYNModelOmegaRef");

    private final DynawoSimulationParameters dynawoParameters;

    public OmegaRef(List<FrequencySynchronizedModel> synchronizedEquipments, String defaultParFile,
                    DynawoSimulationParameters dynawoParameters) {
        super(synchronizedEquipments, MODEL_CONFIG, defaultParFile);
        this.dynawoParameters = dynawoParameters;
    }

    @Override
    public void createDynamicModelParameters(Consumer<ParametersSet> parametersAdder) {
        ParametersSet paramSet = new ParametersSet(getParameterSetId());
        // The dynamic models are declared in the DYD following the order of dynamic models' supplier.
        // The OmegaRef parameters index the weight of each generator according to that declaration order.
        int index = 0;
        for (FrequencySynchronizedModel eq : synchronizedEquipments) {
            paramSet.addParameter("weight_gen_" + index, DOUBLE, Double.toString(eq.getWeightGen(dynawoParameters)));
            index++;
        }
        paramSet.addParameter("nbGen", INT, Long.toString(synchronizedEquipments.size()));
        parametersAdder.accept(paramSet);
    }

    private List<VarConnection> getVarConnectionsWith(FrequencySynchronizedModel connected) {
        return connected.getOmegaRefVarConnections();
    }

    private List<VarConnection> getVarConnectionsWithBus(BusOfFrequencySynchronizedModel connected) {
        return List.of(new VarConnection("numcc_node_@INDEX@", connected.getNumCCVarName()));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) throws PowsyblException {
        int index = 0;
        for (FrequencySynchronizedModel eq : synchronizedEquipments) {
            adder.createMacroConnections(this, eq, getVarConnectionsWith(eq), MacroConnectAttribute.ofIndex1(index));
            // If a bus with a dynamic model is found SetPoint is used in place of OmegaRef, thus at this point we don't have to handle dynamic model buses
            BusOfFrequencySynchronizedModel busOf = new DefaultBusOfFrequencySynchronized(eq.getConnectableBus().getId(), eq.getStaticId());
            adder.createMacroConnections(this, busOf, getVarConnectionsWithBus(busOf), MacroConnectAttribute.ofIndex1(index));
            index++;
        }
    }
}
