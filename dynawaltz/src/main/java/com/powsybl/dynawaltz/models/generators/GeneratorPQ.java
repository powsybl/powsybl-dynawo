/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.models.generators;

public class GeneratorPQ extends AbstractGeneratorConnectedToOmegaRefModel implements GeneratorConnectedToOmegaRefModel {

    public GeneratorPQ(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId,
                "generator_terminal",
                "generator_switchOffSignal1",
                "generator_switchOffSignal2",
                "generator_switchOffSignal3",
                "generator_running");
    }

    @Override
    public String getLib() {
        return "GeneratorPQ";
    }
}
