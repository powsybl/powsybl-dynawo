/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.loads

import com.powsybl.dsl.DslException
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractLoadModelBuilder extends AbstractDynamicModelBuilder {

    Load load

    AbstractLoadModelBuilder(Network network) {
        super(network)
    }

    void checkData() {
        super.checkData()
        load = network.getLoad(staticId)
        if (load == null) {
            throw new DslException("Load static id unknown: " + staticId)
        }
    }
}