/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.PowsyblException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynaWaltzParametersDatabaseTest {

    private FileSystem fileSystem;

    @BeforeEach
    void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Files.copy(getClass().getResourceAsStream("/models.par"), fileSystem.getPath("/models.par"));
        Files.copy(getClass().getResourceAsStream("/models_misspelled.par"), fileSystem.getPath("/models_misspelled.par"));
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void checkParameters() {
        DynaWaltzParametersDatabase parametersDatabase = DynaWaltzParametersDatabase.load(fileSystem.getPath("/models.par"));
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
    void checkParametersMisspelled() {
        Path path = fileSystem.getPath("/models_misspelled.par");
        PowsyblException e = assertThrows(PowsyblException.class, () -> DynaWaltzParametersDatabase.load(path));
        assertEquals("Unexpected element: sett", e.getMessage());
    }

    @Test
    void checkParametersNotFound() {
        Path path = fileSystem.getPath("/file.par");
        UncheckedIOException e = assertThrows(UncheckedIOException.class, () -> DynaWaltzParametersDatabase.load(path));
        assertEquals("java.nio.file.NoSuchFileException: /file.par", e.getMessage());
    }
}
