/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.dynawo.models.versionablevariable.VersionableHandler;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.powsybl.dynawo.models.versionablevariable.GlobalVersionableVariables.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class GridFormingConverter extends AbstractEquipmentBlackBoxModel<Generator> implements FrequencySynchronizedModel, SpecifiedGeneratorModel {

    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("converter_PGenPu", "p"),
            new VarMapping("converter_QGenPu", "q"),
            new VarMapping("converter_state", "state"));

    protected GridFormingConverter(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>();
        varConnections.add(new VarConnection("converter_terminal", connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection("converter_switchOffSignal1", switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    @Override
    public List<VarConnection> getSetPointVarConnections() {
        return List.of(new VarConnection(
                VersionableHandler.getInstance().getCurrentVariable(VARIABLE_VALUE, "setPoint_setPoint"),
                getOmegaRefPuVarName()));
    }

    protected String getOmegaPuVarName() {
        return "converter_omegaPu";
    }

    @Override
    public String getOmegaRefPuVarName() {
        return "control_omegaRefPu";
    }

    @Override
    public String getRunningVarName() {
        return "converter_running";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "converter_switchOffSignal2";
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return "converter_switchOffSignal3";
    }

    @Override
    public String getUPuVarName() {
        return "converter_UPu";
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        VersionableHandler versionableHandler = VersionableHandler.getInstance();
        return List.of(
                new VarConnection(versionableHandler.getCurrentVariable(INDEXED_VARIABLE_VALUE, "omega_grp"), getOmegaPuVarName()),
                new VarConnection(versionableHandler.getCurrentVariable(INDEXED_VARIABLE_VALUE, "omegaRef_grp"), getOmegaRefPuVarName()),
                new VarConnection(versionableHandler.getCurrentVariable(INDEXED_VARIABLE, "running_grp"), getRunningVarName())
        );
    }

    @Override
    public double getWeightGen(DynawoSimulationParameters dynawoSimulationParameters) {
        return dynawoSimulationParameters.getModelParameters(getParameterSetId()).getDouble("converter_SNom");
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public Bus getConnectableBus() {
        return BusUtils.getConnectableBus(equipment);
    }
}
