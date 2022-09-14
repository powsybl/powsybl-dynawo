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
import com.powsybl.loadflow.LoadFlowParameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.powsybl.commons.ComparisonUtils.compareTxt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynaflow-default-parameters");
        moduleConfig.setStringProperty("svcRegulationOn", Boolean.toString(svcRegulationOn));
        moduleConfig.setStringProperty("shuntRegulationOn", Boolean.toString(shuntRegulationOn));
        moduleConfig.setStringProperty("automaticSlackBusOn", Boolean.toString(automaticSlackBusOn));
        moduleConfig.setStringProperty("dsoVoltageLevel", Double.toString(dsoVoltageLevel));

        DynaFlowParameters parameters = DynaFlowParameters.load(platformConfig);

        assertEquals(svcRegulationOn, parameters.getSvcRegulationOn());
        assertEquals(shuntRegulationOn, parameters.getShuntRegulationOn());
        assertEquals(automaticSlackBusOn, parameters.getAutomaticSlackBusOn());
    }

    @Test
    public void checkDefaultParameters() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        assertNotNull(parametersExt);

        assertEquals(DynaFlowParameters.DEFAULT_SVC_REGULATION_ON, parametersExt.getSvcRegulationOn());
        assertEquals(DynaFlowParameters.DEFAULT_SHUNT_REGULATION_ON, parametersExt.getShuntRegulationOn());
        assertEquals(DynaFlowParameters.DEFAULT_AUTOMATIC_SLACK_BUS_ON, parametersExt.getAutomaticSlackBusOn());
    }

    @Test
    public void checkDefaultToString() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        String expectedString = "{svcRegulationOn=" + DynaFlowParameters.DEFAULT_SVC_REGULATION_ON +
                ", shuntRegulationOn=" + DynaFlowParameters.DEFAULT_SHUNT_REGULATION_ON +
                ", automaticSlackBusOn=" + DynaFlowParameters.DEFAULT_AUTOMATIC_SLACK_BUS_ON +
                ", dsoVoltageLevel=" + DynaFlowParameters.DEFAULT_DSO_VOLTAGE_LEVEL + "}";
        assertEquals(expectedString, parametersExt.toString());

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
        lfParameters.addExtension(DynaFlowParameters.class, dynaFlowParameters);

        Path workingDir = fileSystem.getPath("dynaflow/workingDir");
        Path parameterFile = fileSystem.getPath(DynaFlowConstants.CONFIG_FILENAME);
        DynaFlowConfigSerializer.serialize(lfParameters, dynaFlowParameters, workingDir, parameterFile);

        try (InputStream actual = Files.newInputStream(parameterFile);
            InputStream expected = getClass().getResourceAsStream("/params.json")) {
            compareTxt(expected, actual);
        }
    }
}
