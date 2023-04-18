/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons.phaseshifters;

import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.transformers.TransformerModel;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class PhaseShifterIAutomaton extends AbstractPhaseShifterAutomaton {

    public PhaseShifterIAutomaton(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId) {
        super(dynamicModelId, transformer, parameterSetId);
    }

    @Override
    public String getLib() {
        return "PhaseShifterI";
    }

    protected List<VarConnection> getVarConnectionsWithTransformer(TransformerModel connected) {
        return Arrays.asList(
                new VarConnection("phaseShifter_tap", connected.getStepVarName()),
                new VarConnection("phaseShifter_iMonitored", connected.getIMonitoredVarName()),
                new VarConnection("phaseShifter_AutomatonExists", connected.getDisableInternalTapChangerVarName())
        );
    }
}
