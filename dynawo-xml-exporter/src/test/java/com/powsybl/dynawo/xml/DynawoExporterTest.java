/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.cgmes.conversion.CgmesImport;
import com.powsybl.cgmes.model.test.TestGridModel;
import com.powsybl.cgmes.model.test.cim14.Cim14SmallCasesCatalog;
import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.ReadOnlyDataSource;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.dynawo.dsl.GroovyDslDynawoProvider;
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
        platformConfig = new InMemoryPlatformConfig(fileSystem);
        network = importNetwork(platformConfig, catalog.nordic32());
        network.setCaseDate(DateTime.parse("2019-09-23T11:06:12.313+02:00"));
        dynawoProvider = new GroovyDslDynawoProvider(getClass().getResourceAsStream("/nordic32/nordic32.groovy"));
    }

    @Test
    public void export() throws IOException {
        DynawoXmlExporter exporter = new DynawoXmlExporter(platformConfig);
        exporter.export(network, dynawoProvider, tmpDir);
        Files.walk(tmpDir).forEach(file -> {
            if (Files.isRegularFile(file)) {
                try (InputStream is = Files.newInputStream(file)) {
                    compareXml(getClass().getResourceAsStream("/nordic32/" + file.getFileName()), is);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        });
    }

    private Network importNetwork(PlatformConfig platformConfig, TestGridModel gm) throws IOException {
        String impl = TripleStoreFactory.defaultImplementation();
        CgmesImport i = new CgmesImport(platformConfig);
        Properties params = new Properties();
        params.put("storeCgmesModelAsNetworkExtension", "true");
        params.put("powsyblTripleStore", impl);
        ReadOnlyDataSource ds = gm.dataSource();
        Network n = i.importData(ds, NetworkFactory.findDefault(), params);
        return n;
    }

    private Network network;
    private DynawoProvider dynawoProvider;
    private final Cim14SmallCasesCatalog catalog = new Cim14SmallCasesCatalog();
    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoExporterTest.class);
}
