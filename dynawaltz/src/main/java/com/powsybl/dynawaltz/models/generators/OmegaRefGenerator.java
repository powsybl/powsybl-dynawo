/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.models.VarConnection;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
public class OmegaRefGenerator extends AbstractGeneratorModel implements OmegaRefGeneratorModel {

    private final String generatorLib;

    public OmegaRefGenerator(String dynamicModelId, String staticId, String parameterSetId, String generatorLib) {
        super(dynamicModelId, staticId, parameterSetId,
                "generator_terminal",
                "generator_switchOffSignal1",
                "generator_switchOffSignal2",
                "generator_switchOffSignal3",
                "generator_running");
        this.generatorLib = generatorLib;
    }

    protected String getOmegaRefPuVarName() {
        return "generator_omegaRefPu";
    }

    @Override
    public List<VarConnection> getOmegaRefVarConnections() {
        return Arrays.asList(
                new VarConnection("omegaRef_grp_@INDEX@", getOmegaRefPuVarName()),
                new VarConnection("running_grp_@INDEX@", getRunningVarName())
        );
    }

    @Override
    public String getLib() {
        return generatorLib;
    }
}
