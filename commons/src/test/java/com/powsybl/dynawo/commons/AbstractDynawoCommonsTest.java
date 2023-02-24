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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.commons.test.ComparisonUtils.compareTxt;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public abstract class AbstractDynawoCommonsTest extends AbstractConverterTest {

    protected void compare(String expectedIidmResource, Network actual) throws IOException {
        InputStream expected = Objects.requireNonNull(getClass().getResourceAsStream(expectedIidmResource));
        compareTxt(expected, getInputStream(actual, tmpDir.resolve("actual.xiidm")));
    }

    protected void compare(Network expected, Network actual) throws IOException {
        compareTxt(getInputStream(expected, tmpDir.resolve("expected.xiidm")),
                getInputStream(actual, tmpDir.resolve("actual.xiidm")));
    }

    private InputStream getInputStream(Network n, Path path) throws IOException {
        NetworkXml.write(n, path);
        return Files.newInputStream(path);
    }
}
