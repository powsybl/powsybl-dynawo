/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.iidm.network.TwoSides;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class EventXmlTest extends DynawoTestUtil {

    @Test
    void writeDynamicModel() throws SAXException, IOException {
        dynamicModels.clear();
        dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsProportionalRegulations")
                .dynamicModelId("BBM_GEN2")
                .staticId("GEN2")
                .parameterSetId("GSFWPR")
                .build());
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load();
        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(),
                dynamicModels, eventModels, outputVariables, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "events.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    @Test
    void duplicateEventId() {
        eventModels.clear();
        BlackBoxModel event1 = EventDisconnectionBuilder.of(network)
                .staticId("NHV1_NHV2_1")
                .startTime(5)
                .build();
        BlackBoxModel event2 = EventDisconnectionBuilder.of(network)
                .staticId("GEN2")
                .startTime(1)
                .build();
        BlackBoxModel event1Duplicate = EventDisconnectionBuilder.of(network)
                .staticId("NHV1_NHV2_1")
                .startTime(5)
                .disconnectOnly(TwoSides.ONE)
                .build();
        BlackBoxModel event2Duplicate = EventDisconnectionBuilder.of(network)
                .staticId("GEN2")
                .startTime(1)
                .build();
        eventModels.add(event1);
        eventModels.add(event2);
        eventModels.add(event1Duplicate);
        eventModels.add(event2Duplicate);
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        DynawoSimulationContext context = new DynawoSimulationContext(network, workingVariantId, dynamicModels, eventModels, outputVariables, DynamicSimulationParameters.load(), DynawoSimulationParameters.load());
        Assertions.assertThat(context.getBlackBoxEventModels()).containsExactly(event1, event2);
    }
}
