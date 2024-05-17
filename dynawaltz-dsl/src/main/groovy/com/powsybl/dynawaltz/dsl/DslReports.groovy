/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl

import com.powsybl.commons.report.ReportNode

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
final class DslReports {

    private DslReports() {
    }

    static ReportNode createModelBuilderReportNode(ReportNode reportNode, String name) {
        reportNode.newReportNode()
                .withMessageTemplate("DSLModelBuilder", 'DSL model builder for ${name}')
                .withUntypedValue("name", name)
                .add()
    }
}
