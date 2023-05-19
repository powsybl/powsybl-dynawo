/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.models.events.ControllableEquipment;
import com.powsybl.iidm.network.Generator;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class SynchronizedGeneratorControllable extends SynchronizedGenerator implements ControllableEquipment {

    public SynchronizedGeneratorControllable(String dynamicModelId, Generator generator, String parameterSetId, String generatorLib) {
        super(dynamicModelId, generator, parameterSetId, generatorLib);
    }

    @Override
    public String getDeltaPVarName() {
        return "generator_deltaPmRefPu_value";
    }
}
