/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.builders

import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynawaltz.dsl.ModelBuilder
import com.powsybl.iidm.network.Network

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractPureDynamicModelBuilder extends AbstractDynamicModelBuilder implements ModelBuilder<DynamicModel> {

    protected String dynamicModelId
    protected String parameterSetId
    protected final String lib

    AbstractPureDynamicModelBuilder(Network network, String lib) {
        super(network)
        this.lib = lib
        LOGGER.info("Automaton $lib building...")
    }

    void dynamicModelId(String dynamicModelId) {
        this.dynamicModelId = dynamicModelId
    }

    void parameterSetId(String parameterSetId) {
        this.parameterSetId = parameterSetId
    }

    @Override
    protected void checkData() {
        if (!dynamicModelId) {
            LOGGER.warn("'dynamicModelId' field is not set")
            isInstantiable = false
        }
        if (!parameterSetId) {
            LOGGER.warn("'parameterSetId' field is not set")
            isInstantiable = false
        }
    }

    @Override
    protected String getLib() {
        lib
    }

    @Override
    abstract DynamicModel build()
}
