/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.generators.SignalNGeneratorBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class SignalNXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMoreGenerators();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(SignalNGeneratorBuilder.of(network, "GeneratorPVRemoteDiagramPQSignalN")
                .staticId("GEN")
                .parameterSetId("n")
                .build());
        dynamicModels.add(SignalNGeneratorBuilder.of(network, "GeneratorPVRemoteDiagramPQSignalN")
                .staticId("GEN2")
                .parameterSetId("n")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "signal_n_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }
}
