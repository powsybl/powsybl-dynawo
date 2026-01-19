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
import com.powsybl.dynawo.models.transformers.TransformerModel;
import com.powsybl.dynawo.parameters.ParameterType;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterPAutomationSystem extends AbstractPhaseShifterAutomationSystem {

    protected PhaseShifterPAutomationSystem(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId, ModelConfig modelConfig) {
        super(dynamicModelId, transformer, parameterSetId, modelConfig);
    }

    protected List<VarConnection> getVarConnectionsWith(TransformerModel connected) {
        return Arrays.asList(
                new VarConnection("phaseShifter_tap", connected.getStepVarName()),
                new VarConnection("phaseShifter_PMonitored", connected.getPMonitoredVarName()),
                new VarConnection("phaseShifter_AutomatonExists", connected.getDisableInternalTapChangerVarName())
        );
    }

    @Override
    public void updateDynamicModelParameters(ParameterUpdater updater) {
        updater.addReference(getParameterSetId(), "phaseShifter_P0", ParameterType.DOUBLE, "IIDM", "p1", transformer.getNameOrId());
        super.updateDynamicModelParameters(updater);
    }
}
