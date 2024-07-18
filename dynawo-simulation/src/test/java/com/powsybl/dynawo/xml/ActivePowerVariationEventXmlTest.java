/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ActivePowerVariationEventXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(SynchronizedGeneratorBuilder.of(network, "GeneratorPV")
                .dynamicModelId("BBM_GENC")
                .staticId("GEN2")
                .parameterSetId("GPV")
                .build());
        dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsGoverPropVRPropInt")
                .dynamicModelId("BBM_GENC2")
                .staticId("GEN3")
                .parameterSetId("GSTWPR")
                .build());
        dynamicModels.add(BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .dynamicModelId("BBM_LOADC")
                .staticId("LOAD2")
                .parameterSetId("load")
                .build());
        eventModels.add(EventActivePowerVariationBuilder.of(network)
                .staticId("GEN")
                .startTime(1)
                .deltaP(1.1)
                .build());
        eventModels.add(EventActivePowerVariationBuilder.of(network)
                .staticId("GEN2")
                .startTime(1)
                .deltaP(1.2)
                .build());
        eventModels.add(EventActivePowerVariationBuilder.of(network)
                .staticId("GEN3")
                .startTime(1)
                .deltaP(1.3)
                .build());
        eventModels.add(EventActivePowerVariationBuilder.of(network)
                .staticId("LOAD")
                .startTime(10)
                .deltaP(1.2)
                .build());
        eventModels.add(EventActivePowerVariationBuilder.of(network)
                .staticId("LOAD2")
                .startTime(10)
                .deltaP(1.3)
                .build());
    }

    @Test
    void writeDisconnectModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "apv_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "apv_par.xml", tmpDir.resolve(context.getSimulationParFile()));
        validate("parameters.xsd", "apv_network_par.xml", tmpDir.resolve("network.par"));
    }
}
