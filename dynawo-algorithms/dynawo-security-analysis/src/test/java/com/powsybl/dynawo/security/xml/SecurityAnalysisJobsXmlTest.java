/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security.xml;

import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.security.SecurityAnalysisContext;
import com.powsybl.dynawo.xml.DynawoTestUtil;
import com.powsybl.dynawo.xml.JobsXml;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class SecurityAnalysisJobsXmlTest extends DynawoTestUtil {

    @Test
    void testJobXml() throws IOException, SAXException {
        DynawoSimulationContext context = new SecurityAnalysisContext.Builder(network, dynamicModels, Collections.emptyList()).build();
        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", "saJobs.xml", tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME), true);
    }
}
