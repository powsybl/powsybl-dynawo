/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */
class EmptyActivePowerVariationEventXmlTest extends AbstractDynamicModelXmlTest {

    protected void setupNetwork() {
        network = EurostagTutorialExample1Factory.create();
    }

    @Override
    protected void addDynamicModels() {
        dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousThreeWindingsPmConstVRNordic")
                .staticId("GEN")
                .parameterSetId("GSTWPR")
                .build());
        eventModels.add(EventActivePowerVariationBuilder.of(network)
                .staticId("GEN")
                .startTime(1)
                .deltaP(1.1)
                .build());
    }

    @Test
    void writeModel() throws SAXException, IOException {
        DydXml.write(tmpDir, context.getSimulationDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "apv_empty_dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
        validate("parameters.xsd", "empty_omega_ref_par.xml", tmpDir.resolve(context.getSimulationParFile()));
        checkReport("""
                + Test DYD
                   + Dynawo models processing
                      ActivePowerVariation ActivePowerVariation_GEN requires a connection with a PControllableEquipmentModel but dynamic model GeneratorSynchronousThreeWindingsPmConstVRNordic GEN does not implement it
                      ActivePowerVariation ActivePowerVariation_GEN connections cannot be created, the model will be skipped
                """);
    }
}
