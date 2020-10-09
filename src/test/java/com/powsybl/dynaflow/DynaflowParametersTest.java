package com.powsybl.dynaflow;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.loadflow.LoadFlowParameters;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.util.Objects;

import static org.junit.Assert.*;

public class DynaflowParametersTest {

    private FileSystem fileSystem;
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

        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynaflow-default-parameters");
        moduleConfig.setStringProperty("svcRegulationOn", Objects.toString(svcRegulationOn));
        moduleConfig.setStringProperty("shuntRegulationOn", Objects.toString(shuntRegulationOn));
        moduleConfig.setStringProperty("automaticSlackBusOn", Objects.toString(automaticSlackBusOn));
        moduleConfig.setStringProperty("vscAsGenerators", Objects.toString(vscAsGenerators));
        moduleConfig.setStringProperty("lccAsLoads", Objects.toString(lccAsLoads));

        DynaflowParameters.DynaflowConfigLoader configLoader = new DynaflowParameters.DynaflowConfigLoader();
        DynaflowParameters parameters = configLoader.load(platformConfig);

        assertTrue(parameters.getSvcRegulationOn());
        assertFalse(parameters.getShuntRegulationOn());
        assertTrue(parameters.getAutomaticSlackBusOn());
        assertFalse(parameters.getVscAsGenerators());
        assertTrue(parameters.getLccAsLoads());
    }

    @Test
    public void checkDefaultParameters() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaflowParameters parametersExt = parameters.getExtension(DynaflowParameters.class);

        assertEquals(DynaflowParameters.SVC_REGULATION_ON, parametersExt.getSvcRegulationOn());
        assertEquals(DynaflowParameters.SHUNT_REGULATION_ON, parametersExt.getShuntRegulationOn());
        assertEquals(DynaflowParameters.AUTOMATIC_SLACK_BUS_ON, parametersExt.getAutomaticSlackBusOn());
        assertEquals(DynaflowParameters.VSC_AS_GENERATORS, parametersExt.getVscAsGenerators());
        assertEquals(DynaflowParameters.LCC_AS_LOADS, parametersExt.getLccAsLoads());

    }

    @Test
    public void checkDefaultToString() {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        DynaflowParameters parametersExt = parameters.getExtension(DynaflowParameters.class);
        String expectedString = "{svcRegulationOn=" + DynaflowParameters.SVC_REGULATION_ON +
                ", shuntRegulationON=" + DynaflowParameters.SHUNT_REGULATION_ON +
                ", automaticSlackBusON=" + DynaflowParameters.AUTOMATIC_SLACK_BUS_ON +
                ", vscAsGenerators=" + DynaflowParameters.VSC_AS_GENERATORS +
                ", lccAsLoads=" + DynaflowParameters.LCC_AS_LOADS + "}";
        assertEquals(expectedString, parametersExt.toString());

    }

    @Test
    public void parametersSerialization() throws IOException {
        LoadFlowParameters parameters = LoadFlowParameters.load(platformConfig);
        parameters.setNoGeneratorReactiveLimits(true);
        parameters.setPhaseShifterRegulationOn(false);

        DynaflowParameters params = new DynaflowParameters();
        params.setSvcRegulationOn(true);
        params.setShuntRegulationOn(false);
        params.setAutomaticSlackBusOn(true);
        params.setVscAsGenerators(false);
        params.setLccAsLoads(true);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        parameters.addExtension(DynaflowParameters.class, params);
        params.writeConfigInputFile(out, parameters);
        String generatedParams = out.toString();

        InputStream inputStream = getClass().getResourceAsStream("/dynaflow/params.json");
        String fileParams = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        assertEquals(fileParams, generatedParams);

    }
}
