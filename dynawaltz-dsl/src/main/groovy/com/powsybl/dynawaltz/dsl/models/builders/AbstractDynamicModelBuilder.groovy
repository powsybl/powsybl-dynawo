/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.dsl.models.builders

import com.powsybl.dsl.DslException
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynawaltz.dsl.ModelBuilder
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractDynamicModelBuilder implements ModelBuilder<DynamicModel> {

    Network network
    String dynamicModelId
    String staticId
    String parameterSetId

    AbstractDynamicModelBuilder(Network network) {
        this.network = network
    }

    void dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId
    }

    void staticId(String staticId) {
        this.staticId = staticId
    }

    void parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId
    }

    void checkData() {
        if (!staticId) {
            throw new DslException("'staticId' field is not set")
        }
        if (!parameterSetId) {
            throw new DslException("'parameterSetId' field is not set")
        }
        if (!dynamicModelId) {
            dynamicModelId = staticId
        }
    }

    @Override
    abstract DynamicModel build();
}
