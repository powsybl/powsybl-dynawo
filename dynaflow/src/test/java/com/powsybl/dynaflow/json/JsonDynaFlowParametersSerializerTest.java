/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow.json;

import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynaflow.DynaFlowConstants;
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
class JsonDynaFlowParametersSerializerTest extends AbstractSerDeTest {

    @Test
    void testDeserialize() {

        double expectedDsoVoltageLevelValue = 987.6;
        String expectedSettingPath = "path/to/settingFile";
        String expectedAssemblingPath = "path/to/assemblingFile";
        double expectedStartTime = 0.;
        double expectedStopTime = 100.;
        double expectedPrecision = 0.;
        Set<DynaFlowConstants.OutputTypes> expectedChosenOutputs = Set.of(DynaFlowConstants.OutputTypes.STEADYSTATE, DynaFlowConstants.OutputTypes.TIMELINE);
        double expectedTimeStep = 2.6;

        LoadFlowParameters lfParameters = LoadFlowParameters.load();
        JsonLoadFlowParameters.update(lfParameters, getClass().getResourceAsStream("/config.json"));
        DynaFlowParameters dynaFlowParameters = lfParameters.getExtension(DynaFlowParameters.class);
        assertNotNull(dynaFlowParameters);

        assertTrue(dynaFlowParameters.getSvcRegulationOn());
        assertEquals(expectedDsoVoltageLevelValue, dynaFlowParameters.getDsoVoltageLevel(), 0);
        assertEquals(expectedSettingPath, dynaFlowParameters.getSettingPath());
        assertEquals(expectedAssemblingPath, dynaFlowParameters.getAssemblingPath());
        assertEquals(expectedStartTime, dynaFlowParameters.getStartTime(), 0.1d);
        assertEquals(expectedStopTime, dynaFlowParameters.getStopTime(), 0.1d);
        assertEquals(expectedPrecision, dynaFlowParameters.getPrecision(), 0.1d);
        assertThat(dynaFlowParameters.getChosenOutputs()).containsExactlyInAnyOrderElementsOf(expectedChosenOutputs);
        assertEquals(expectedTimeStep, dynaFlowParameters.getTimeStep(), 0.1d);
        assertEquals(DynaFlowConstants.StartingPointMode.WARM, dynaFlowParameters.getStartingPointMode());
        assertFalse(dynaFlowParameters.isMergeLoads());

        assertTrue(lfParameters.isTransformerVoltageControlOn());
        assertFalse(lfParameters.isPhaseShifterRegulationOn());
    }

    @Test
    void roundTripParameters() throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);

        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        parameters.setUseReactiveLimits(false);
        parameters.setPhaseShifterRegulationOn(false);

        DynaFlowParameters params = new DynaFlowParameters()
            .setSvcRegulationOn(true)
            .setDsoVoltageLevel(54.23)
            .setSettingPath("path/to/settingFile")
            .setAssemblingPath("path/to/assemblingFile")
            .setStartTime(0.)
            .setStopTime(100.)
            .setPrecision(0.)
            .setChosenOutputs(Set.of(DynaFlowConstants.OutputTypes.STEADYSTATE))
            .setTimeStep(2.6)
            .setStartingPointMode(DynaFlowConstants.StartingPointMode.WARM)
            .setMergeLoads(false);

        parameters.addExtension(DynaFlowParameters.class, params);

        roundTripTest(parameters, JsonLoadFlowParameters::write,
                JsonLoadFlowParameters::read, "/dynaflow_parameters_set_serialization.json");
    }

    @Test
    void serializeWithDefaultDynaflowParameters() throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        roundTripTest(parameters, JsonLoadFlowParameters::write,
                JsonLoadFlowParameters::read, "/dynaflow_default_serialization.json");
    }

    @Test
    void partialUpdate() {
        LoadFlowParameters lfParameters = LoadFlowParameters.load();
        JsonLoadFlowParameters.update(lfParameters, getClass().getResourceAsStream("/partial_dynaflow_parameters_update.json"));
        assertTrue(lfParameters.isTransformerVoltageControlOn());

        DynaFlowParameters dynaFlowParameters = lfParameters.getExtension(DynaFlowParameters.class);
        assertNotNull(dynaFlowParameters);
        //set false in config.yml
        assertFalse(dynaFlowParameters.getSvcRegulationOn());
        assertEquals(45d, dynaFlowParameters.getDsoVoltageLevel(), 0);
        assertEquals("path/to/settingFile", dynaFlowParameters.getSettingPath());
        assertNull(dynaFlowParameters.getAssemblingPath());
        assertEquals(0d, dynaFlowParameters.getStartTime(), 0.1d);
        assertEquals(150d, dynaFlowParameters.getStopTime(), 0.1d);
        assertNull(dynaFlowParameters.getPrecision());
        assertThat(dynaFlowParameters.getChosenOutputs()).containsExactlyInAnyOrderElementsOf(EnumSet.of(DynaFlowConstants.OutputTypes.TIMELINE));
        assertEquals(10d, dynaFlowParameters.getTimeStep(), 0.1d);
        assertEquals(DynaFlowConstants.StartingPointMode.WARM, dynaFlowParameters.getStartingPointMode());
        assertTrue(dynaFlowParameters.isMergeLoads());
    }
}
