/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection;
import com.powsybl.dynawaltz.models.events.EventSetPointBoolean;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronous;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class EventXmlTest extends DynaWaltzTestUtil {

    @Test
    void writeDynamicModel() throws SAXException, IOException, XMLStreamException {
        dynamicModels.clear();
        dynamicModels.add(new GeneratorSynchronous("BBM_GEN2", network.getGenerator("GEN2"), "GSFWPR", "GeneratorSynchronousFourWindingsProportionalRegulations"));
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
        eventModels.add(new EventQuadripoleDisconnection("NHV1_NHV2_1", 5));
        eventModels.add(new EventQuadripoleDisconnection("NHV1_NHV2_1", 5, true, false));
        eventModels.add(new EventSetPointBoolean("GEN2", 1, true));
        eventModels.add(new EventSetPointBoolean("GEN2", 1, false));
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        Exception e = assertThrows(PowsyblException.class, () -> new DynaWaltzContext(network, workingVariantId, dynamicModels, eventModels, curves, null, null));
        //TODO fix TU
        assertEquals("Duplicate dynamicId: [Disconnect_NHV1_NHV2_1]", e.getMessage());
        //assertEquals("Duplicate dynamicId: [duplicateID, duplicateID2]", e.getMessage());
    }

}
