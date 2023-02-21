/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.test.AbstractConverterTest;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.NetworkXml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.powsybl.commons.test.ComparisonUtils.compareTxt;
import static org.junit.Assert.assertNotNull;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public abstract class AbstractDynawoCommonsTest extends AbstractConverterTest {

    protected void compare(Network expected, Network actual) throws IOException {
        Path pExpected = tmpDir.resolve("expected.xiidm");
        assertNotNull(pExpected);
        Path pActual = tmpDir.resolve("actual.xiidm");
        assertNotNull(pActual);
        NetworkXml.write(expected, pExpected);
        actual.setCaseDate(expected.getCaseDate());
        NetworkXml.write(actual, pActual);
        compareTxt(Files.newInputStream(pExpected), Files.newInputStream(pActual));
    }
}
