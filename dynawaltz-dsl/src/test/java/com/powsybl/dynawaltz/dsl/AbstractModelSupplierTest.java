/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl;

import com.powsybl.commons.reporter.ReporterModel;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractModelSupplierTest {

    protected final ReporterModel reporter = new ReporterModel("dslTests", "DSL tests");

    protected InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(AbstractModelSupplierTest.class.getResourceAsStream(name));
    }

    protected void checkReporter(String report) {
        StringWriter sw = new StringWriter();
        reporter.export(sw);
        assertEquals(report, sw.toString());
    }
}
