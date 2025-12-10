/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawo.extensions.serde;

import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynawo.extensions.impl.generator.SynchronizedGeneratorPropertiesAdderImpl;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.serde.NetworkSerDe;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
class SynchronizedGeneratorPropertiesSerDeTest extends AbstractSerDeTest {

    @Test
    void testXmlSerializer() throws IOException {
        var network = EurostagTutorialExample1Factory.create();
        var generator = network.getGenerator("GEN");
        generator.newExtension(SynchronizedGeneratorPropertiesAdderImpl.class)
                .withRpcl2(true)
                .withType("t")
                .add();

        network.setCaseDate(java.time.ZonedDateTime.parse("2016-12-07T11:18:52.881+01:00"));
        String xmlRefFile = "/synchronizedGeneratorProperties.xml";

        roundTripXmlTest(network,
                (n, p) -> n,
                NetworkSerDe::write,
                NetworkSerDe::validateAndRead,
                xmlRefFile);
    }
}
