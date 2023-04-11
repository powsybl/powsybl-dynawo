/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.loads

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.AbstractPowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerTapChanger

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LoadOneTransformerTapChangerGroovyExtension extends AbstractPowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    LoadOneTransformerTapChangerGroovyExtension() {
        modelTags = ["LoadOneTransformerTapChanger"]
    }

    @Override
    protected LoadOneTransformerTapChangerBuilder createBuilder(String currentTag) {
        new LoadOneTransformerTapChangerBuilder()
    }

    static class LoadOneTransformerTapChangerBuilder extends AbstractDynamicModelBuilder {
        @Override
        LoadOneTransformerTapChanger build() {
            checkData()
            new LoadOneTransformerTapChanger(dynamicModelId, staticId, parameterSetId)
        }
    }
}
