/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.builders

import com.powsybl.commons.reporter.Reporter
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynawaltz.dsl.ModelBuilder
import com.powsybl.dynawaltz.dsl.Reporters
import com.powsybl.iidm.network.Network

/**
 * Superclass for automaton and event model builders
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractPureDynamicModelBuilder extends AbstractDynamicModelBuilder implements ModelBuilder<DynamicModel> {

    protected String dynamicModelId
    protected String parameterSetId
    protected final String lib

    AbstractPureDynamicModelBuilder(Network network, String lib, Reporter reporter) {
        super(network, reporter)
        this.lib = lib
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
            Reporters.reportFieldNotSet(reporter, "dynamicModelId")
            isInstantiable = false
        }
        if (!parameterSetId) {
            Reporters.reportFieldNotSet(reporter, "dynamicModelId")
            isInstantiable = false
        }
    }

    @Override
    String getModelId() {
        dynamicModelId ?: "unknownDynamicId"
    }

    @Override
    abstract DynamicModel build()
}