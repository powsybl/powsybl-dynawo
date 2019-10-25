/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.exporter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.file.FileSystem;
import java.nio.file.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoExporterTest {

    private FileSystem fileSystem;

    private InMemoryPlatformConfig platformConfig;

    private Network network;

    private DynawoInputProvider dynawoInputProvider;

    private Path workingDir;

    @Before
    public void setUp() throws Exception {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        platformConfig = new InMemoryPlatformConfig(fileSystem);
        network = Mockito.mock(Network.class);
        dynawoInputProvider = Mockito.mock(DynawoInputProvider.class);
        workingDir = fileSystem.getPath("tmp");
    }

    @After
    public void tearDown() throws Exception {
        fileSystem.close();
    }

    @Test
    public void testDefaultOneProvider() {
        // case with only one provider, no need for config
        DynawoExporter.Runner defaultExporter = DynawoExporter.find(null, ImmutableList.of(new DynawoExporterProviderMock()), platformConfig);
        assertEquals("DynawoExporterProviderMock", defaultExporter.getName());
        String result = defaultExporter.export(network, dynawoInputProvider, workingDir);
        assertNotNull(result);
    }

    @Test
    public void testDefaultTwoProviders() {
        // case with 2 providers without any config, an exception is expected
        try {
            DynawoExporter.find(null, ImmutableList.of(new DynawoExporterProviderMock(), new AnotherDynawoExporterProviderMock()), platformConfig);
            fail();
        } catch (PowsyblException ignored) {
        }
    }

    @Test
    public void testDefaultNoProvider() {
        // case without any provider
        try {
            DynawoExporter.find(null, ImmutableList.of(), platformConfig);
            fail();
        } catch (PowsyblException ignored) {
        }
    }

    @Test
    public void testTwoProviders() {
        // case with 2 providers without any config but specifying which one to use
        // programmatically
        DynawoExporter.Runner otherDynamicSimulation = DynawoExporter.find("AnotherDynawoExporterProviderMock",
            ImmutableList.of(new DynawoExporterProviderMock(), new AnotherDynawoExporterProviderMock()), platformConfig);
        assertEquals("AnotherDynawoExporterProviderMock", otherDynamicSimulation.getName());
    }

    @Test
    public void testDefaultTwoProvidersPlatformConfig() {
        // case with 2 providers without any config but specifying which one to use in
        // platform config
        platformConfig.createModuleConfig("dynawo-exporter").setStringProperty("default", "AnotherDynawoExporterProviderMock");
        DynawoExporter.Runner otherDynamicSimulation2 = DynawoExporter.find(null,
            ImmutableList.of(new DynawoExporterProviderMock(), new AnotherDynawoExporterProviderMock()), platformConfig);
        assertEquals("AnotherDynawoExporterProviderMock", otherDynamicSimulation2.getName());
    }

    @Test(expected = PowsyblException.class)
    public void testOneProviderAndMistakeInPlatformConfig() {
        // case with 1 provider with config but with a name that is not the one of
        // provider.
        platformConfig.createModuleConfig("dynawo-exporter").setStringProperty("default", "AnotherDynawoExporterProviderMock");
        DynawoExporter.find(null, ImmutableList.of(new DynawoExporterProviderMock()), platformConfig);
    }
}
