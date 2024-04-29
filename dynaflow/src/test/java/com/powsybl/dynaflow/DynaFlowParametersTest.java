/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynaflow.DynaFlowConstants.ActivePowerCompensation;
import com.powsybl.dynaflow.DynaFlowConstants.OutputTypes;
import com.powsybl.dynaflow.DynaFlowConstants.StartingPointMode;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.loadflow.LoadFlowParameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.powsybl.commons.test.ComparisonUtils.assertTxtEquals;
import static com.powsybl.dynaflow.DynaFlowProvider.MODULE_SPECIFIC_PARAMETERS;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author Guillaume Pernin {@literal <guillaume.pernin at rte-france.com>}
 */
class DynaFlowParametersTest extends AbstractSerDeTest {

    private InMemoryPlatformConfig platformConfig;

    @BeforeEach
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    @AfterEach
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void checkParameters() {
        boolean svcRegulationOn = true;
        boolean shuntRegulationOn = false;
        boolean automaticSlackBusOn = true;
        double dsoVoltageLevel = 87.32;
        ActivePowerCompensation activePowerCompensation = ActivePowerCompensation.PMAX;
        String settingPath = "path/to/settingFile";
        String assemblingPath = "path/to/assemblingFile";
        double startTime = 0.;
        double stopTime = 100.;
        double precision = 15.45;
        double timeOfEvent = 10.;
        List<String> chosenOutputs = Arrays.asList(OutputTypes.STEADYSTATE.name(), OutputTypes.TIMELINE.name());
        double timeStep = 0;
        StartingPointMode startingPointMode = StartingPointMode.FLAT;
        boolean mergeLoads = false;

        DynaFlowParameters.Sa securityAnalysis = new DynaFlowParameters.Sa();
        securityAnalysis.setTimeOfEvent(2.);

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        moduleConfig.setStringProperty("svcRegulationOn", Boolean.toString(svcRegulationOn));
        moduleConfig.setStringProperty("shuntRegulationOn", Boolean.toString(shuntRegulationOn));
        moduleConfig.setStringProperty("automaticSlackBusOn", Boolean.toString(automaticSlackBusOn));
        moduleConfig.setStringProperty("dsoVoltageLevel", Double.toString(dsoVoltageLevel));
        moduleConfig.setStringProperty("activePowerCompensation", activePowerCompensation.name());
        moduleConfig.setStringProperty("settingPath", settingPath);
        moduleConfig.setStringProperty("assemblingPath", assemblingPath);
        moduleConfig.setStringProperty("startTime", Double.toString(startTime));
        moduleConfig.setStringProperty("stopTime", Double.toString(stopTime));
        moduleConfig.setStringProperty("precision", Double.toString(precision));
        moduleConfig.setStringProperty("timeOfEvent", Double.toString(timeOfEvent));
        moduleConfig.setStringListProperty("chosenOutputs", chosenOutputs);
        moduleConfig.setStringProperty("timeStep", Double.toString(timeStep));
        moduleConfig.setStringProperty("startingPointMode", startingPointMode.getName());
        moduleConfig.setStringProperty("mergeLoads", Boolean.toString(mergeLoads));

        DynaFlowParameters parameters = DynaFlowParameters.load(platformConfig);

        assertEquals(svcRegulationOn, parameters.getSvcRegulationOn());
        assertEquals(shuntRegulationOn, parameters.getShuntRegulationOn());
        assertEquals(automaticSlackBusOn, parameters.getAutomaticSlackBusOn());
        assertEquals(dsoVoltageLevel, parameters.getDsoVoltageLevel(), 0.1d);
        assertEquals(activePowerCompensation, parameters.getActivePowerCompensation());
        assertEquals(settingPath, parameters.getSettingPath());
        assertEquals(assemblingPath, parameters.getAssemblingPath());
        assertEquals(startTime, parameters.getStartTime(), 0.1d);
        assertEquals(stopTime, parameters.getStopTime(), 0.1d);
        assertEquals(precision, parameters.getPrecision(), 0.1d);
        assertEquals(timeOfEvent, parameters.getTimeOfEvent(), 0.1d);
        assertArrayEquals(chosenOutputs.toArray(), parameters.getChosenOutputs().toArray());
        assertEquals(timeStep, parameters.getTimeStep(), 0.1d);
        assertEquals(startingPointMode, parameters.getStartingPointMode());
        assertEquals(mergeLoads, parameters.isMergeLoads());
    }

    @Test
    void checkDefaultParameters() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        assertNotNull(parametersExt);

        assertEquals("{chosenOutputs=[TIMELINE], mergeLoads=true}", parametersExt.toString());

