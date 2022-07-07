/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public abstract class AbstractGeneratorBasicModel extends AbstractGeneratorModel {

    protected AbstractGeneratorBasicModel(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId,
                "generator_terminal",
                "generator_switchOffSignal1",
                "generator_switchOffSignal2",
                "generator_switchOffSignal3",
                "generator_omegaPu",
                "generator_omegaRefPu",
                "generator_running");
    }
}
