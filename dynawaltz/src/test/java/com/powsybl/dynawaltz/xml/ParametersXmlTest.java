/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class ParametersXmlTest extends DynaWaltzTestUtil {

    @Test
    public void writeOmegaRef() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        DynaWaltzContext context = new DynaWaltzContext(network, dynamicModels, eventModels, curves, parameters, dynawoParameters);
        DynaWaltzXmlContext xmlContext = new DynaWaltzXmlContext(context);

        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("parameters.xsd", "omega_ref.xml", tmpDir.resolve(xmlContext.getSimulationParFile()));
    }

}