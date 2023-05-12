/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml.securityanalysis;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.SecurityAnalysisContext;
import com.powsybl.dynawaltz.xml.DynaWaltzTestUtil;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class ContingenciesXmlTest extends DynaWaltzTestUtil {

    @Test
    void writeDyds() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        List<Contingency> contingencies = List.of(
                Contingency.load("LOAD"),
                Contingency.builder("DisconnectLineGenerator")
                        .addLine("NHV1_NHV2_1")
                        .addGenerator("GEN2")
                        .build());
        SecurityAnalysisContext context = new SecurityAnalysisContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, parameters, dynawoParameters, contingencies);

        ContingenciesDydXml.write(tmpDir, context);
        ContingenciesParXml.write(tmpDir, context);
        validate("dyd.xsd", "securityanalysis/LOAD.xml", tmpDir.resolve("LOAD.dyd"));
        validate("dyd.xsd", "securityanalysis/DisconnectLineGenerator.xml", tmpDir.resolve("DisconnectLineGenerator.dyd"));
        validate("parameters.xsd", "securityanalysis/LOAD_par.xml", tmpDir.resolve("LOAD.par"));
        validate("parameters.xsd", "securityanalysis/DisconnectLineGenerator_par.xml", tmpDir.resolve("DisconnectLineGenerator.par"));
    }

}
