/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml.securityanalysis;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.SecurityAnalysisContext;
import com.powsybl.dynawaltz.xml.DynaWaltzConstants;
import com.powsybl.dynawaltz.xml.DynaWaltzTestUtil;
import com.powsybl.security.dynamic.DynamicSecurityAnalysisParameters;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class MultiplesJobsXmlTest extends DynaWaltzTestUtil {

    @Test
    void writeMultiplesJobs() throws SAXException, IOException, XMLStreamException {
        DynamicSecurityAnalysisParameters parameters = DynamicSecurityAnalysisParameters.load();
        DynaWaltzParameters dynawaltzParameters = DynaWaltzParameters.load();
        List<Contingency> contingencies = List.of(
                Contingency.load("LOAD"),
                Contingency.builder("DisconnectLineGenerator")
                        .addLine("NHV1_NHV2_1")
                        .addGenerator("GEN2")
                        .build());
        SecurityAnalysisContext context = new SecurityAnalysisContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, parameters, dynawaltzParameters, contingencies);

        MultipleJobsXml.write(tmpDir, context);
        validate("securityanalysis/multipleJobs.xsd", "securityanalysis/multipleJobs.xml", tmpDir.resolve(DynaWaltzConstants.MULTIPLE_JOBS_FILENAME));
    }

}
