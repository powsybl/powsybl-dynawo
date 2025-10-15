/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.NetworkSerDe;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
abstract class AbstractDynawoCommonsTest extends AbstractSerDeTest {

    protected static final String ACTUAL_NETWORK_NAME = "actual.xiidm";
    protected static final String EXPECTED_NETWORK_NAME = "expected.xiidm";

    protected InputStream getInputStream(String expectedIidmResource) {
        return Objects.requireNonNull(getClass().getResourceAsStream(expectedIidmResource));
    }

    protected InputStream getActualNetworkInputStream(Network n) throws IOException {
        return getInputStream(n, tmpDir.resolve(ACTUAL_NETWORK_NAME));
    }

    protected InputStream getExpectedNetworkInputStream(Network n) throws IOException {
        return getInputStream(n, tmpDir.resolve(EXPECTED_NETWORK_NAME));
    }

    protected InputStream getInputStream(Network n, Path path) throws IOException {
        NetworkSerDe.write(n, path);
        return Files.newInputStream(path);
    }
}
