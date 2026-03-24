/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynamicModelsXmlTest extends DynawoTestUtil {

    @Test
    void writeDynamicModel() throws SAXException, IOException {
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .outputVariables(outputVariables)
                .build();
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", "dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    @Test
    void writeDynamicModelWithLoadsAndOnlyOneFictitiousGenerator() throws SAXException, IOException {
        dynamicModels.clear();
        dynamicModels.add(BaseGeneratorBuilder.of(network)
                .staticId("GEN6")
                .parameterSetId("GF")
                .build());

        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .outputVariables(outputVariables)
                .build();

        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", "dyd_fictitious.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }
}
