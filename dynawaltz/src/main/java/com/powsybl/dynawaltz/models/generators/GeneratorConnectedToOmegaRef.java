/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.models.generators;

public class GeneratorConnectedToOmegaRef extends AbstractGeneratorModel implements GeneratorConnectedToOmegaRefModel {

    private final String generatorLib;

    public GeneratorConnectedToOmegaRef(String dynamicModelId, String staticId, String parameterSetId, String generatorLib,
                                        String terminalVarName, String switchOffSignalNodeVarName,
                                        String switchOffSignalEventVarName, String switchOffSignalAutomatonVarName,
                                        String runningVarName) {
        super(dynamicModelId, staticId, parameterSetId,
                terminalVarName, switchOffSignalNodeVarName,
                switchOffSignalEventVarName, switchOffSignalAutomatonVarName,
                runningVarName);
        this.generatorLib = generatorLib;
    }

    @Override
    public String getOmegaRefPuVarName() {
        return "generator_omegaRefPu";
    }

    @Override
    public String getLib() {
        return generatorLib;
    }
}