        assertNull(parametersExt.getSvcRegulationOn());
        assertNull(parametersExt.getShuntRegulationOn());
        assertNull(parametersExt.getAutomaticSlackBusOn());
        assertNull(parametersExt.getDsoVoltageLevel());
        assertNull(parametersExt.getActivePowerCompensation());
        assertNull(parametersExt.getSettingPath());
        assertNull(parametersExt.getAssemblingPath());
        assertNull(parametersExt.getStartTime());
        assertNull(parametersExt.getStopTime());
        assertNull(parametersExt.getPrecision());
        assertNull(parametersExt.getSa());
        assertEquals(List.of(OutputTypes.TIMELINE.name()), parametersExt.getChosenOutputs());
        assertNull(parametersExt.getTimeStep());
        assertNull(parametersExt.getStartingPointMode());
        assertTrue(parametersExt.isMergeLoads());
    }

    @Test
    void checkAllParametersAssignedToString() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        boolean svcRegulationOn = true;
        boolean shuntRegulationOn = false;
        boolean automaticSlackBusOn = true;
        double dsoVoltageLevel = 87.32;
        ActivePowerCompensation activePowerCompensation = ActivePowerCompensation.PMAX;
        String settingPath = "path/to/settingFile";
        String assemblingPath = "path/to/assemblingFile";
        double startTime = 0.;
        double stopTime = 100.;
        double precision = 15.45;
        double timeOfEvent = 10.;
        List<String> chosenOutputs = Arrays.asList(OutputTypes.STEADYSTATE.name(), OutputTypes.TIMELINE.name());
        double timeStep = 0;
        StartingPointMode startingPointMode = StartingPointMode.WARM;
        boolean mergeLoad = false;

        Map<String, String> properties = new HashMap<>();
        properties.put("svcRegulationOn", Boolean.toString(svcRegulationOn));
        properties.put("shuntRegulationOn", Boolean.toString(shuntRegulationOn));
        properties.put("automaticSlackBusOn", Boolean.toString(automaticSlackBusOn));
        properties.put("dsoVoltageLevel", Double.toString(dsoVoltageLevel));
        properties.put("activePowerCompensation", activePowerCompensation.name());
        properties.put("settingPath", settingPath);
        properties.put("assemblingPath", assemblingPath);
        properties.put("startTime", Double.toString(startTime));
        properties.put("stopTime", Double.toString(stopTime));
        properties.put("precision", Double.toString(precision));
        properties.put("timeOfEvent", Double.toString(timeOfEvent));
        properties.put("chosenOutputs", OutputTypes.STEADYSTATE.name() + "," + OutputTypes.TIMELINE.name());
        properties.put("timeStep", Double.toString(timeStep));
        properties.put("startingPointMode", startingPointMode.name());
        properties.put("mergeLoads", Boolean.toString(mergeLoad));

        parametersExt.update(properties);

        String expectedString = "{svcRegulationOn=" + svcRegulationOn +
                ", shuntRegulationOn=" + shuntRegulationOn +
                ", automaticSlackBusOn=" + automaticSlackBusOn +
                ", dsoVoltageLevel=" + dsoVoltageLevel +
                ", activePowerCompensation=" + activePowerCompensation +
                ", settingPath=" + settingPath +
                ", assemblingPath=" + assemblingPath +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                ", precision=" + precision +
                ", sa=" +
                "{timeOfEvent=" + timeOfEvent + "}" +
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
        lfParameters.setNoGeneratorReactiveLimits(true);
        lfParameters.setPhaseShifterRegulationOn(false);

        DynaFlowParameters dynaFlowParameters = new DynaFlowParameters();
        lfParameters.addExtension(DynaFlowParameters.class, dynaFlowParameters);

        Path workingDir = fileSystem.getPath("dynaflow/workingDir");
        Path parameterFile = fileSystem.getPath(DynaFlowConstants.CONFIG_FILENAME);
        DynaFlowConfigSerializer.serialize(lfParameters, dynaFlowParameters, workingDir, parameterFile);

        try (InputStream actual = Files.newInputStream(parameterFile);
             InputStream expected = getClass().getResourceAsStream("/params_default.json")) {
            assertTxtEquals(Objects.requireNonNull(expected), actual);
        }
    }

    @Test
    void parametersSerialization() throws IOException {
        LoadFlowParameters lfParameters = LoadFlowParameters.load(platformConfig);
        lfParameters.setNoGeneratorReactiveLimits(true);
        lfParameters.setPhaseShifterRegulationOn(false);

        DynaFlowParameters dynaFlowParameters = new DynaFlowParameters();
        dynaFlowParameters.setSvcRegulationOn(true);
        dynaFlowParameters.setShuntRegulationOn(false);
        dynaFlowParameters.setAutomaticSlackBusOn(true);
        dynaFlowParameters.setDsoVoltageLevel(32.4);
        dynaFlowParameters.setActivePowerCompensation(ActivePowerCompensation.P);
        dynaFlowParameters.setSettingPath("path/to/settingFile");
        dynaFlowParameters.setAssemblingPath("path/to/assemblingFile");
        dynaFlowParameters.setStartTime(0.);
        dynaFlowParameters.setStopTime(100.);
        dynaFlowParameters.setPrecision(0.);
        dynaFlowParameters.setTimeOfEvent(10.);
        dynaFlowParameters.setChosenOutputs(Collections.singletonList(OutputTypes.STEADYSTATE.name()));
        dynaFlowParameters.setTimeStep(2.6);
        dynaFlowParameters.setStartingPointMode(StartingPointMode.WARM);
        dynaFlowParameters.setMergeLoads(false);
        lfParameters.addExtension(DynaFlowParameters.class, dynaFlowParameters);

        Path workingDir = fileSystem.getPath("dynaflow/workingDir");
        Path parameterFile = fileSystem.getPath(DynaFlowConstants.CONFIG_FILENAME);
        DynaFlowConfigSerializer.serialize(lfParameters, dynaFlowParameters, workingDir, parameterFile);

        try (InputStream actual = Files.newInputStream(parameterFile);
             InputStream expected = getClass().getResourceAsStream("/params.json")) {
            assertTxtEquals(Objects.requireNonNull(expected), actual);
        }
    }

    @Test
    void loadMapDynaflowParameters() {

        boolean svcRegulationOn = true;
        boolean shuntRegulationOn = true;
        boolean automaticSlackBusOn = false;
        double dsoVoltageLevel = 2.0;
        ActivePowerCompensation activePowerCompensation = ActivePowerCompensation.PMAX;
        String settingPath = "path/to/settingFile";
        String assemblingPath = "path/to/assemblingFile";
        double startTime = 0.;
        double stopTime = 100.;
        double precision = 15.45;
        double timeOfEvent = 10.;
        List<String> chosenOutputs = Arrays.asList(OutputTypes.STEADYSTATE.name(), OutputTypes.TIMELINE.name());
        double timeStep = 0;
        StartingPointMode startingPointMode = StartingPointMode.WARM;
        boolean mergeLoads = false;

        Map<String, String> properties = new HashMap<>();
        properties.put("svcRegulationOn", Boolean.toString(svcRegulationOn));
        properties.put("shuntRegulationOn", Boolean.toString(shuntRegulationOn));
        properties.put("automaticSlackBusOn", Boolean.toString(automaticSlackBusOn));
        properties.put("dsoVoltageLevel", Double.toString(dsoVoltageLevel));
        properties.put("activePowerCompensation", activePowerCompensation.name());
        properties.put("settingPath", settingPath);
        properties.put("assemblingPath", assemblingPath);
        properties.put("startTime", Double.toString(startTime));
        properties.put("stopTime", Double.toString(stopTime));
        properties.put("precision", Double.toString(precision));
        properties.put("timeOfEvent", Double.toString(timeOfEvent));
        properties.put("chosenOutputs", OutputTypes.STEADYSTATE.name() + ", " + OutputTypes.TIMELINE.name());
        properties.put("timeStep", Double.toString(timeStep));
        properties.put("startingPointMode", startingPointMode.getName());
        properties.put("mergeLoads", Boolean.toString(mergeLoads));

        DynaFlowParameters dynaFlowParameters = DynaFlowParameters.load(properties);

        assertTrue(dynaFlowParameters.getSvcRegulationOn());
        assertTrue(dynaFlowParameters.getShuntRegulationOn());
        assertFalse(dynaFlowParameters.getAutomaticSlackBusOn());
        assertEquals(dsoVoltageLevel, dynaFlowParameters.getDsoVoltageLevel(), 0.1d);
        assertEquals(activePowerCompensation, dynaFlowParameters.getActivePowerCompensation());
        assertEquals(settingPath, dynaFlowParameters.getSettingPath());
        assertEquals(assemblingPath, dynaFlowParameters.getAssemblingPath());
        assertEquals(startTime, dynaFlowParameters.getStartTime(), 0.1d);
        assertEquals(stopTime, dynaFlowParameters.getStopTime(), 0.1d);
        assertEquals(precision, dynaFlowParameters.getPrecision(), 0.1d);
        assertEquals(timeOfEvent, dynaFlowParameters.getTimeOfEvent(), 0.1d);
        assertArrayEquals(chosenOutputs.toArray(), dynaFlowParameters.getChosenOutputs().toArray());
        assertEquals(timeStep, dynaFlowParameters.getTimeStep(), 0.1d);
        assertEquals(startingPointMode, dynaFlowParameters.getStartingPointMode());
        assertEquals(mergeLoads, dynaFlowParameters.isMergeLoads());
    }
}
