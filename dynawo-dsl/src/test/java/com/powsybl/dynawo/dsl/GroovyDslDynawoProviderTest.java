/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.cgmes.model.test.cim14.Cim14SmallCasesCatalog;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.DynawoCurve;
import com.powsybl.dynawo.DynawoDynamicModel;
import com.powsybl.dynawo.DynawoJob;
import com.powsybl.dynawo.DynawoModeler;
import com.powsybl.dynawo.DynawoOutputs;
import com.powsybl.dynawo.DynawoSimulation;
import com.powsybl.dynawo.DynawoSolver;
import com.powsybl.dynawo.DynawoParameter;
import com.powsybl.dynawo.DynawoParameterSet;
import com.powsybl.dynawo.simulator.DynawoSimulatorTester;
import com.powsybl.dynawo.simulator.results.DynawoResults;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class GroovyDslDynawoProviderTest {

    private FileSystem fileSystem;

    private Path dslFile;

    private Network network;

    @Before
    public void setUp() {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        dslFile = fileSystem.getPath("/test.dsl");
        network = EurostagTutorialExample1Factory.create();
    }

    @After
    public void tearDown() throws Exception {
        fileSystem.close();
    }

    private void writeToDslFile(String... lines) throws IOException {
        try (Writer writer = Files.newBufferedWriter(dslFile, StandardCharsets.UTF_8)) {
            writer.write(String.join(System.lineSeparator(), lines));
        }
    }

    @Test
    public void testJob() throws IOException {
        writeToDslFile("job('j1') {",
            "    solver {",
            "        lib 'lib'",
            "        file 'file'",
            "        id 2",
            "    }",
            "    modeler {",
            "        compile 'compile'",
            "        iidm 'iidm'",
            "        parameters 'parameters'",
            "        parameterId 1",
            "        dyd 'dyd'",
            "    }",
            "    simulation {",
            "        startTime 0",
            "        stopTime 30",
            "        activeCriteria false",
            "    }",
            "    outputs {",
            "        directory 'directory'",
            "        curve 'curve'",
            "    }",
            "}");

        List<DynawoJob> jobs = new GroovyDslDynawoProvider(dslFile).getDynawoJob(network);
        assertEquals(1, jobs.size());
        DynawoJob job = jobs.get(0);
        assertEquals("j1", job.getName());
        DynawoSolver solver = job.getSolver();
        assertEquals("lib", solver.getLib());
        assertEquals("file", solver.getFile());
        assertEquals(2, solver.getId());
        DynawoModeler modeler = job.getModeler();
        assertEquals("compile", modeler.getCompile());
        assertEquals("iidm", modeler.getIidm());
        assertEquals("parameters", modeler.getParameters());
        assertEquals(1, modeler.getParameterId());
        assertEquals("dyd", modeler.getDyd());
        DynawoSimulation simulation = job.getSimulation();
        assertEquals(0, simulation.getStartTime());
        assertEquals(30, simulation.getStopTime());
        assertFalse(simulation.isActiveCriteria());
        DynawoOutputs outputs = job.getOutputs();
        assertEquals("directory", outputs.getDirectory());
        assertEquals("curve", outputs.getCurve());
    }

    @Test
    public void testCurve() throws IOException {
        writeToDslFile("curve {",
            "    model 'model'",
            "    variable 'variable'",
            "}");

        List<DynawoCurve> curves = new GroovyDslDynawoProvider(dslFile).getDynawoCurves(network);
        assertEquals(1, curves.size());
        DynawoCurve curve = curves.get(0);
        assertEquals("model", curve.getModel());
        assertEquals("variable", curve.getVariable());
    }

    @Test
    public void testDynamicModel() throws IOException {
        writeToDslFile("dynamicModel {",
            "    blackBoxModelId 'bbid'",
            "    blackBoxModelLib 'bblib'",
            "    parametersFile 'parametersFile'",
            "    parametersId 1",
            "}");

        List<DynawoDynamicModel> dynamicModels = new GroovyDslDynawoProvider(dslFile).getDynawoDynamicModels(network);
        assertEquals(1, dynamicModels.size());
        DynawoDynamicModel dynamicModel = dynamicModels.get(0);
        assertTrue(dynamicModel.isBlackBoxModel());
        assertEquals("bblib", dynamicModel.getBlackBoxModelLib());
        assertEquals("bbid", dynamicModel.getBlackBoxModelId());
        assertEquals("parametersFile", dynamicModel.getParametersFile());
        assertEquals(1, dynamicModel.getParametersId());
    }

    @Test
    public void testParameterSet() throws IOException {
        writeToDslFile("parameterSet (1) {",
            "    parameters {",
            "        parameter {",
            "            name 'name1'",
            "            type 'type1'",
            "            origData 'origData1'",
            "            origName 'origName1'",
            "        }",
            "        parameter {",
            "            name 'name2'",
            "            type 'type2'",
            "            origData 'origData2'",
            "            origName 'origName2'",
            "        }",
            "    }",
            "}");

        List<DynawoParameterSet> parameterSets = new GroovyDslDynawoProvider(dslFile).getDynawoParameterSets(network);
        assertEquals(1, parameterSets.size());
        DynawoParameterSet parameterSet = parameterSets.get(0);
        assertEquals(1, parameterSet.getId());
        assertEquals(2, parameterSet.getParameters().size());
        DynawoParameter parameter = parameterSet.getParameters().get(0);
        assertTrue(parameter.isReference());
        assertEquals("name1", parameter.getName());
        assertEquals("type1", parameter.getType());
        assertEquals("origData1", parameter.getOrigData());
        assertEquals("origName1", parameter.getOrigName());
    }

    @Test
    public void testSolverParameterSet() throws IOException {
        writeToDslFile("solverParameterSet (1) {",
            "    parameters {",
            "        parameter {",
            "            name 'name'",
            "            type 'type'",
            "            value 'value'",
            "        }",
            "    }",
            "}");

        List<DynawoParameterSet> parameterSets = new GroovyDslDynawoProvider(dslFile)
            .getDynawoSolverParameterSets(network);
        assertEquals(1, parameterSets.size());
        DynawoParameterSet parameterSet = parameterSets.get(0);
        assertEquals(1, parameterSet.getId());
        assertEquals(1, parameterSet.getParameters().size());
        DynawoParameter parameter = parameterSet.getParameters().get(0);
        assertFalse(parameter.isReference());
        assertEquals("name", parameter.getName());
        assertEquals("type", parameter.getType());
        assertEquals("value", parameter.getValue());
    }

    @Test
    public void testNordic32() throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {

            PlatformConfig platformConfig = configure(fs);
            DynawoSimulatorTester tester = new DynawoSimulatorTester(platformConfig, true);
            Network network = tester.convert(platformConfig, catalog.nordic32());
            DynawoProvider provider = new GroovyDslDynawoProvider(getClass().getResourceAsStream("/nordic32.groovy"));
            DynawoResults result = tester.testGridModel(network, provider);
            LOGGER.info("metrics " + result.getMetrics().get("success"));
            assertTrue(Boolean.parseBoolean(result.getMetrics().get("success")));

            // check final voltage of bus close to the event
            int index = result.getNames().indexOf("NETWORK__N1011____TN_Upu_value");
            assertEquals(result.getTimeSerie().get(new Double(30.0)).get(index), new Double(0.931558));
        }
    }

    private PlatformConfig configure(FileSystem fs) throws IOException {
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fs);
        Files.createDirectory(fs.getPath("/dynawoPath"));
        Files.createDirectory(fs.getPath("/workingPath"));
        Files.createDirectories(fs.getPath("/tmp"));
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("import-export-parameters-default-value");
        moduleConfig.setStringProperty("iidm.export.xml.extensions", "null");
        moduleConfig = platformConfig.createModuleConfig("computation-local");
        moduleConfig.setStringProperty("tmpDir", "/tmp");
        moduleConfig = platformConfig.createModuleConfig("dynawo");
        moduleConfig.setStringProperty("dynawoHomeDir", "/dynawoPath");
        moduleConfig.setStringProperty("workingDir", "/workingPath");
        moduleConfig.setStringProperty("debug", "false");
        moduleConfig.setStringProperty("dynawoCptCommandName", "myEnvDynawo.sh");
        moduleConfig = platformConfig.createModuleConfig("simulation-parameters");
        moduleConfig.setStringProperty("preFaultSimulationStopInstant", "1");
        moduleConfig.setStringProperty("postFaultSimulationStopInstant", "60");
        moduleConfig.setStringProperty("faultEventInstant", "30");
        moduleConfig.setStringProperty("branchSideOneFaultShortCircuitDuration", "60");
        moduleConfig.setStringProperty("branchSideTwoFaultShortCircuitDuration", "60");
        moduleConfig.setStringProperty("generatorFaultShortCircuitDuration", "60");
        return platformConfig;
    }

    private final Cim14SmallCasesCatalog catalog = new Cim14SmallCasesCatalog();
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyDslDynawoProviderTest.class);
}
