/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class OmegaRefModelXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsPmConstVRNordic")
                .dynamicModelId("BBM_GEN_SYNCHRO")
                .staticId("GEN")
                .parameterSetId("GSFW")
                .build());
        dynamicModels.add(SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                .dynamicModelId("BBM_GEN_PQ")
                .staticId("GEN2")
                .parameterSetId("GPQ")
                .build());
        dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousThreeWindingsPmConstVRNordic")
                .dynamicModelId("BBM_GEN_SYNCHRO2")
                .staticId("GEN3")
                .parameterSetId("GSTW")
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "omega_ref_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "omega_ref_par.xml", tmpDir.resolve(context.getSimulationParFile()));
    }
}
