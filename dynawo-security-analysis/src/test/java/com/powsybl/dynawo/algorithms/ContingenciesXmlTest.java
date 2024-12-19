/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.algorithms.xml.ContingenciesDydXml;
import com.powsybl.dynawo.algorithms.xml.ContingenciesParXml;
import com.powsybl.dynawo.xml.DynawoTestUtil;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.security.SecurityAnalysisContext;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
//TODO replace SecurityAnalysisContext
class ContingenciesXmlTest extends DynawoTestUtil {

    @Test
    void writeDyds() throws SAXException, IOException, XMLStreamException {
        DynamicSecurityAnalysisParameters parameters = DynamicSecurityAnalysisParameters.load();
        parameters.setDynamicSimulationParameters(new DynamicSimulationParameters(0, 20));
        parameters.setDynamicContingenciesParameters(new DynamicSecurityAnalysisParameters.ContingenciesParameters(10));
        DynawoSimulationParameters dynawoSimulationParameters = DynawoSimulationParameters.load();
        List<Contingency> contingencies = List.of(
                Contingency.load("LOAD"),
                Contingency.builder("DisconnectLineGenerator")
                        .addLine("NHV1_NHV2_1")
                        .addGenerator("GEN2")
                        .build());
        SecurityAnalysisContext context = new SecurityAnalysisContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, parameters, dynawoSimulationParameters, contingencies);

        ContingenciesDydXml.write(tmpDir, context.getContingencyEventModels());
        ContingenciesParXml.write(tmpDir, context.getContingencyEventModels());
        validate("dyd.xsd", "LOAD.xml", tmpDir.resolve("LOAD.dyd"));
        validate("dyd.xsd", "DisconnectLineGenerator.xml", tmpDir.resolve("DisconnectLineGenerator.dyd"));
        validate("parameters.xsd", "LOAD_par.xml", tmpDir.resolve("LOAD.par"));
        validate("parameters.xsd", "DisconnectLineGenerator_par.xml", tmpDir.resolve("DisconnectLineGenerator.par"));
    }

}
