/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.iidm.network.Generator;

/**
 * @author Dimitri Baudrier {@literal <dimitri.baudrier at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class GeneratorFictitious extends AbstractGenerator {

    public GeneratorFictitious(String dynamicModelId, Generator generator, String parameterSetId) {
        super(dynamicModelId, generator, parameterSetId, "GeneratorFictitious");
    }
}
