/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.models.lines

import com.google.auto.service.AutoService
import com.powsybl.dynamicsimulation.DynamicModel
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension
import com.powsybl.dynawaltz.dsl.AbstractDynamicModelBuilder
import com.powsybl.dynawaltz.dsl.PowsyblDynawoGroovyExtension
import com.powsybl.dynawaltz.models.lines.StandardLine
import com.powsybl.iidm.network.Branch

/**
 * An implementation of {@link DynamicModelGroovyExtension} that adds the <pre>Line</pre> keyword to the DSL
 *
 * @author Dimitri Baudrier <dimitri.baudrier at rte-france.com>
 */
@AutoService(DynamicModelGroovyExtension.class)
class LineGroovyExtension extends PowsyblDynawoGroovyExtension<DynamicModel> implements DynamicModelGroovyExtension {

    LineGroovyExtension() {
        tags = ["Line"]
    }

    @Override
    protected LineBuilder createBuilder(String currentTag) {
        new LineBuilder()
    }

    static class LineBuilder extends AbstractDynamicModelBuilder {

        Branch.Side side

        LineBuilder() {
            side = Branch.Side.ONE
        }

        void side(Branch.Side side) {
            this.side = side
        }

        @Override
        StandardLine build() {
            setupBuild()
            new StandardLine(dynamicModelId, staticId, parameterSetId, side)
        }
    }
}
