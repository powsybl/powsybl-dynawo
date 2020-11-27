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
import com.powsybl.dynaflow.json.DynaflowConfigSerializer;
import com.powsybl.loadflow.LoadFlowParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
public class DynaflowParametersTest extends AbstractConverterTest {

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
        boolean vscAsGenerators = false;
        boolean lccAsLoads = true;
        double dsoVoltageLevel = 87.32;

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynaflow-default-parameters");
        moduleConfig.setStringProperty("svcRegulationOn", Boolean.toString(svcRegulationOn));
        moduleConfig.setStringProperty("shuntRegulationOn", Boolean.toString(shuntRegulationOn));
        moduleConfig.setStringProperty("automaticSlackBusOn", Boolean.toString(automaticSlackBusOn));
        moduleConfig.setStringProperty("vscAsGenerators", Boolean.toString(vscAsGenerators));
        moduleConfig.setStringProperty("lccAsLoads", Boolean.toString(lccAsLoads));
        moduleConfig.setStringProperty("dsoVoltageLevel", Double.toString(dsoVoltageLevel));

        DynaflowParameters.DynaflowConfigLoader configLoader = new DynaflowParameters.DynaflowConfigLoader();
        DynaflowParameters parameters = configLoader.load(platformConfig);

        assertEquals(svcRegulationOn, parameters.getSvcRegulationOn());
        assertEquals(shuntRegulationOn, parameters.getShuntRegulationOn());
        assertEquals(automaticSlackBusOn, parameters.getAutomaticSlackBusOn());
        assertEquals(vscAsGenerators, parameters.getVscAsGenerators());
        assertEquals(lccAsLoads, parameters.getLccAsLoads());
    }

    @Test
    public void checkDefaultParameters() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaflowParameters parametersExt = parameters.getExtension(DynaflowParameters.class);
        assertNotNull(parametersExt);

        assertEquals(DynaflowParameters.DEFAULT_SVC_REGULATION_ON, parametersExt.getSvcRegulationOn());
        assertEquals(DynaflowParameters.DEFAULT_SHUNT_REGULATION_ON, parametersExt.getShuntRegulationOn());
        assertEquals(DynaflowParameters.DEFAULT_AUTOMATIC_SLACK_BUS_ON, parametersExt.getAutomaticSlackBusOn());
        assertEquals(DynaflowParameters.DEFAULT_VSC_AS_GENERATORS, parametersExt.getVscAsGenerators());
        assertEquals(DynaflowParameters.DEFAULT_LCC_AS_LOADS, parametersExt.getLccAsLoads());

    }

    @Test
    public void checkDefaultToString() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaflowParameters parametersExt = parameters.getExtension(DynaflowParameters.class);
        String expectedString = "{svcRegulationOn=" + DynaflowParameters.DEFAULT_SVC_REGULATION_ON +
                ", shuntRegulationON=" + DynaflowParameters.DEFAULT_SHUNT_REGULATION_ON +
                ", automaticSlackBusON=" + DynaflowParameters.DEFAULT_AUTOMATIC_SLACK_BUS_ON +
                ", vscAsGenerators=" + DynaflowParameters.DEFAULT_VSC_AS_GENERATORS +
                ", lccAsLoads=" + DynaflowParameters.DEFAULT_LCC_AS_LOADS +
                ", dsoVoltageLevel=" + DynaflowParameters.DEFAULT_DSO_VOLTAGE_LEVEL + "}";
        assertEquals(expectedString, parametersExt.toString());

    }

    @Test
    public void parametersSerialization() throws IOException {
        LoadFlowParameters lfParameters = LoadFlowParameters.load(platformConfig);
        lfParameters.setNoGeneratorReactiveLimits(true);
        lfParameters.setPhaseShifterRegulationOn(false);

        DynaflowParameters dynaflowParameters = new DynaflowParameters();
        dynaflowParameters.setSvcRegulationOn(true);
        dynaflowParameters.setShuntRegulationOn(false);
        dynaflowParameters.setAutomaticSlackBusOn(true);
        dynaflowParameters.setVscAsGenerators(false);
        dynaflowParameters.setLccAsLoads(true);
        dynaflowParameters.setDsoVoltageLevel(32.4);
        lfParameters.addExtension(DynaflowParameters.class, dynaflowParameters);

        Path workingDir = fileSystem.getPath("dynaflow/workingDir");
        Path parameterFile = fileSystem.getPath(DynaflowConstants.CONFIG_FILENAME);
        DynaflowConfigSerializer.serialize(lfParameters, dynaflowParameters, workingDir, parameterFile);

        try (InputStream actual = Files.newInputStream(parameterFile);
            InputStream expected = getClass().getResourceAsStream("/dynaflow/params.json")) {
            compareTxt(expected, actual);
        }
    }
}
