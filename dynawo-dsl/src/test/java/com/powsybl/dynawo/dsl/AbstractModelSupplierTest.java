/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.dsl;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.TestUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractModelSupplierTest {

    protected final ReportNode reportNode = ReportNode.newRootReportNode()
            .withAllResourceBundlesFromClasspath()
            .withMessageTemplate("dslTests", "DSL tests").build();

    protected InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(AbstractModelSupplierTest.class.getResourceAsStream(name));
    }

    protected void checkReportNode(String report) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(report, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
