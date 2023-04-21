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
import com.powsybl.dynawaltz.parameters.Set;
import com.powsybl.dynawaltz.xml.ParametersXml;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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
        Map<String, Set> setsMap = ParametersXml.load(fileSystem.getPath("/models.par"));

        Set set1 = setsMap.get("LoadAlphaBeta");
        assertEquals(1.5, set1.getDouble("load_alpha"), 1e-6);
        assertEquals(2.5, set1.getDouble("load_beta"), 1e-6);

        Set set2 = setsMap.get("GeneratorSynchronousFourWindingsProportionalRegulations");
        assertEquals(5.4, set2.getDouble("generator_H"), 1e-6);
        assertEquals(1, set2.getInt("generator_ExcitationPu"));

        Set set3 = setsMap.get("test");
        assertTrue(set3.getBool("boolean"));
        assertEquals("aString", set3.getString("string"));
    }

    @Test
    void checkParametersMisspelled() {
        Path path = fileSystem.getPath("/models_misspelled.par");
        PowsyblException e = assertThrows(PowsyblException.class, () -> ParametersXml.load(path));
        assertEquals("Unexpected element: sett", e.getMessage());
    }

    @Test
    void checkParametersNotFound() {
        Path path = fileSystem.getPath("/file.par");
        UncheckedIOException e = assertThrows(UncheckedIOException.class, () -> ParametersXml.load(path));
        assertEquals("java.nio.file.NoSuchFileException: /file.par", e.getMessage());
    }
}
