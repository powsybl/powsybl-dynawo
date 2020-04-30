/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class JobsXmlTest extends DynawoTestUtil {

    @Test
    public void writeJob() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load(platformConfig);
        parameters.addExtension(DynawoSimulationParameters.class, DynawoSimulationParameters.load(platformConfig));

        Network network = Network.create("test", "test");
        DynawoContext context = new DynawoContext(network, parameters);

        JobsXml.write(tmpDir, context);
        validate(tmpDir.resolve(DynawoConstants.JOBS_FILENAME), "jobs");
    }

}
