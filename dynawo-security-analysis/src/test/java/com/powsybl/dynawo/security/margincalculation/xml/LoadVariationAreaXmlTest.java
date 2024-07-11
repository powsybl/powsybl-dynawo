/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security.margincalculation.xml;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.margincalculation.MarginCalculationContext;
import com.powsybl.dynawo.xml.DydXml;
import com.powsybl.dynawo.xml.DynawoTestUtil;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class LoadVariationAreaXmlTest extends DynawoTestUtil {

    @Test
    void writeDyd() throws SAXException, IOException {
        DynamicSecurityAnalysisParameters parameters = DynamicSecurityAnalysisParameters.load();
        parameters.setDynamicSimulationParameters(new DynamicSimulationParameters(0, 20));
        parameters.setDynamicContingenciesParameters(new DynamicSecurityAnalysisParameters.ContingenciesParameters(10));
        DynawoSimulationParameters dynawoSimulationParameters = DynawoSimulationParameters.load();
        MarginCalculationContext context = new MarginCalculationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, parameters, dynawoSimulationParameters, Collections.emptyList());

        DydXml.write(tmpDir, context.getLoadVariationAreaDydData());
        ParametersXml.write(tmpDir, context);
        validate("dyd.xsd", "LOAD_VARIATION_AREA.xml", tmpDir.resolve("LOAD_VARIATION_AREA.dyd"));
        validate("parameters.xsd", "omega_ref.xml", tmpDir.resolve(context.getSimulationParFile()));
    }

}
