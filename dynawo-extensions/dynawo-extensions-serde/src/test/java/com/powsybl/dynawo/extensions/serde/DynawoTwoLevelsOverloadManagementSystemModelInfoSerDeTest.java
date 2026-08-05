/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.serde;

import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynawo.extensions.api.model.DynawoTwoLevelsOverloadManagementSystemModelAdder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.TwoWindingsTransformer;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.serde.NetworkSerDe;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoTwoLevelsOverloadManagementSystemModelInfoSerDeTest extends AbstractSerDeTest {

    @Test
    void testXmlSerializer() throws IOException {
        Network network = EurostagTutorialExample1Factory.create();
        TwoWindingsTransformer tfo = network.getTwoWindingsTransformer("NGEN_NHV1");

        DynawoTwoLevelsOverloadManagementSystemModelAdder<TwoWindingsTransformer> adder = tfo.newExtension(DynawoTwoLevelsOverloadManagementSystemModelAdder.class);
        adder.withDynamicModelId("TLOMS")
                .withModelName("TwoLevelsOverloadManagementSystem")
                .withParameterSetId("tloms")
                .withIMeasurement1("NHV2_NLOAD")
                .withIMeasurement1Side(TwoSides.TWO)
                .withIMeasurement2("NHV1_NHV2_1")
                .withIMeasurement2Side(TwoSides.ONE)
                .add();

        network.setCaseDate(java.time.ZonedDateTime.parse("2016-12-07T11:18:52.881+01:00"));

        roundTripXmlTest(network,
                (n, p) -> n,
                NetworkSerDe::write,
                NetworkSerDe::validateAndRead,
                "/dynawoTwoLevelsOverloadManagementSystemModelInfo.xml");
    }
}
