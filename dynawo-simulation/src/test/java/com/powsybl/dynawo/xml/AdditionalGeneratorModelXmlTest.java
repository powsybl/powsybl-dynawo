/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class AdditionalGeneratorModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void setupDynawoSimulationParameters() {
        try {
            Path additionalModels = Path.of(Objects.requireNonNull(getClass().getResource("/additionalModelsDyd.json")).toURI());
            dynawoParameters = DynawoSimulationParameters.load().setAdditionalModelsPath(additionalModels);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(BaseGeneratorBuilder.of(network, "BaseGenerator2")
                .staticId("GEN")
                .parameterSetId("g")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", "additional_generator_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }
}
