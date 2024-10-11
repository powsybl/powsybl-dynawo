/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class OutputVariablesXmlTest extends DynawoTestUtil {

    @Test
    void writeOutputVariables() throws SAXException, IOException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load();
        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, outputVariables, parameters, dynawoParameters);

        OutputVariablesXml.writeCurve(tmpDir, context);
        validate("curvesInput.xsd", "curvesInput.xml", tmpDir.resolve(DynawoSimulationConstants.CRV_FILENAME));
        OutputVariablesXml.writeFsv(tmpDir, context);
        validate("fsvInput.xsd", "fsvInput.xml", tmpDir.resolve(DynawoSimulationConstants.FSV_FILENAME));
    }
}
