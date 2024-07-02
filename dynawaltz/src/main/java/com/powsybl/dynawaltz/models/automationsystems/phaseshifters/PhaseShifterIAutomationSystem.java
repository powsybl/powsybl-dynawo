/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automationsystems.phaseshifters;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.automationsystems.ConnectionStatefulModel;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.transformers.TransformerModel;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterIAutomationSystem extends AbstractPhaseShifterAutomationSystem implements PhaseShifterIModel, ConnectionStatefulModel {

    private ConnectionState connection = ConnectionState.NOT_SET;

    protected PhaseShifterIAutomationSystem(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId, String lib) {
        super(dynamicModelId, transformer, parameterSetId, lib);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        if (ConnectionState.NOT_SET == connection) {
            super.createMacroConnections(adder);
            connection = ConnectionState.CONNECTED;
        }
    }

    protected List<VarConnection> getVarConnectionsWith(TransformerModel connected) {
        return Arrays.asList(
                new VarConnection("phaseShifter_tap", connected.getStepVarName()),
                new VarConnection("phaseShifter_iMonitored", connected.getIMonitoredVarName()),
                new VarConnection("phaseShifter_P", connected.getPMonitoredVarName()),
                new VarConnection("phaseShifter_AutomatonExists", connected.getDisableInternalTapChangerVarName())
        );
    }

    @Override
    public boolean isConnectedOrConnect(MacroConnectionsAdder adder) {
        createMacroConnections(adder);
        return ConnectionState.CONNECTED == connection;
    }

    @Override
    public TwoWindingsTransformer getConnectedTransformer() {
        return transformer;
    }

    @Override
    public String getLockedVarName() {
        return "phaseShifter_locked";
    }
}
