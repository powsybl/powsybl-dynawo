/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParametersDatabaseTest {

    private FileSystem fileSystem;

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Files.copy(getClass().getResourceAsStream("/models.par"), fileSystem.getPath("/models.par"));
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void checkParameters() {
        try {
            DynawoParametersDatabase parametersDatabase = DynawoParametersDatabase.load(fileSystem.getPath("/models.par"));
            assertNotNull(parametersDatabase.getParameterSet("LoadAlphaBeta"));
            assertEquals("1.5", parametersDatabase.getParameterSet("LoadAlphaBeta").getParameter("load_alpha").getValue());
            assertEquals("2.5", parametersDatabase.getParameterSet("LoadAlphaBeta").getParameter("load_beta").getValue());
            assertNotNull(parametersDatabase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations"));
            assertEquals("5.4000000000000004", parametersDatabase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations").getParameter("generator_H").getValue());
            assertEquals("1211", parametersDatabase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations").getParameter("generator_SNom").getValue());
        } catch (IOException ignored) {
        }
    }
}
