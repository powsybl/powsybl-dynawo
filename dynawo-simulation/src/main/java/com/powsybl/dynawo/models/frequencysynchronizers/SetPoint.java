/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.frequencysynchronizers;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.parameters.ParametersSet;

import java.util.List;
import java.util.function.Consumer;

import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;

/**
 * Special generators' frequency synchronizer used when an Infinite Bus is present in the model.
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SetPoint extends AbstractFrequencySynchronizer {

    public SetPoint(List<FrequencySynchronizedModel> synchronizedEquipments, String defaultParFile) {
        super(synchronizedEquipments, "SetPoint", defaultParFile);
    }

    @Override
    public void createDynamicModelParameters(DynawoSimulationContext context, Consumer<ParametersSet> parametersAdder) {
        ParametersSet paramSet = new ParametersSet(getParameterSetId());
        paramSet.addParameter("setPoint_Value0", DOUBLE, Double.toString(1));
        parametersAdder.accept(paramSet);
    }

    private List<VarConnection> getVarConnectionsWith(FrequencySynchronizedModel connected) {
        return connected.getSetPointVarConnections();
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) throws PowsyblException {
        for (FrequencySynchronizedModel eq : synchronizedEquipments) {
            adder.createMacroConnections(this, eq, getVarConnectionsWith(eq));
        }
    }
}
