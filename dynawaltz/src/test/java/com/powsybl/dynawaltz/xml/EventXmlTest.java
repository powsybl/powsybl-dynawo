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
import com.powsybl.dynawaltz.models.events.EventInjectionDisconnection;
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection;
import com.powsybl.dynawaltz.models.generators.SynchronousGenerator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class EventXmlTest extends DynaWaltzTestUtil {

    @Test
    void writeDynamicModel() throws SAXException, IOException, XMLStreamException {
        dynamicModels.clear();
        dynamicModels.add(new SynchronousGenerator("BBM_GEN2", network.getGenerator("GEN2"), "GSFWPR", "GeneratorSynchronousFourWindingsProportionalRegulations"));
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
        EventQuadripoleDisconnection event1 = new EventQuadripoleDisconnection(network.getLine("NHV1_NHV2_1"), 5);
        EventInjectionDisconnection event2 = new EventInjectionDisconnection(network.getGenerator("GEN2"), 1, true);
        eventModels.add(event1);
        eventModels.add(new EventQuadripoleDisconnection(network.getLine("NHV1_NHV2_1"), 5, true, false));
        eventModels.add(event2);
        eventModels.add(new EventInjectionDisconnection(network.getGenerator("GEN2"), 1, false));
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        DynaWaltzContext context = new DynaWaltzContext(network, workingVariantId, dynamicModels, eventModels, curves, DynamicSimulationParameters.load(), DynaWaltzParameters.load());
        Assertions.assertThat(context.getBlackBoxEventModels()).containsExactly(event1, event2);
    }
}
