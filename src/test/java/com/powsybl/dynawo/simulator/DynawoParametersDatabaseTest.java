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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.PowsyblException;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParametersDatabaseTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private FileSystem fileSystem;

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Files.copy(getClass().getResourceAsStream("/models.par"), fileSystem.getPath("/models.par"));
        Files.copy(getClass().getResourceAsStream("/models_reference.par"), fileSystem.getPath("/models_reference.par"));
        Files.copy(getClass().getResourceAsStream("/models_misspelled.par"), fileSystem.getPath("/models_misspelled.par"));
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void checkParameters() throws IOException {
        DynawoParametersDatabase parametersDatabase = DynawoParametersDatabase.load(fileSystem.getPath("/models.par"));
        assertNotNull(parametersDatabase.getParameterSet("LoadAlphaBeta"));
        assertEquals("1.5", parametersDatabase.getParameterSet("LoadAlphaBeta").getParameter("load_alpha").getValue());
        assertEquals("2.5", parametersDatabase.getParameterSet("LoadAlphaBeta").getParameter("load_beta").getValue());
        assertNotNull(parametersDatabase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations"));
        assertEquals("5.4000000000000004",
            parametersDatabase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations").getParameter("generator_H").getValue());
        assertEquals("1211",
            parametersDatabase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations").getParameter("generator_SNom").getValue());
    }

    @Test
    public void checkParametersWithReference() throws IOException {
        exception.expect(PowsyblException.class);
        exception.expectMessage("Unexpected element: reference");

        DynawoParametersDatabase.load(fileSystem.getPath("/models_reference.par"));
    }

    @Test
    public void checkParametersMisspelled() throws IOException {
        exception.expect(PowsyblException.class);
        exception.expectMessage("Unexpected element: sett");

        DynawoParametersDatabase.load(fileSystem.getPath("/models_misspelled.par"));
    }

    @Test
    public void checkParametersNotFound() throws IOException {
        exception.expect(IOException.class);
        exception.expectMessage("/file.par not found");

        DynawoParametersDatabase.load(fileSystem.getPath("/file.par"));
    }
}
