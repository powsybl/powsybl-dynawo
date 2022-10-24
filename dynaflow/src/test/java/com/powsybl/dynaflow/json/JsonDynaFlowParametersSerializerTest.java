/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow.json;

import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.dynaflow.DynaFlowConstants;
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.json.JsonLoadFlowParameters;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class JsonDynaFlowParametersSerializerTest extends AbstractConverterTest {

    @Test
    public void testDeserialize() {

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
        assert expectedStartTime == dynaFlowParameters.getStartTime();
        assert expectedStopTime == dynaFlowParameters.getStopTime();
        assert expectedPrecision == dynaFlowParameters.getPrecision();
        assert expectedTimeOfEvent == dynaFlowParameters.getTimeOfEvent();
        assertArrayEquals(expectedChosenOutputs.toArray(), dynaFlowParameters.getChosenOutputs().toArray());
        assert expectedTimeStep == dynaFlowParameters.getTimeStep();

        assertTrue(lfParameters.isTransformerVoltageControlOn());
        assertFalse(lfParameters.isPhaseShifterRegulationOn());
    }

    @Test
    public void roundTripParameters() throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);

        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        parameters.setNoGeneratorReactiveLimits(true);
        parameters.setPhaseShifterRegulationOn(false);

        DynaFlowParameters params = new DynaFlowParameters();
        params.setSvcRegulationOn(true);
        params.setShuntRegulationOn(false);
        params.setAutomaticSlackBusOn(true);
        params.setDsoVoltageLevel(54.23);
        params.setActivePowerCompensation(DynaFlowConstants.ActivePowerCompensation.P);
        params.setSettingPath("path/to/settingFile");
        params.setAssemblingPath("path/to/assemblingFile");
        params.setStartTime(0.);
        params.setStopTime(100.);
        params.setPrecision(0.);
        params.setTimeOfEvent(10.);
        params.setChosenOutputs(Collections.singletonList(DynaFlowConstants.OutputTypes.STEADYSTATE.name()));
        params.setTimeStep(2.6);

        parameters.addExtension(DynaFlowParameters.class, params);

        roundTripTest(parameters, JsonLoadFlowParameters::write,
                JsonLoadFlowParameters::read, "/dynaflow_parameters_set_serialization.json");
    }

    @Test
    public void serializeWithDefaultDynaflowParameters() throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);

        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);

        roundTripTest(parameters, JsonLoadFlowParameters::write,
                JsonLoadFlowParameters::read, "/dynaflow_default_serialization.json");
    }
}
