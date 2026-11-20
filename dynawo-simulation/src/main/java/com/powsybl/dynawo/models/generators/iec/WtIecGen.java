/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators.iec;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.iidm.network.Generator;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class WtIecGen extends AbstractIecGen {

    private static final VarMapping STATE = new VarMapping("WT_wT4Injector_state", "state");

    protected WtIecGen(Generator generator, String parameterSetId, ModelConfig modelConfig, MeasurementType measurementType) {
        super(generator, parameterSetId, modelConfig, List.of(
                new VarMapping("WT_%s_PPuSnRef".formatted(measurementType.getVariable()), "p"),
                new VarMapping("WT_%s_QPuSnRef".formatted(measurementType.getVariable()), "q"),
                STATE));
    }

    @Override
    protected String switchOffSignalNodeVarName() {
        return "WT_wT4Injector_switchOffSignal1";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "WT_wT4Injector_switchOffSignal2";
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return "WT_wT4Injector_switchOffSignal3";
    }

    @Override
    protected String getTerminalVarName() {
        return "WT_terminal";
    }

    @Override
    public String getUPuVarName() {
        return "WT_pll_UWTPu";
    }

    @Override
    public String getOmegaRefPuVarName() {
        return "WT_omegaRefPu";
    }

    @Override
    public String getRunningVarName() {
        return "WT_wT4Injector_running";
    }
}
