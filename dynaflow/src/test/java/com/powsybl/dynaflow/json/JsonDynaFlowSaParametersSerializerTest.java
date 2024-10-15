/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow.json;

import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynaflow.DynaFlowSecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.json.JsonSecurityAnalysisParameters;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class JsonDynaFlowSaParametersSerializerTest extends AbstractSerDeTest {

    @Test
    void roundTripParameters() throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);
        SecurityAnalysisParameters parameters = SecurityAnalysisParameters.load(platformConfig);
        DynaFlowSecurityAnalysisParameters params = new DynaFlowSecurityAnalysisParameters()
                .setTimeOfEvent(23.);
        parameters.addExtension(DynaFlowSecurityAnalysisParameters.class, params);

        roundTripTest(parameters, JsonSecurityAnalysisParameters::write,
                JsonSecurityAnalysisParameters::read, "/dynaflow_sa_parameters_set_serialization.json");
    }

    @Test
    void serializeWithDefaultDynaflowSaParameters() throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);
        SecurityAnalysisParameters parameters = SecurityAnalysisParameters.load(platformConfig);
        roundTripTest(parameters, JsonSecurityAnalysisParameters::write,
                JsonSecurityAnalysisParameters::read, "/dynaflow_sa_default_serialization.json");
    }

    @Test
    void update() {
        SecurityAnalysisParameters parameters = SecurityAnalysisParameters.load();
        JsonSecurityAnalysisParameters.update(parameters, getClass().getResourceAsStream("/dynaflow_sa_update.json"));
        DynaFlowSecurityAnalysisParameters dynaFlowSaParameters = parameters.getExtension(DynaFlowSecurityAnalysisParameters.class);
        assertNotNull(dynaFlowSaParameters);
        assertEquals(14., dynaFlowSaParameters.getTimeOfEvent());
    }
}
