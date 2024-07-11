/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.loads.ControllableLoadModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Load;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.powsybl.dynawo.parameters.ParameterType.DOUBLE;
import static com.powsybl.dynawo.parameters.ParameterType.INT;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadVariationAreaAutomationSystem extends AbstractPureDynamicBlackBoxModel {

    private static final String ID = "LOAD_VARIATION_AREA";
    private static final String PAR_ID = "loadVarArea";
    private static final String LIB = "DYNModelVariationArea";

    private final List<Load> loads;
    private final double loadIncreaseStartTime;
    private final double loadIncreaseStopTime;


    public LoadVariationAreaAutomationSystem(List<Load> loads, double loadIncreaseStartTime, double loadIncreaseStopTime) {
        super(ID, PAR_ID, LIB);
        this.loads = Objects.requireNonNull(loads);
        this.loadIncreaseStartTime = loadIncreaseStartTime;
        this.loadIncreaseStopTime = loadIncreaseStopTime;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        int index = 0;
        for (Load load : loads) {
            adder.createMacroConnections(this, load, ControllableLoadModel.class, this::getVarConnectionsWith, MacroConnectAttribute.ofIndex1(index));
            index++;
        }
    }

    private List<VarConnection> getVarConnectionsWith(ControllableLoadModel connected) {
        return List.of(new VarConnection("DeltaPc_load_@INDEX@", connected.getDeltaPVarName()),
                new VarConnection("DeltaQc_load_@INDEX@", connected.getDeltaQVarName()));
    }

    @Override
    public void createDynamicModelParameters(DynawoSimulationContext context, Consumer<ParametersSet> parametersAdder) {
        ParametersSet paramSet = new ParametersSet(getParameterSetId());
        paramSet.addParameter("nbLoads", INT, String.valueOf(loads.size()));
        paramSet.addParameter("startTime", DOUBLE, String.valueOf(loadIncreaseStartTime));
        paramSet.addParameter("stopTime", DOUBLE, String.valueOf(loadIncreaseStopTime));
        //TODO calc delta
        int index = 0;
        for (Load load : loads) {
            paramSet.addParameter("deltaP_load_" + index, DOUBLE, String.valueOf(1));
            paramSet.addParameter("deltaQ_load_" + index, DOUBLE, String.valueOf(1));
            index++;
        }
        parametersAdder.accept(paramSet);
    }
}
