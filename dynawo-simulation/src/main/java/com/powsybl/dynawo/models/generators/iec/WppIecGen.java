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
public class WppIecGen extends AbstractIecGen {

    private final String iecPrefix;

    protected WppIecGen(Generator generator, String parameterSetId, ModelConfig modelConfig, MeasurementType measurementType, String iecPrefix) {
        super(generator, parameterSetId, modelConfig, List.of(
                new VarMapping("WPP_%sCurrentSource_%s_PPuSnRef".formatted(iecPrefix, measurementType.getVariable()), "p"),
                new VarMapping("WPP_%sCurrentSource_%s_QPuSnRef".formatted(iecPrefix, measurementType.getVariable()), "q"),
                new VarMapping("WPP_" + iecPrefix + "CurrentSource_wT4Injector_state", "state")));
        this.iecPrefix = iecPrefix;
    }

    @Override
    protected String switchOffSignalNodeVarName() {
        return "WPP_" + iecPrefix + "CurrentSource_wT4Injector_switchOffSignal1";
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "WPP_" + iecPrefix + "CurrentSource_wT4Injector_switchOffSignal2";
    }

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return "WPP_" + iecPrefix + "CurrentSource_wT4Injector_switchOffSignal3";
    }

    @Override
    protected String getTerminalVarName() {
        return "WPP_terminal";
    }

    @Override
    public String getUPuVarName() {
        return "WPP_elecMeasurements_UPu";
    }

    @Override
    public String getOmegaRefPuVarName() {
        return "WPP_omegaRefPu";
    }

    @Override
    public String getRunningVarName() {
        return "WPP_" + iecPrefix + "CurrentSource_wT4Injector_running";
    }
}
