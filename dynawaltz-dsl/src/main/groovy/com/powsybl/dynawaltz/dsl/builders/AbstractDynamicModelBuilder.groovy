/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.builders

import com.powsybl.iidm.network.Network
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractDynamicModelBuilder {

    protected static final Logger LOGGER = LoggerFactory.getLogger("DynamicModelBuilder")

    protected final Network network
    protected boolean isInstantiable = true

    protected AbstractDynamicModelBuilder(Network network) {
        this.network = network
    }

    abstract protected void checkData()

    protected final boolean isInstantiable() {
        checkData()
        isInstantiable ? LOGGER.debug("${getLib()} instanciation successful")
             : LOGGER.warn("${getLib()} cannot be instantiated")
        isInstantiable
    }

    abstract protected String getLib()
}
