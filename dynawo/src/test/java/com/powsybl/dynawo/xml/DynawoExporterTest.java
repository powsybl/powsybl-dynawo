/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.test.TestGridModel;
import com.powsybl.cgmes.model.test.cim14.Cim14SmallCasesCatalog;
import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.dynawo.dsl.GroovyDslDynawoInputProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.triplestore.api.TripleStoreFactory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoExporterTest extends AbstractConverterTest {

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        tmpDir = Files.createDirectory(fileSystem.getPath("/tmp"));
        network = NetworkFactory.findDefault().createNetwork("network1", "test");
        dynawoProvider = new GroovyDslDynawoInputProvider(getClass().getResourceAsStream("/nordic32/nordic32.groovy"));
        dslFile = fileSystem.getPath("/test.dsl");
        exporter = new DynawoXmlExporter();
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void export() throws IOException {
        network = importNetwork(Cim14SmallCasesCatalog.nordic32());
        network.setCaseDate(DateTime.parse("2019-09-23T11:06:12.313+02:00"));
        exporter.export(network, dynawoProvider, tmpDir);
        Files.walk(tmpDir).forEach(file -> {
            if (Files.isRegularFile(file)) {
                try (InputStream is = Files.newInputStream(file)) {
                    assertNotNull(is);
                    compareXml(getClass().getResourceAsStream("/nordic32/" + file.getFileName()), is);
                } catch (IOException ignored) {
                }
            }
        });
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

        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        InputStream is = Files.newInputStream(tmpDir.resolve("dynawoModel.jobs"));
        assertNotNull(is);
        compareXml(getClass().getResourceAsStream("/dynawoModel.jobs"), is);
    }

    @Test
    public void testCurve() throws IOException {
        writeToDslFile("curve {",
            "    model 'model'",
            "    variable 'variable'",
            "}");

        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        InputStream is = Files.newInputStream(tmpDir.resolve("dynawoModel.crv"));
        assertNotNull(is);
        compareXml(getClass().getResourceAsStream("/dynawoModel.crv"), is);
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

        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        InputStream is = Files.newInputStream(tmpDir.resolve("dynawoModel.dyd"));
        assertNotNull(is);
        compareXml(getClass().getResourceAsStream("/dynawoModel.dyd"), is);
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
            "            componentId 'componentId1'",
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

        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        InputStream is = Files.newInputStream(tmpDir.resolve("dynawoModel.par"));
        assertNotNull(is);
        compareXml(getClass().getResourceAsStream("/dynawoModel.par"), is);
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

        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        InputStream is = Files.newInputStream(tmpDir.resolve("solvers.par"));
        assertNotNull(is);
        compareXml(getClass().getResourceAsStream("/solvers.par"), is);
    }

    private Network importNetwork(TestGridModel gm) throws IOException {
        String impl = TripleStoreFactory.defaultImplementation();
        CgmesImport i = new CgmesImport();
        Properties params = new Properties();
        params.put("storeCgmesModelAsNetworkExtension", "true");
        params.put("powsyblTripleStore", impl);
        ReadOnlyDataSource ds = gm.dataSource();
        Network n = i.importData(ds, NetworkFactory.findDefault(), params);
        return n;
    }

    private Network network;
    private DynawoInputProvider dynawoProvider;
    private Path dslFile;
    private DynawoXmlExporter exporter;
}
