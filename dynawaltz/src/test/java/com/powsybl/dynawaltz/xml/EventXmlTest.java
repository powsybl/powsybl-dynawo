/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.dynamicmodels.staticref.generators.GeneratorSynchronousFourWindingsProportionalRegulations;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class EventXmlTest extends DynaWaltzTestUtil {

    @Test
    public void writeDynamicModel() throws SAXException, IOException, XMLStreamException {
        List<DynamicModel> dynamicModels = List.of(
                new GeneratorSynchronousFourWindingsProportionalRegulations("BBM_GEN2", "GEN2", "GSFWPR")
        );
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(),
                dynamicModels, eventModels, curves, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "events.xml", tmpDir.resolve(DynaWaltzConstants.DYD_FILENAME));
    }

}
