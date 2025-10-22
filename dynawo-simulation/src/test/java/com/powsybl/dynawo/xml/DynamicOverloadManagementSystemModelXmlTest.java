/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynamicModelsConfigUtils;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static com.powsybl.iidm.network.test.EurostagTutorialExample1Factory.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynamicOverloadManagementSystemModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(DynamicOverloadManagementSystemBuilder.of(network, "OverloadManagementSystem")
                .dynamicModelId("BBM_CLA_LINE")
                .parameterSetId("cla")
                .controlledBranch(NHV1_NHV2_1)
                .iMeasurement(NHV2_NLOAD)
                .iMeasurementSide(TwoSides.ONE)
                .build());
        dynamicModels.add(DynamicOverloadManagementSystemBuilder.of(network, "OverloadManagementSystem")
                .dynamicModelId("BBM_CLA_TRANSFORMER")
                .parameterSetId("cla")
                .controlledBranch(NGEN_NHV1)
                .iMeasurement(NGEN_NHV1)
                .iMeasurementSide(TwoSides.TWO)
                .build());
        DynamicModelsConfigUtils.mandatoryModelsAdder(network, dynamicModels);
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", "cla_dyna_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }
}
