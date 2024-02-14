/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.automatons.currentlimits.CurrentLimitTwoLevelsAutomatonBuilder;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class CurrentLimitAutomatonBuilderLimitTwoLevelsModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(CurrentLimitTwoLevelsAutomatonBuilder.of(network, "CurrentLimitAutomatonTwoLevels")
                .dynamicModelId("BBM_CLA_TWO_LEVELS")
                .parameterSetId("cla")
                .controlledBranch("NGEN_NHV1")
                .iMeasurement1("NHV1_NHV2_1")
                .iMeasurement1Side(TwoSides.TWO)
                .iMeasurement2("NHV1_NHV2_2")
                .iMeasurement2Side(TwoSides.ONE)
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "cla_tl_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }
}
