/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.parameters.Parameter;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynaflow.DynaFlowConstants.ActivePowerCompensation;
import com.powsybl.dynaflow.DynaFlowConstants.OutputTypes;
import com.powsybl.dynaflow.DynaFlowConstants.StartingPointMode;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.loadflow.LoadFlowParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.powsybl.commons.test.ComparisonUtils.assertTxtEquals;
import static com.powsybl.dynaflow.DynaFlowParameters.MODULE_SPECIFIC_PARAMETERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
class DynaFlowParametersTest extends AbstractSerDeTest {

    private InMemoryPlatformConfig platformConfig;

    @BeforeEach
    @Override
    public void setUp() throws IOException {
        super.setUp();
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    @Test
    void checkParameters() {
        boolean svcRegulationOn = true;
        boolean shuntRegulationOn = false;
        boolean automaticSlackBusOn = true;
        double dsoVoltageLevel = 87.32;
        double tfoVoltageLevel = 89.01;
        ActivePowerCompensation activePowerCompensation = ActivePowerCompensation.PMAX;
        String settingPath = "path/to/settingFile";
        String assemblingPath = "path/to/assemblingFile";
        double startTime = 0.;
        double stopTime = 100.;
        double precision = 15.45;
        List<String> chosenOutputs = List.of(OutputTypes.STEADYSTATE.name(), OutputTypes.TIMELINE.name());
        double timeStep = 0;
        StartingPointMode startingPointMode = StartingPointMode.FLAT;
        boolean mergeLoads = true;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        moduleConfig.setStringProperty("svcRegulationOn", Boolean.toString(svcRegulationOn));
        moduleConfig.setStringProperty("shuntRegulationOn", Boolean.toString(shuntRegulationOn));
        moduleConfig.setStringProperty("automaticSlackBusOn", Boolean.toString(automaticSlackBusOn));
        moduleConfig.setStringProperty("dsoVoltageLevel", Double.toString(dsoVoltageLevel));
        moduleConfig.setStringProperty("tfoVoltageLevel", Double.toString(tfoVoltageLevel));
        moduleConfig.setStringProperty("activePowerCompensation", activePowerCompensation.name());
        moduleConfig.setStringProperty("settingPath", settingPath);
        moduleConfig.setStringProperty("assemblingPath", assemblingPath);
        moduleConfig.setStringProperty("startTime", Double.toString(startTime));
        moduleConfig.setStringProperty("stopTime", Double.toString(stopTime));
        moduleConfig.setStringProperty("precision", Double.toString(precision));
        moduleConfig.setStringListProperty("chosenOutputs", chosenOutputs);
        moduleConfig.setStringProperty("timeStep", Double.toString(timeStep));
        moduleConfig.setStringProperty("startingPointMode", startingPointMode.getName());
        moduleConfig.setStringProperty("mergeLoads", Boolean.toString(mergeLoads));

        DynaFlowParameters parameters = DynaFlowParameters.load(moduleConfig);

        assertEquals(svcRegulationOn, parameters.getSvcRegulationOn());
        assertEquals(shuntRegulationOn, parameters.getShuntRegulationOn());
        assertEquals(automaticSlackBusOn, parameters.getAutomaticSlackBusOn());
        assertEquals(dsoVoltageLevel, parameters.getDsoVoltageLevel(), 0.1d);
        assertEquals(tfoVoltageLevel, parameters.getTfoVoltageLevel(), 0.1d);
        assertEquals(activePowerCompensation, parameters.getActivePowerCompensation());
        assertEquals(settingPath, parameters.getSettingPath());
        assertEquals(assemblingPath, parameters.getAssemblingPath());
        assertEquals(startTime, parameters.getStartTime(), 0.1d);
        assertEquals(stopTime, parameters.getStopTime(), 0.1d);
        assertEquals(precision, parameters.getPrecision(), 0.1d);
        assertThat(parameters.getChosenOutputs()).map(OutputTypes::name).containsExactlyInAnyOrderElementsOf(chosenOutputs);
        assertEquals(timeStep, parameters.getTimeStep(), 0.1d);
        assertEquals(startingPointMode, parameters.getStartingPointMode());
        assertEquals(mergeLoads, parameters.isMergeLoads());
    }

    @Test
    void checkDefaultParameters() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        assertNotNull(parametersExt);

        assertEquals("{svcRegulationOn=true, shuntRegulationOn=true, automaticSlackBusOn=true, dsoVoltageLevel=45.0, tfoVoltageLevel=100.0, activePowerCompensation=PMAX, startTime=0.0, stopTime=100.0, chosenOutputs=[TIMELINE], timeStep=10.0, startingPointMode=WARM, mergeLoads=true}",
                parametersExt.toString());

        assertTrue(parametersExt.getSvcRegulationOn());
        assertTrue(parametersExt.getShuntRegulationOn());
        assertTrue(parametersExt.getAutomaticSlackBusOn());
        assertEquals(45d, parametersExt.getDsoVoltageLevel());
        assertEquals(100d, parametersExt.getTfoVoltageLevel());
        assertEquals(ActivePowerCompensation.PMAX, parametersExt.getActivePowerCompensation());
        assertNull(parametersExt.getSettingPath());
        assertNull(parametersExt.getAssemblingPath());
        assertEquals(0d, parametersExt.getStartTime());
        assertEquals(100d, parametersExt.getStopTime());
        assertNull(parametersExt.getPrecision());
        assertThat(parametersExt.getChosenOutputs()).containsExactly(OutputTypes.TIMELINE);
        assertEquals(10d, parametersExt.getTimeStep());
        assertEquals(StartingPointMode.WARM, parametersExt.getStartingPointMode());
        assertTrue(parametersExt.isMergeLoads());
    }

