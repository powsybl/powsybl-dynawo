/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.builders.EventModelsBuilderUtils;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.iidm.network.TwoSides;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class EventXmlTest extends DynaWaltzTestUtil {

    @Test
    void writeDynamicModel() throws SAXException, IOException {
        dynamicModels.clear();
        dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsProportionalRegulations")
                .dynamicModelId("BBM_GEN2")
                .staticId("GEN2")
                .parameterSetId("GSFWPR")
                .build());
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(),
                dynamicModels, eventModels, curves, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "events.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

    @Test
    void duplicateEventId() {
        eventModels.clear();
        BlackBoxModel event1 = EventModelsBuilderUtils.newEventDisconnectionBuilder(network)
                .staticId("NHV1_NHV2_1")
                .startTime(5)
                .build();
        BlackBoxModel event2 = EventModelsBuilderUtils.newEventDisconnectionBuilder(network)
                .staticId("GEN2")
                .startTime(1)
                .build();
        BlackBoxModel event1Duplicate = EventModelsBuilderUtils.newEventDisconnectionBuilder(network)
                .staticId("NHV1_NHV2_1")
                .startTime(5)
                .disconnectOnly(TwoSides.ONE)
                .build();
        BlackBoxModel event2Duplicate = EventModelsBuilderUtils.newEventDisconnectionBuilder(network)
                .staticId("GEN2")
                .startTime(1)
                .build();
        eventModels.add(event1);
        eventModels.add(event2);
        eventModels.add(event1Duplicate);
        eventModels.add(event2Duplicate);
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        DynaWaltzContext context = new DynaWaltzContext(network, workingVariantId, dynamicModels, eventModels, curves, DynamicSimulationParameters.load(), DynaWaltzParameters.load());
        Assertions.assertThat(context.getBlackBoxEventModels()).containsExactly(event1, event2);
    }
}
