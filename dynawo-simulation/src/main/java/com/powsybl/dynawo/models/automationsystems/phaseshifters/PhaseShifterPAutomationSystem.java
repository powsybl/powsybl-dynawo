/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems.phaseshifters;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.ParameterUpdater;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.automationsystems.ConnectionStatefulModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.transformers.TransformerModel;
import com.powsybl.dynawo.parameters.ParameterType;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterPAutomationSystem extends AbstractPhaseShifterAutomationSystem implements PhaseShifterPModel, ConnectionStatefulModel {

    private ConnectionState connection = null;

    protected PhaseShifterPAutomationSystem(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId, ModelConfig modelConfig) {
        super(dynamicModelId, transformer, parameterSetId, modelConfig);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        if (connection == null) {
            super.createMacroConnections(adder);
            connection = ConnectionState.CONNECTED;
        }
    }

    protected List<VarConnection> getVarConnectionsWith(TransformerModel connected) {
        return Arrays.asList(
                new VarConnection("phaseShifter_tap", connected.getStepVarName()),
                new VarConnection("phaseShifter_PMonitored", connected.getPMonitoredVarName()),
                new VarConnection("phaseShifter_AutomatonExists", connected.getDisableInternalTapChangerVarName())
        );
    }

    @Override
    public ConnectionState getConnectionState() {
        return connection;
    }

    @Override
    public boolean connect(MacroConnectionsAdder adder) {
        createMacroConnections(adder);
        return ConnectionState.CONNECTED == getConnectionState();
    }

    @Override
    public TwoWindingsTransformer getConnectedTransformer() {
        return transformer;
    }

    @Override
    public String getLockedVarName() {
        return "phaseShifter_locked";
    }

    @Override
    public void updateDynamicModelParameters(ParameterUpdater updater) {
        String psId = getParameterSetId(); // par exemple "phase_shifter_p_par"
        String tfoId = transformer.getNameOrId();

        updater.addReference(psId, "phaseShifter_P0", ParameterType.DOUBLE, "IIDM", "p1", tfoId);
        updater.addReference(psId, "phaseShifter_increasePhase", ParameterType.INT, "IIDM", "increasePhase", tfoId);
        updater.addReference(psId, "phaseShifter_regulating0", ParameterType.BOOL, "IIDM", "regulating", tfoId);
        updater.addReference(psId, "phaseShifter_tap0", ParameterType.INT, "IIDM", "tapPosition", tfoId);
        updater.addReference(psId, "phaseShifter_tapMax", ParameterType.INT, "IIDM", "highTapPosition", tfoId);
        updater.addReference(psId, "phaseShifter_tapMin", ParameterType.INT, "IIDM", "lowTapPosition", tfoId);
    }
}
