/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.io.IOException;
import java.io.UncheckedIOException;
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

import static org.junit.Assert.*;

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
        Files.copy(getClass().getResourceAsStream("/models_misspelled.par"), fileSystem.getPath("/models_misspelled.par"));
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void checkParameters() {
        DynawoParametersDatabase parametersDatabase = DynawoParametersDatabase.load(fileSystem.getPath("/models.par"));
        assertNotNull(parametersDatabase.getParameterSet("LoadAlphaBeta"));
        assertEquals(1.5, parametersDatabase.getDouble("LoadAlphaBeta", "load_alpha"), 1e-6);
        assertEquals(2.5, parametersDatabase.getDouble("LoadAlphaBeta", "load_beta"), 1e-6);
        assertNotNull(parametersDatabase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations"));
        assertEquals(5.4, parametersDatabase.getDouble("GeneratorSynchronousFourWindingsProportionalRegulations", "generator_H"), 1e-6);
        assertEquals(1, parametersDatabase.getInt("GeneratorSynchronousFourWindingsProportionalRegulations", "generator_ExcitationPu"));
        assertTrue(parametersDatabase.getBool("test", "boolean"));
        assertEquals("aString", parametersDatabase.getString("test", "string"));
    }

    @Test
    public void checkParametersMisspelled() {
        exception.expect(PowsyblException.class);
        exception.expectMessage("Unexpected element: sett");

        DynawoParametersDatabase.load(fileSystem.getPath("/models_misspelled.par"));
    }

    @Test
    public void checkParametersNotFound() {
        exception.expect(UncheckedIOException.class);
        exception.expectMessage("NoSuchFileException: /file.par");

        DynawoParametersDatabase.load(fileSystem.getPath("/file.par"));
    }
}
