/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.buses

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.AbstractPowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.buses.StandardBus

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>Bus</pre> keyword to the DSL
 *
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class BusGroovyExtension extends AbstractPowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    BusGroovyExtension() {
        tags = ["Bus"]
    }

    @Override
    protected BusBuilder createBuilder(String currentTag) {
        new BusBuilder()
    }

    static class BusBuilder extends AbstractDynamicModelBuilder {

        @Override
        StandardBus build() {
            setupBuild()
            new StandardBus(dynamicModelId, staticId, parameterSetId)
        }
    }
}
