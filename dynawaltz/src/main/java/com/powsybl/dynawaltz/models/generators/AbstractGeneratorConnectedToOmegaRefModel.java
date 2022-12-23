/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.models.generators;

public abstract class AbstractGeneratorConnectedToOmegaRefModel extends AbstractGeneratorModel implements GeneratorConnectedToOmegaRefModel {

    protected AbstractGeneratorConnectedToOmegaRefModel(String dynamicModelId, String staticId, String parameterSetId,
                                                        String terminalVarName, String switchOffSignalNodeVarName,
                                                        String switchOffSignalEventVarName, String switchOffSignalAutomatonVarName,
                                                        String runningVarName) {
        super(dynamicModelId, staticId, parameterSetId,
                terminalVarName, switchOffSignalNodeVarName,
                switchOffSignalEventVarName, switchOffSignalAutomatonVarName,
                runningVarName);
    }

    @Override
    public String getOmegaRefPuVarName() {
        return "generator_omegaRefPu";
    }
}
