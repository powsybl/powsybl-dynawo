/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.dsl

import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractPureDynamicModelBuilder implements ModelBuilder<DynamicModel> {

    String dynamicModelId
    String parameterSetId

    void dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId
    }

    void parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId
    }

    void checkData() {
        if (!dynamicModelId) {
            throw new DslException("'dynamicModelId' field is not set")
        }
        if (!parameterSetId) {
            throw new DslException("'parameterSetId' field is not set")
        }
    }

    @Override
    abstract DynamicModel build();
}
