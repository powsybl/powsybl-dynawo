/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.models.automatons.currentLimits.CurrentLimitAutomatonBuilder;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class CurrentLimitAutomatonBuilderLimitModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(CurrentLimitAutomatonBuilder.of(network, "CurrentLimitAutomaton")
                .dynamicModelId("BBM_CLA_LINE")
                .parameterSetId("cla")
                .controlledQuadripole("NHV1_NHV2_1")
                .iMeasurement("NHV1_NHV2_1")
                .iMeasurementSide(TwoSides.ONE)
                .build());
        dynamicModels.add(CurrentLimitAutomatonBuilder.of(network, "CurrentLimitAutomaton")
                .dynamicModelId("BBM_CLA_TRANSFORMER")
                .parameterSetId("cla")
                .controlledQuadripole("NGEN_NHV1")
                .iMeasurement("NGEN_NHV1")
                .iMeasurementSide(TwoSides.TWO)
                .build());
        dynamicModels.add(new TransformerFixedRatio("BBM_TRANSFORMER", network.getTwoWindingsTransformer("NHV2_NLOAD"), "tf", "TransformerFixedRatio"));
        dynamicModels.add(CurrentLimitAutomatonBuilder.of(network, "CurrentLimitAutomaton")
                .dynamicModelId("BBM_CLA_TRANSFORMER2")
                .parameterSetId("cla")
                .controlledQuadripole("NHV1_NHV2_1")
                .iMeasurement("NHV2_NLOAD")
                .iMeasurementSide(TwoSides.TWO)
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException, XMLStreamException {
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "cla_dyd.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }
}