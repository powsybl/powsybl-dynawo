/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class ParametersXmlTest extends DynaWaltzTestUtil {

    @Test
    void writeOmegaRef() throws SAXException, IOException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load(PlatformConfig.defaultConfig(), fileSystem);
        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("parameters.xsd", "omega_ref.xml", tmpDir.resolve(context.getSimulationParFile()));
    }

}
