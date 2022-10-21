/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynaflow.DynaFlowConstants.OutputTypes;
import com.powsybl.loadflow.LoadFlowParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.powsybl.commons.ComparisonUtils.compareTxt;
import static com.powsybl.dynaflow.DynaFlowProvider.MODULE_SPECIFIC_PARAMETERS;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaFlowParametersTest extends AbstractConverterTest {

    private InMemoryPlatformConfig platformConfig;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void checkParameters() {
        boolean svcRegulationOn = true;
        boolean shuntRegulationOn = false;
        boolean automaticSlackBusOn = true;
        double dsoVoltageLevel = 87.32;
        List<String> chosenOutputs = Arrays.asList(OutputTypes.STEADYSTATE.name(), OutputTypes.TIMELINE.name());
        boolean vscAsGenerators = false;
        boolean lccAsLoads = false;
        double timeStep = 0;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig(MODULE_SPECIFIC_PARAMETERS);
        moduleConfig.setStringProperty("svcRegulationOn", Boolean.toString(svcRegulationOn));
        moduleConfig.setStringProperty("shuntRegulationOn", Boolean.toString(shuntRegulationOn));
        moduleConfig.setStringProperty("automaticSlackBusOn", Boolean.toString(automaticSlackBusOn));
        moduleConfig.setStringProperty("dsoVoltageLevel", Double.toString(dsoVoltageLevel));
        moduleConfig.setStringListProperty("chosenOutputs", chosenOutputs);
        moduleConfig.setStringProperty("vscAsGenerators", Boolean.toString(vscAsGenerators));
        moduleConfig.setStringProperty("lccAsLoads", Boolean.toString(lccAsLoads));
        moduleConfig.setStringProperty("timeStep", Double.toString(timeStep));

        DynaFlowParameters parameters = DynaFlowParameters.load(platformConfig);

        assertEquals(svcRegulationOn, parameters.getSvcRegulationOn());
        assertEquals(shuntRegulationOn, parameters.getShuntRegulationOn());
        assertEquals(automaticSlackBusOn, parameters.getAutomaticSlackBusOn());
        assert dsoVoltageLevel == parameters.getDsoVoltageLevel();
        assertArrayEquals(chosenOutputs.toArray(), parameters.getChosenOutputs().toArray());
        assertEquals(vscAsGenerators, parameters.getVscAsGenerators());
        assertEquals(lccAsLoads, parameters.getLccAsLoads());
        assert timeStep == parameters.getTimeStep();
    }

    @Test
    public void checkDefaultParameters() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        assertNotNull(parametersExt);

        assertNull(parametersExt.getSvcRegulationOn());
        assertNull(parametersExt.getShuntRegulationOn());
        assertNull(parametersExt.getAutomaticSlackBusOn());
        assertNull(parametersExt.getDsoVoltageLevel());
        assertNull(parametersExt.getChosenOutputs());
        assertNull(parametersExt.getVscAsGenerators());
        assertNull(parametersExt.getLccAsLoads());
        assertNull(parametersExt.getTimeStep());
    }

    @Test
    public void checkDefaultToString() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);

        String expectedString = "{}";

        assertEquals(expectedString, parametersExt.toString());

    }

    @Test
    public void checkAllParametersAssignedToString() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        boolean svcRegulationOn = true;
        boolean shuntRegulationOn = false;
        boolean automaticSlackBusOn = true;
        Double dsoVoltage = 45.0;
        List<String> chosenOutputs = Arrays.asList(OutputTypes.STEADYSTATE.name(), OutputTypes.TIMELINE.name());
        boolean vscAsGenerators = false;
        boolean lccAsLoads = false;
        Double timeStep = 2.6;

        Map<String, String> properties = Map.of(
                "svcRegulationOn", Boolean.toString(svcRegulationOn),
                "shuntRegulationOn", Boolean.toString(shuntRegulationOn),
                "automaticSlackBusOn", Boolean.toString(automaticSlackBusOn),
                "dsoVoltageLevel", Double.toString(dsoVoltage),
                "chosenOutputs", OutputTypes.STEADYSTATE.name() + "," + OutputTypes.TIMELINE.name(),
                "vscAsGenerators", Boolean.toString(vscAsGenerators),
                "lccAsLoads", Boolean.toString(lccAsLoads),
                "timeStep", Double.toString(timeStep));

        parametersExt.update(properties);

        String expectedString = "{svcRegulationOn=" + svcRegulationOn +
                ", shuntRegulationOn=" + shuntRegulationOn +
                ", automaticSlackBusOn=" + automaticSlackBusOn +
                ", dsoVoltageLevel=" + dsoVoltage +
                ", chosenOutputs=" + chosenOutputs +
                ", vscAsGenerators=" + vscAsGenerators +
                ", lccAsLoads=" + lccAsLoads +
                ", timeStep=" + timeStep + "}";
        assertEquals(expectedString, parametersExt.toString());
        System.out.println(expectedString);
    }

    @Test
    public void defaultParametersSerialization() throws IOException {
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
            compareTxt(expected, actual);
        }
    }

    @Test
    public void parametersSerialization() throws IOException {
        LoadFlowParameters lfParameters = LoadFlowParameters.load(platformConfig);
        lfParameters.setNoGeneratorReactiveLimits(true);
        lfParameters.setPhaseShifterRegulationOn(false);

        DynaFlowParameters dynaFlowParameters = new DynaFlowParameters();
        dynaFlowParameters.setSvcRegulationOn(true);
        dynaFlowParameters.setShuntRegulationOn(false);
        dynaFlowParameters.setAutomaticSlackBusOn(true);
        dynaFlowParameters.setDsoVoltageLevel(32.4);
        dynaFlowParameters.setChosenOutputs(Collections.singletonList(OutputTypes.STEADYSTATE.name()));
        dynaFlowParameters.setVscAsGenerators(true);
        dynaFlowParameters.setLccAsLoads(true);
        dynaFlowParameters.setTimeStep(2.6);
        lfParameters.addExtension(DynaFlowParameters.class, dynaFlowParameters);

        Path workingDir = fileSystem.getPath("dynaflow/workingDir");
        Path parameterFile = fileSystem.getPath(DynaFlowConstants.CONFIG_FILENAME);
        DynaFlowConfigSerializer.serialize(lfParameters, dynaFlowParameters, workingDir, parameterFile);

        try (InputStream actual = Files.newInputStream(parameterFile);
             InputStream expected = getClass().getResourceAsStream("/params.json")) {
            compareTxt(expected, actual);
        }
    }

    @Test
    public void loadMapDynaflowParameters() {
        Map<String, String> properties = Map.of(
                "svcRegulationOn", "true",
                "shuntRegulationOn", "true",
                "automaticSlackBusOn", "false",
                "dsoVoltageLevel", "2.0",
                "chosenOutputs", "STEADYSTATE, CONSTRAINTS",
                "vscAsGenerators", "false",
                "lccAsLoads", "false",
                "timeStep", "0");

        DynaFlowParameters dynaFlowParameters = DynaFlowParameters.load(properties);

        assertTrue(dynaFlowParameters.getSvcRegulationOn());
        assertTrue(dynaFlowParameters.getShuntRegulationOn());
        assertFalse(dynaFlowParameters.getAutomaticSlackBusOn());
        assert 2 == dynaFlowParameters.getDsoVoltageLevel();
        assertArrayEquals(Arrays.asList(OutputTypes.STEADYSTATE.name(), OutputTypes.CONSTRAINTS.name()).toArray(), dynaFlowParameters.getChosenOutputs().toArray());
        assertFalse(dynaFlowParameters.getVscAsGenerators());
        assertFalse(dynaFlowParameters.getLccAsLoads());
        assert 0 == dynaFlowParameters.getTimeStep();
    }
}
