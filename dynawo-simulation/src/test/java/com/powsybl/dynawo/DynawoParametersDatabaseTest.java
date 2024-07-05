/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.parameters.ParameterType;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.ParametersXml;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class DynawoParametersDatabaseTest {

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
        List<ParametersSet> setsMap = ParametersXml.load(fileSystem.getPath("/models.par"));
        DynawoSimulationParameters dParameters = new DynawoSimulationParameters().setModelsParameters(setsMap);

        ParametersSet set1 = dParameters.getModelParameters("LoadAlphaBeta");
        assertEquals(1.5, set1.getDouble("load_alpha"), 1e-6);
        assertEquals(2.5, set1.getDouble("load_beta"), 1e-6);

        ParametersSet set2 = dParameters.getModelParameters("GeneratorSynchronousFourWindingsProportionalRegulations");
        assertEquals(5.4, set2.getDouble("generator_H"), 1e-6);
        assertEquals(1, set2.getInt("generator_ExcitationPu"));

        ParametersSet set3 = dParameters.getModelParameters("test");
        assertFalse(set3.hasParameter("unknown"));
        assertTrue(set3.hasParameter("boolean"));
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

    @Test
    void addParametersSet() {
        DynawoSimulationParameters dParameters = new DynawoSimulationParameters();
        ParametersSet set = new ParametersSet("test");
        dParameters.addModelParameters(set);
        assertEquals(1, dParameters.getModelParameters().size());
        assertEquals(set, dParameters.getModelParameters("test"));
    }

    @Test
    void replaceParameter() {
        ParametersSet set = new ParametersSet("test");
        String param = "modifiedParam";
        set.addParameter(param, ParameterType.DOUBLE, "2.2");
        set.replaceParameter(param, ParameterType.INT, "3");
        assertEquals(3, set.getInt(param));
        assertThrows(PowsyblException.class, () -> set.getDouble(param));
    }

    @Test
    void copyParametersSet() {
        ParametersSet set0 = new ParametersSet("test");
        set0.addParameter("param", ParameterType.INT, "2");
        ParametersSet set1 = new ParametersSet("copy", set0);
        assertEquals(2, set1.getInt("param"));
    }
}
