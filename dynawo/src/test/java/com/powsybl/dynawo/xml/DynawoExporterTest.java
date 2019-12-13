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
import java.nio.file.Files;
import java.util.Properties;

import javax.xml.stream.XMLStreamException;

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
        dslFile = getClass().getResourceAsStream("/exportTest.groovy");
        exporter = new DynawoXmlExporter();
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void export() throws IOException, XMLStreamException {
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

    @Test
    public void testJob() throws IOException, XMLStreamException {
        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        try (InputStream is = Files.newInputStream(tmpDir.resolve("dynawoModel.jobs"))) {
            assertNotNull(is);
            compareXml(getClass().getResourceAsStream("/dynawoModel.jobs"), is);
        }
    }

    @Test
    public void testCurve() throws IOException, XMLStreamException {
        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        try (InputStream is = Files.newInputStream(tmpDir.resolve("dynawoModel.crv"))) {
            assertNotNull(is);
            compareXml(getClass().getResourceAsStream("/dynawoModel.crv"), is);
        }
    }

    @Test
    public void testDynamicModel() throws IOException, XMLStreamException {
        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        try (InputStream is = Files.newInputStream(tmpDir.resolve("dynawoModel.dyd"))) {
            assertNotNull(is);
            compareXml(getClass().getResourceAsStream("/dynawoModel.dyd"), is);
        }
    }

    @Test
    public void testParameterSet() throws IOException, XMLStreamException {
        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        try (InputStream is = Files.newInputStream(tmpDir.resolve("dynawoModel.par"))) {
            assertNotNull(is);
            compareXml(getClass().getResourceAsStream("/dynawoModel.par"), is);
        }
    }

    @Test
    public void testSolverParameterSet() throws IOException, XMLStreamException {
        exporter.export(network, new GroovyDslDynawoInputProvider(dslFile), tmpDir);
        try (InputStream is = Files.newInputStream(tmpDir.resolve("solvers.par"))) {
            assertNotNull(is);
            compareXml(getClass().getResourceAsStream("/solvers.par"), is);
        }
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
    private InputStream dslFile;
    private DynawoXmlExporter exporter;
}
