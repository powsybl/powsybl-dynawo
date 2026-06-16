/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.dynawo.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawo.models.events.EventReactivePowerVariationBuilder;
import com.powsybl.dynawo.models.events.EventReferenceVoltageVariationBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerTapChangerBuilder;
import com.powsybl.dynawo.models.loads.LoadTwoTransformersTapChangersBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class Dynawo18VersionXmlTest extends AbstractDynamicModelXmlTest {

    @Override
    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        network.getBusBreakerView().getBus("NLOAD")
                .setV(147.57861328125)
                .setAngle(-9.614486694335938);
    }

    @Override
    protected DynawoSimulationContext.Builder setupDynawoContextBuilder() {
        return super.setupDynawoContextBuilder().currentVersion(DynawoVersion.createFromString("1.8.0"));
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsGoverPropVRPropInt")
                .staticId("GEN")
                .parameterSetId("GSFWPR")
                .build());
        dynamicModels.add(LoadOneTransformerTapChangerBuilder.of(network, "LoadOneTransformerTapChanger")
                .staticId("LOAD")
                .parameterSetId("LOT")
                .build());
        dynamicModels.add(LoadTwoTransformersTapChangersBuilder.of(network, "LoadTwoTransformersTapChangers")
                .staticId("LOAD2")
                .parameterSetId("LOT")
                .build());
        dynamicModels.add(TapChangerBlockingAutomationSystemBuilder.of(network, "TapChangerBlockingAutomationSystem")
                .dynamicModelId("TCB")
                .parameterSetId("TCB")
                .transformers("LOAD", "LOAD2")
                .uMeasurements("NLOAD")
                .build());
        eventModels.add(EventActivePowerVariationBuilder.of(network)
                .staticId("GEN")
                .startTime(1)
                .deltaP(1.1)
                .build());
        eventModels.add(EventReactivePowerVariationBuilder.of(network)
                .staticId("LOAD3")
                .startTime(1)
                .deltaQ(1.1)
                .build());
        eventModels.add(EventReferenceVoltageVariationBuilder.of(network)
                .staticId("GEN")
                .startTime(1)
                .deltaU(1.1)
                .build());

    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "dynawo_1_8_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }
}
