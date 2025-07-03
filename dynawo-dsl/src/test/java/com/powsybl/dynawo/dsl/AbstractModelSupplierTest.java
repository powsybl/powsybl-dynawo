/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.dsl;

import com.powsybl.commons.report.PowsyblCoreReportResourceBundle;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractModelSupplierTest {

    protected final ReportNode reportNode = ReportNode.newRootReportNode()
            .withLocale(Locale.US)
            .withResourceBundles(PowsyblCoreReportResourceBundle.BASE_NAME,
                    PowsyblDynawoReportResourceBundle.BASE_NAME,
                    PowsyblTestReportResourceBundle.TEST_BASE_NAME)
            .withMessageTemplate("testDSL")
            .build();

    protected InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(AbstractModelSupplierTest.class.getResourceAsStream(name));
    }

    protected void checkReportNode(String report) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(report, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
