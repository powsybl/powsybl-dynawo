/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.generators

import com.powsybl.dsl.DslException
import com.powsybl.dynawaltz.dsl.models.builders.AbstractDynamicModelBuilder
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Network

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
abstract class AbstractGeneratorBuilder extends AbstractDynamicModelBuilder {

    Generator generator

    AbstractGeneratorBuilder(Network network) {
        super(network)
    }

    void checkData() {
        super.checkData()
        generator = network.getGenerator(staticId)
        if (generator == null) {
            throw new DslException("Generator static id unknown: " + staticId)
        }
    }
}