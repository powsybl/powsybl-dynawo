/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow.json;

import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.dynaflow.DynaflowParameters;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class JsonDynaflowParametersSerializerTest extends AbstractConverterTest {

    @Test
    public void testDeserialize() {
        LoadFlowParameters lfParameters = LoadFlowParameters.load();
        JsonLoadFlowParameters.update(lfParameters, getClass().getResourceAsStream("/dynaflow/config.json"));
        DynaflowParameters dynaflowParameters = lfParameters.getExtension(DynaflowParameters.class);
        assertNotNull(dynaflowParameters);

        assertTrue(dynaflowParameters.getSvcRegulationOn());
        assertFalse(dynaflowParameters.getShuntRegulationOn());
        assertTrue(dynaflowParameters.getAutomaticSlackBusOn());
        assertFalse(dynaflowParameters.getVscAsGenerators());
        assertTrue(dynaflowParameters.getLccAsLoads());
        double expectedDsoVoltageLevelValue = 987.6;
        assertEquals(expectedDsoVoltageLevelValue, dynaflowParameters.getDsoVoltageLevel(), 0);

        assertTrue(lfParameters.isTransformerVoltageControlOn());
        assertFalse(lfParameters.isPhaseShifterRegulationOn());
    }

    @Test
    public void roundTripParameters() throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);

        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        parameters.setNoGeneratorReactiveLimits(true);
        parameters.setPhaseShifterRegulationOn(false);

        DynaflowParameters params = new DynaflowParameters();
        params.setSvcRegulationOn(true);
        params.setShuntRegulationOn(false);
        params.setAutomaticSlackBusOn(true);
        params.setVscAsGenerators(false);
        params.setLccAsLoads(true);
        params.setDsoVoltageLevel(54.23);

        parameters.addExtension(DynaflowParameters.class, params);

        roundTripTest(parameters, JsonLoadFlowParameters::write,
                JsonLoadFlowParameters::read, "/dynaflow/dynaflow_default_serialization.json");
    }
}
