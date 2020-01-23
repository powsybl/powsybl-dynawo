/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.dynawo.inputs.dsl.GroovyDslDynawoInputProvider;
import com.powsybl.dynawo.inputs.model.crv.Curve;
import com.powsybl.dynawo.inputs.model.dyd.BlackBoxModel;
import com.powsybl.dynawo.inputs.model.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.inputs.model.job.Job;
import com.powsybl.dynawo.inputs.model.job.Modeler;
import com.powsybl.dynawo.inputs.model.job.Outputs;
import com.powsybl.dynawo.inputs.model.job.Simulation;
import com.powsybl.dynawo.inputs.model.job.Solver;
import com.powsybl.dynawo.inputs.model.par.Parameter;
import com.powsybl.dynawo.inputs.model.par.ParameterSet;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class GroovyDslDynawoInputProviderTest {

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
            "        id '2'",
            "    }",
            "    modeler {",
            "        compile 'compile'",
            "        iidm 'iidm'",
            "        parameters 'parameters'",
            "        parameterId '1'",
            "        dyd 'dyd'",
            "        useStandardModelsPreCompiledModels false",
            "        useStandardModelsModelicaModels false",
            "        preCompiledModelsDir 'preCompiledModelsDir'",
            "        modelicaModelsDir 'modelicaModelsDir'",
            "        initialState 'initialState'",
            "    }",
            "    simulation {",
            "        startTime 0",
            "        stopTime 30",
            "        activeCriteria false",
            "    }",
            "    outputs {",
            "        directory 'directory'",
            "        curve 'curve'",
            "        appenders {",
            "            appender {",
            "                tag 'tag'",
            "                file 'file'",
            "                lvlFilter 'lvlFilter'",
            "            }",
            "        }",
            "    }",
            "}");

        List<Job> jobs = new GroovyDslDynawoInputProvider(dslFile).getDynawoInputs(network).getJobs();
        assertEquals(1, jobs.size());
        Job job = jobs.get(0);
        assertEquals("j1", job.getName());
        Solver solver = job.getSolver();
        assertEquals("lib", solver.getLib());
        assertEquals("file", solver.getParFile());
        assertEquals("2", solver.getParId());
        Modeler modeler = job.getModeler();
        assertEquals("compile", modeler.getCompileDir());
        assertEquals("iidm", modeler.getIidm());
        assertEquals("parameters", modeler.getParameters());
        assertEquals("1", modeler.getParameterId());
        assertEquals("dyd", modeler.getDyd());
        Simulation simulation = job.getSimulation();
        assertEquals(0, simulation.getStartTime());
        assertEquals(30, simulation.getStopTime());
        assertFalse(simulation.isActiveCriteria());
        Outputs outputs = job.getOutputs();
        assertEquals("directory", outputs.getDirectory());
        assertEquals("curve", outputs.getCurve());
    }

    @Test
    public void testCurve() throws IOException {
        writeToDslFile("curve {",
            "    model 'model'",
            "    variable 'variable'",
            "}");

        List<Curve> curves = new GroovyDslDynawoInputProvider(dslFile).getDynawoInputs(network).getCurves();
        assertEquals(1, curves.size());
        Curve curve = curves.get(0);
        assertEquals("model", curve.getModel());
        assertEquals("variable", curve.getVariable());
    }

    @Test
    public void testDynamicModel() throws IOException {
        writeToDslFile("blackBoxModel ('bbid') {",
            "    lib 'bblib'",
            "    parametersFile 'parametersFile'",
            "    parametersId '1'",
            "    staticId 'staticId'",
            "    staticRefs {",
            "        staticRef {",
            "            var 'var1'",
            "            staticVar 'staticVar1'",
            "        }",
            "        staticRef {",
            "            var 'var2'",
            "            staticVar 'staticVar2'",
            "        }",
            "    }",
            "    macroStaticRefs {",
            "        macroStaticRef ('macroStaticid')",
            "    }",
            "}",
            "modelicaModel ('mid') {",
            "    unitDynamicModels {",
            "        unitDynamicModel ('udmid') {",
            "            name 'name'",
            "            moFile 'moFile'",
            "            initName 'initName'",
            "            parametersFile 'parFile'",
            "            parametersId '1'",
            "        }",
            "    }",
            "    connections {",
            "        connection {",
            "            id1 'id1'",
            "            var1 'var1'",
            "            id2 'id2'",
            "            var2 'var2'",
            "        }",
            "    }",
            "    initConnections {",
            "        initConnection {",
            "            id1 'id1'",
            "            var1 'var1'",
            "            id2 'id2'",
            "            var2 'var2'",
            "        }",
            "    }",
            "    staticRefs {",
            "        staticRef {",
            "            var 'var1'",
            "            staticVar 'staticVar1'",
            "        }",
            "        staticRef {",
            "            var 'var2'",
            "            staticVar 'staticVar2'",
            "        }",
            "    }",
            "    macroStaticRefs {",
            "        macroStaticRef ('macroStaticid')",
            "    }",
            "}",
            "modelTemplate ('mtid') {",
            "    unitDynamicModels {",
            "        unitDynamicModel ('udmid') {",
            "            name 'name'",
            "            moFile 'moFile'",
            "            initName 'initName'",
            "            parametersFile 'parFile'",
            "            parametersId '1'",
            "        }",
            "    }",
            "    connections {",
            "        connection {",
            "            id1 'id1'",
            "            var1 'var1'",
            "            id2 'id2'",
            "            var2 'var2'",
            "        }",
            "    }",
            "    initConnections {",
            "        initConnection {",
            "            id1 'id1'",
            "            var1 'var1'",
            "            id2 'id2'",
            "            var2 'var2'",
            "        }",
            "    }",
            "}",
            "modelTemplateExpansion ('mtid') {",
            "    templateId 'templateId'",
            "    parametersFile 'parametersFile'",
            "    parametersId '1'",
            "}",
            "connection {",
            "    id1 'id1'",
            "    var1 'var1'",
            "    id2 'id2'",
            "    var2 'var2'",
            "}",
            "initConnection {",
            "    id1 'id1'",
            "    var1 'var1'",
            "    id2 'id2'",
            "    var2 'var2'",
            "}",
            "macroConnector ('mcid') {",
            "    connections {",
            "        connection {",
            "            var1 'var1'",
            "            var2 'var2'",
            "        }",
            "    }",
            "}",
            "macroStaticRef ('msrid') {",
            "    staticRefs {",
            "        staticRef {",
            "            var 'var'",
            "            staticVar 'staticVar'",
            "        }",
            "    }",
            "}",
            "macroConnection {",
            "    connector 'connector'",
            "    id1 'id1'",
            "    id2 'id2'",
            "}");

        List<DynawoDynamicModel> dynamicModels = new GroovyDslDynawoInputProvider(dslFile).getDynawoInputs(network).getDynamicModels();
        assertEquals(9, dynamicModels.size());
        DynawoDynamicModel dynamicModel = dynamicModels.get(0);
        assertTrue(BlackBoxModel.class.isInstance(dynamicModel));
        BlackBoxModel blackBoxModel = (BlackBoxModel) dynamicModel;
        assertEquals("bblib", blackBoxModel.getLib());
        assertEquals("bbid", blackBoxModel.getId());
        assertEquals("parametersFile", blackBoxModel.getParametersFile());
        assertEquals("1", blackBoxModel.getParametersId());
    }

    @Test
    public void testParameterSet() throws IOException {
        writeToDslFile("parameterSet ('1') {",
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
            "            value 'value2'",
            "        }",
            "    }",
            "    parameterTables {",
            "        parameterTable {",
            "            name 'nameTable'",
            "            type 'typeTable'",
            "            parameterRows {",
            "                parameterRow {",
            "                    row 1",
            "                    column 1",
            "                    value 'valueRow'",
            "                }",
            "            }",
            "        }",
            "    }",
            "}");

        List<ParameterSet> parameterSets = new GroovyDslDynawoInputProvider(dslFile).getDynawoInputs(network).getParameterSets();
        assertEquals(1, parameterSets.size());
        ParameterSet parameterSet = parameterSets.get(0);
        assertEquals("1", parameterSet.getId());
        assertEquals(2, parameterSet.getParameters().size());
        String key = "name1";
        Parameter parameter = parameterSet.getParameters().get(key);
        assertTrue(parameter.isReference());
        assertEquals("name1", parameter.getName());
        assertEquals("type1", parameter.getType());
        assertEquals("origData1", parameter.getOrigData());
        assertEquals("origName1", parameter.getOrigName());
    }

    @Test
    public void testSolverParameterSet() throws IOException {
        writeToDslFile("solverParameterSet ('1') {",
            "    parameters {",
            "        parameter {",
            "            name 'name'",
            "            type 'type'",
            "            value 'value'",
            "        }",
            "    }",
            "}");

        List<ParameterSet> parameterSets = new GroovyDslDynawoInputProvider(dslFile)
            .getDynawoInputs(network).getSolverParameterSets();
        assertEquals(1, parameterSets.size());
        ParameterSet parameterSet = parameterSets.get(0);
        assertEquals("1", parameterSet.getId());
        assertEquals(1, parameterSet.getParameters().size());
        String key = "name";
        Parameter parameter = parameterSet.getParameters().get(key);
        assertFalse(parameter.isReference());
        assertEquals("name", parameter.getName());
        assertEquals("type", parameter.getType());
        assertEquals("value", parameter.getValue());
        assertNull(parameter.getOrigData());
        assertNull(parameter.getOrigName());
    }
}
