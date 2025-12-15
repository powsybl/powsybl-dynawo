/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.loadsvariation;

import com.powsybl.dynawo.DynawoSimulationReports;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.loads.DefaultControllableLoadModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.powsybl.dynawo.parameters.ParameterType.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadVariationAreaAutomationSystem extends AbstractPureDynamicBlackBoxModel {

    private static final String ID = "LOAD_VARIATION_AREA";
    private static final String PAR_ID = "LOAD_VAR_AREA";
    private static final ModelConfig MODEL_CONFIG = new ModelConfig("DYNModelVariationArea");

    private final List<LoadsVariation> loadsVariations;
    private final double loadIncreaseStartTime;
    private final double loadIncreaseStopTime;
    private final BiConsumer<LoadsProportionalScalable, Double> scalingConfig;

    public LoadVariationAreaAutomationSystem(List<LoadsVariation> loadsVariations, double loadIncreaseStartTime,
                                             double loadIncreaseStopTime,
                                             BiConsumer<LoadsProportionalScalable, Double> scalingConfig) {
        super(ID, PAR_ID, MODEL_CONFIG);
        this.loadsVariations = Objects.requireNonNull(loadsVariations);
        this.loadIncreaseStartTime = loadIncreaseStartTime;
        this.loadIncreaseStopTime = loadIncreaseStopTime;
        this.scalingConfig = scalingConfig;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        int index = 0;
        for (LoadsVariation lv : loadsVariations) {
            for (Load load : lv.loads()) {
                boolean isSkipped = adder.createMacroConnectionsOrSkip(this, load, DefaultControllableLoadModel.class, this::getVarConnectionsWith, MacroConnectAttribute.ofIndex1(index));
                if (!isSkipped) {
                    index++;
                } else {
                    DynawoSimulationReports.reportFailedDefaultModelHandling(adder.getReportNode(), getName(), getDynamicModelId(), IdentifiableType.LOAD.toString());
                }
            }
        }
    }

    private List<VarConnection> getVarConnectionsWith(DefaultControllableLoadModel connected) {
        return List.of(new VarConnection("DeltaPc_load_@INDEX@_value", connected.getDeltaPVarName()),
                new VarConnection("DeltaQc_load_@INDEX@_value", connected.getDeltaQVarName()));
    }

    @Override
    public void createDynamicModelParameters(Consumer<ParametersSet> parametersAdder) {
        ParametersSet paramSet = new ParametersSet(getParameterSetId());
        int index = 0;
        for (LoadsVariation lv : loadsVariations) {
            LoadsProportionalScalable proportionalScalable = new LoadsProportionalScalable(lv.loads());
            scalingConfig.accept(proportionalScalable, lv.variationValue());
            for (CalculatedLoadScalable load : proportionalScalable.getLoadScalable()) {
                paramSet.addParameter("deltaP_load_" + index, DOUBLE, String.valueOf(load.getCalculatedDeltaP()));
                paramSet.addParameter("deltaQ_load_" + index, DOUBLE, String.valueOf(load.getCalculatedDeltaQ()));
                index++;
            }
        }
        paramSet.addParameter("nbLoads", INT, String.valueOf(index));
        paramSet.addParameter("startTime", DOUBLE, String.valueOf(loadIncreaseStartTime));
        paramSet.addParameter("stopTime", DOUBLE, String.valueOf(loadIncreaseStopTime));
        parametersAdder.accept(paramSet);
    }

    @Override
    public void createNetworkParameter(ParametersSet networkParameters) {
        for (LoadsVariation lv : loadsVariations) {
            for (Load load : lv.loads()) {
                networkParameters.addParameter(load.getId() + "_isControllable", BOOL, Boolean.toString(true));
            }
        }
    }
}
