/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.transformers

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.AbstractPowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class TransformerFixedRatioGroovyExtension extends AbstractPowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    TransformerFixedRatioGroovyExtension() {
        modelTags = ["TransformerFixedRatio"]
    }

    @Override
    protected TransformerBuilder createBuilder(String currentTag) {
        new TransformerBuilder(currentTag)
    }

    static class TransformerBuilder extends AbstractDynamicModelBuilder {

        String tag

        TransformerBuilder(String tag) {
            this.tag = tag
        }

        @Override
        TransformerFixedRatio build() {
            checkData()
            new TransformerFixedRatio(dynamicModelId, staticId, parameterSetId, tag)
        }
    }
}