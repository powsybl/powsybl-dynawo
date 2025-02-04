/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class ParametersXmlTest extends DynawoTestUtil {

    @Test
    void writeOmegaRef() throws SAXException, IOException {
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .dynawoParameters(DynawoSimulationParameters.load(PlatformConfig.defaultConfig(), fileSystem))
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();
        DydXml.write(tmpDir, context);
        ParametersXml.write(tmpDir, context);
        validate("parameters.xsd", "omega_ref.xml", tmpDir.resolve(context.getSimulationParFile()));
    }

}
