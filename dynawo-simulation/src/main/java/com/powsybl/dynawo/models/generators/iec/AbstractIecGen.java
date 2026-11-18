/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators.iec;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawo.models.generators.SpecifiedGeneratorModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.utils.BusUtils;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractIecGen extends AbstractEquipmentBlackBoxModel<Generator> implements FrequencySynchronizedModel, SpecifiedGeneratorModel {

    enum MeasurementType {

        MEASUREMENT_PQ("measurementsPQ"),
        MEASUREMENT_CONTROL("controlMeasurements");

        private final String variable;

        MeasurementType(String variable) {
            this.variable = variable;
        }

        public String getVariable() {
            return variable;
        }
    }

    private final List<VarMapping> varsMapping;

    protected AbstractIecGen(Generator generator, String parameterSetId, ModelConfig modelConfig, List<VarMapping> varsMapping) {
        super(generator, parameterSetId, modelConfig);
        this.varsMapping = varsMapping;
    }

    protected abstract String switchOffSignalNodeVarName();

    protected abstract String getTerminalVarName();

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>();
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection(switchOffSignalNodeVarName(), switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    @Override
    public List<VarConnection> getSetPointVarConnections() {
        return List.of(new VarConnection("setPoint_setPoint", getOmegaRefPuVarName()));
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        return List.of(
                new VarConnection("omegaRef_grp_@INDEX@_", getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@_", getRunningVarName())
        );
    }

    @Override
    public Bus getConnectableBus() {
        return BusUtils.getConnectableBus(equipment);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return varsMapping;
    }
}