    @Test
    void testConfigSpecificParameters() {
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        moduleConfig.setStringProperty("svcRegulationOn", "false");

        List<Parameter> parameters = new DynaFlowProvider().getSpecificParameters(platformConfig);

        assertEquals(false, parameters.getFirst().getDefaultValue());
    }

    @Test
    void checkAllParametersAssignedToString() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        boolean svcRegulationOn = true;
        boolean shuntRegulationOn = false;
        boolean automaticSlackBusOn = true;
        double dsoVoltageLevel = 87.32;
        double tfoVoltageLevel = 56.78;
        ActivePowerCompensation activePowerCompensation = ActivePowerCompensation.PMAX;
        String settingPath = "path/to/settingFile";
        String assemblingPath = "path/to/assemblingFile";
        double startTime = 0.;
        double stopTime = 100.;
        double precision = 15.45;
        List<String> chosenOutputs = Arrays.asList(OutputTypes.STEADYSTATE.name(), OutputTypes.TIMELINE.name());
        double timeStep = 0;
        StartingPointMode startingPointMode = StartingPointMode.WARM;
        boolean mergeLoad = false;

        Map<String, String> properties = new HashMap<>();
        properties.put("svcRegulationOn", Boolean.toString(svcRegulationOn));
        properties.put("shuntRegulationOn", Boolean.toString(shuntRegulationOn));
        properties.put("automaticSlackBusOn", Boolean.toString(automaticSlackBusOn));
        properties.put("dsoVoltageLevel", Double.toString(dsoVoltageLevel));
        properties.put("tfoVoltageLevel", Double.toString(tfoVoltageLevel));
        properties.put("activePowerCompensation", activePowerCompensation.name());
        properties.put("settingPath", settingPath);
        properties.put("assemblingPath", assemblingPath);
        properties.put("startTime", Double.toString(startTime));
        properties.put("stopTime", Double.toString(stopTime));
        properties.put("precision", Double.toString(precision));
        properties.put("chosenOutputs", OutputTypes.STEADYSTATE.name() + "," + OutputTypes.TIMELINE.name());
        properties.put("timeStep", Double.toString(timeStep));
        properties.put("startingPointMode", startingPointMode.name());
        properties.put("mergeLoads", Boolean.toString(mergeLoad));

        parametersExt.update(properties);

