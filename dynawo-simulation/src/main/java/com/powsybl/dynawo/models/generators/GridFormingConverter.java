/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class GridFormingConverter extends AbstractEquipmentBlackBoxModel<Generator> implements FrequencySynchronizedModel {

    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("converter_PGenPu", "p"),
            new VarMapping("converter_QGenPu", "q"),
            new VarMapping("converter_state", "state"));

    protected GridFormingConverter(String dynamicModelId, Generator generator, String parameterSetId, String lib) {
        super(dynamicModelId, parameterSetId, generator, lib);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        return List.of(new VarConnection("converter_terminal", connected.getTerminalVarName()));
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
    public List<VarConnection> getOmegaRefVarConnections() {
        return List.of(
                new VarConnection("omega_grp_@INDEX@", getOmegaPuVarName()),
                new VarConnection("omegaRef_grp_@INDEX@", getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@", getRunningVarName())
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
