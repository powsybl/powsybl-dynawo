/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawo.extensions.serde;

import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynawo.commons.TransformerSide;
import com.powsybl.dynawo.extensions.api.model.DynawoTapChangerModelAdder;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.serde.NetworkSerDe;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoTapChangerModelInfoSerDeTest extends AbstractSerDeTest {

    @Test
    void testXmlSerializer() throws IOException {
        Network network = EurostagTutorialExample1Factory.create();
        Load load = network.getLoad("LOAD");

        load.newExtension(DynawoTapChangerModelAdder.class)
                .withDynamicModelId("TC")
                .withModelName("TapChangerAutomationSystem")
                .withParameterSetId("tc")
                .withSide(TransformerSide.HIGH_VOLTAGE)
                .add();

        network.setCaseDate(java.time.ZonedDateTime.parse("2016-12-07T11:18:52.881+01:00"));

        roundTripXmlTest(network,
                (n, p) -> n,
                NetworkSerDe::write,
                NetworkSerDe::validateAndRead,
                "/dynawoTapChangerModelInfo.xml");
    }
}
