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
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class WppIecGen extends AbstractIecGen {

    private final String iecPrefix;
    private final List<VarMapping> varsMapping;

    protected WppIecGen(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig);
        this.iecPrefix = Objects.requireNonNull(modelConfig.internalModelPrefix());
        this.varsMapping = List.of(
                new VarMapping("WPP_" + iecPrefix + "CurrentSource_measurementsPQ_PPuSnRef", "p"),
                new VarMapping("WPP_" + iecPrefix + "CurrentSource_measurementsPQ_QPuSnRef", "q"),
                new VarMapping("WPP_" + iecPrefix + "CurrentSource_wT4Injector_state", "state"));
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
        return "WPP_" + iecPrefix + "CurrentSource_wT4Injector_UPu";
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return varsMapping;
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