        String expectedString = "{svcRegulationOn=" + svcRegulationOn +
                ", shuntRegulationOn=" + shuntRegulationOn +
                ", automaticSlackBusOn=" + automaticSlackBusOn +
                ", dsoVoltageLevel=" + dsoVoltageLevel +
                ", tfoVoltageLevel=" + tfoVoltageLevel +
                ", activePowerCompensation=" + activePowerCompensation +
                ", settingPath=" + settingPath +
                ", assemblingPath=" + assemblingPath +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", precision=" + precision +
                ", chosenOutputs=" + chosenOutputs +
                ", timeStep=" + timeStep +
                ", startingPointMode=" + startingPointMode +
                ", mergeLoads=" + mergeLoad + "}";
        assertEquals(expectedString, parametersExt.toString());
        System.out.println(expectedString);
    }

    @Test
    void defaultParametersSerialization() throws IOException {
        LoadFlowParameters lfParameters = LoadFlowParameters.load(platformConfig);
        lfParameters.setUseReactiveLimits(false);
        lfParameters.setPhaseShifterRegulationOn(false);

        DynaFlowParameters dynaFlowParameters = new DynaFlowParameters();
        lfParameters.addExtension(DynaFlowParameters.class, dynaFlowParameters);

        Path workingDir = fileSystem.getPath("dynaflow/workingDir");
        Path parameterFile = fileSystem.getPath(DynaFlowConstants.CONFIG_FILENAME);
        DynaFlowConfigSerializer.serialize(lfParameters, dynaFlowParameters, workingDir, parameterFile);

        try (InputStream actual = Files.newInputStream(parameterFile);
             InputStream expected = getClass().getResourceAsStream("/params_default.json")) {
            assertNotNull(expected);
            assertTxtEquals(expected, actual);
        }
    }

    @Test
    void parametersSerialization() throws IOException {
        LoadFlowParameters lfParameters = LoadFlowParameters.load(platformConfig);
        lfParameters.setUseReactiveLimits(false);
        lfParameters.setPhaseShifterRegulationOn(false);

        DynaFlowParameters dynaFlowParameters = new DynaFlowParameters()
            .setSvcRegulationOn(true)
            .setShuntRegulationOn(false)
            .setAutomaticSlackBusOn(true)
            .setDsoVoltageLevel(32.4)
            .setTfoVoltageLevel(67.89)
            .setActivePowerCompensation(ActivePowerCompensation.P)
            .setSettingPath("path/to/settingFile")
            .setAssemblingPath("path/to/assemblingFile")
            .setStartTime(0.)
            .setStopTime(100.)
            .setPrecision(0.)
            .setChosenOutputs(Set.of(OutputTypes.STEADYSTATE))
            .setTimeStep(2.6)
            .setStartingPointMode(StartingPointMode.WARM)
            .setMergeLoads(true);
        lfParameters.addExtension(DynaFlowParameters.class, dynaFlowParameters);

        Path workingDir = fileSystem.getPath("dynaflow/workingDir");
        Path parameterFile = fileSystem.getPath(DynaFlowConstants.CONFIG_FILENAME);
        DynaFlowConfigSerializer.serialize(lfParameters, dynaFlowParameters, workingDir, parameterFile);

        try (InputStream actual = Files.newInputStream(parameterFile);
             InputStream expected = getClass().getResourceAsStream("/params.json")) {
            assertNotNull(expected);
            assertTxtEquals(expected, actual);
        }
    }

    @Test
    void loadMapDynaflowParameters() {

        boolean svcRegulationOn = true;
        boolean shuntRegulationOn = true;
        boolean automaticSlackBusOn = false;
        double dsoVoltageLevel = 2.0;
        double tfoVoltageLevel = 10;
        ActivePowerCompensation activePowerCompensation = ActivePowerCompensation.PMAX;
        String settingPath = "path/to/settingFile";
        String assemblingPath = "path/to/assemblingFile";
        double startTime = 0.;
        double stopTime = 100.;
        double precision = 15.45;
        Set<OutputTypes> chosenOutputs = Set.of(OutputTypes.STEADYSTATE, OutputTypes.TIMELINE);
        double timeStep = 0;
        StartingPointMode startingPointMode = StartingPointMode.WARM;
        boolean mergeLoads = false;

        Map<String, String> properties = new HashMap<>();
        properties.put("svcRegulationOn", Boolean.toString(svcRegulationOn));
        properties.put("shuntRegulationOn", Boolean.toString(shuntRegulationOn));
        properties.put("automaticSlackBusOn", Boolean.toString(automaticSlackBusOn));
        properties.put("dsoVoltageLevel", Double.toString(dsoVoltageLevel));
        properties.put("tfoVoltageLevel", Double.toString(tfoVoltageLevel));
        properties.put("activePowerCompensation", activePowerCompensation.name());
        properties.put("settingPath", settingPath);
        properties.put("assemblingPath", assemblingPath);
        properties.put("startTime", Double.toString(startTime));
        properties.put("stopTime", Double.toString(stopTime));
        properties.put("precision", Double.toString(precision));
        properties.put("chosenOutputs", "STEADYSTATE, TIMELINE");
        properties.put("timeStep", Double.toString(timeStep));
        properties.put("startingPointMode", startingPointMode.getName());
        properties.put("mergeLoads", Boolean.toString(mergeLoads));

        DynaFlowParameters dynaFlowParameters = DynaFlowParameters.load(properties);

        assertTrue(dynaFlowParameters.getSvcRegulationOn());
        assertTrue(dynaFlowParameters.getShuntRegulationOn());
        assertFalse(dynaFlowParameters.getAutomaticSlackBusOn());
        assertEquals(dsoVoltageLevel, dynaFlowParameters.getDsoVoltageLevel(), 0.1d);
        assertEquals(tfoVoltageLevel, dynaFlowParameters.getTfoVoltageLevel(), 0.1d);
        assertEquals(activePowerCompensation, dynaFlowParameters.getActivePowerCompensation());
        assertEquals(settingPath, dynaFlowParameters.getSettingPath());
        assertEquals(assemblingPath, dynaFlowParameters.getAssemblingPath());
        assertEquals(startTime, dynaFlowParameters.getStartTime(), 0.1d);
        assertEquals(stopTime, dynaFlowParameters.getStopTime(), 0.1d);
        assertEquals(precision, dynaFlowParameters.getPrecision(), 0.1d);
        assertThat(dynaFlowParameters.getChosenOutputs()).containsExactlyInAnyOrderElementsOf(chosenOutputs);
        assertEquals(timeStep, dynaFlowParameters.getTimeStep(), 0.1d);
        assertEquals(startingPointMode, dynaFlowParameters.getStartingPointMode());
        assertEquals(mergeLoads, dynaFlowParameters.isMergeLoads());
    }
}
