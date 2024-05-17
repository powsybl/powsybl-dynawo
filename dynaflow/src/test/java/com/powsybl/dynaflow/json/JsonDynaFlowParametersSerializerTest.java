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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        double expectedTimeOfEvent = 10.;
        List<String> expectedChosenOutputs = Arrays.asList(DynaFlowConstants.OutputTypes.STEADYSTATE.name(), DynaFlowConstants.OutputTypes.TIMELINE.name());
        double expectedTimeStep = 2.6;

        LoadFlowParameters lfParameters = LoadFlowParameters.load();
        JsonLoadFlowParameters.update(lfParameters, getClass().getResourceAsStream("/config.json"));
        DynaFlowParameters dynaFlowParameters = lfParameters.getExtension(DynaFlowParameters.class);
        assertNotNull(dynaFlowParameters);

        assertTrue(dynaFlowParameters.getSvcRegulationOn());
        assertFalse(dynaFlowParameters.getShuntRegulationOn());
        assertTrue(dynaFlowParameters.getAutomaticSlackBusOn());
        assertEquals(expectedDsoVoltageLevelValue, dynaFlowParameters.getDsoVoltageLevel(), 0);
        assertEquals(DynaFlowConstants.ActivePowerCompensation.P, dynaFlowParameters.getActivePowerCompensation());
        assertEquals(expectedSettingPath, dynaFlowParameters.getSettingPath());
        assertEquals(expectedAssemblingPath, dynaFlowParameters.getAssemblingPath());
        assertEquals(expectedStartTime, dynaFlowParameters.getStartTime(), 0.1d);
        assertEquals(expectedStopTime, dynaFlowParameters.getStopTime(), 0.1d);
        assertEquals(expectedPrecision, dynaFlowParameters.getPrecision(), 0.1d);
        assertEquals(expectedTimeOfEvent, dynaFlowParameters.getTimeOfEvent(), 0.1d);
        assertArrayEquals(expectedChosenOutputs.toArray(), dynaFlowParameters.getChosenOutputs().toArray());
        assertEquals(expectedTimeStep, dynaFlowParameters.getTimeStep(), 0.1d);
        assertEquals(DynaFlowConstants.StartingPointMode.WARM, dynaFlowParameters.getStartingPointMode());

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
            .setShuntRegulationOn(false)
            .setAutomaticSlackBusOn(true)
            .setDsoVoltageLevel(54.23)
            .setActivePowerCompensation(DynaFlowConstants.ActivePowerCompensation.P)
            .setSettingPath("path/to/settingFile")
            .setAssemblingPath("path/to/assemblingFile")
            .setStartTime(0.)
            .setStopTime(100.)
            .setPrecision(0.)
            .setTimeOfEvent(10.)
            .setChosenOutputs(Collections.singletonList(DynaFlowConstants.OutputTypes.STEADYSTATE.name()))
            .setTimeStep(2.6)
            .setStartingPointMode(DynaFlowConstants.StartingPointMode.WARM);

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
}
