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

import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParametersDataBaseTest {

    private FileSystem fileSystem;

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Files.copy(getClass().getResourceAsStream("/models.par"), fileSystem.getPath("/models.par"));
    }

    @Test
    public void checkParameters() {
        DynawoParametersDataBase parametersDataBase = DynawoParametersDataBase.load(fileSystem.getPath("/models.par"));
        assertNotNull(parametersDataBase.getParameterSet("LoadAlphaBeta"));
        assertEquals("1.5", parametersDataBase.getParameterSet("LoadAlphaBeta").getParameter("load_alpha").getValue());
        assertEquals("2.5", parametersDataBase.getParameterSet("LoadAlphaBeta").getParameter("load_beta").getValue());
        assertEquals("p_pu", parametersDataBase.getParameterSet("LoadAlphaBeta").getReference("load_P0Pu").getOrigName());
        assertEquals("q_pu", parametersDataBase.getParameterSet("LoadAlphaBeta").getReference("load_Q0Pu").getOrigName());
        assertNotNull(parametersDataBase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations"));
        assertEquals("5.4000000000000004", parametersDataBase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations").getParameter("generator_H").getValue());
        assertEquals("1211", parametersDataBase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations").getParameter("generator_SNom").getValue());
        assertEquals("p_pu", parametersDataBase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations").getReference("generator_P0Pu").getOrigName());
        assertEquals("q_pu", parametersDataBase.getParameterSet("GeneratorSynchronousFourWindingsProportionalRegulations").getReference("generator_Q0Pu").getOrigName());
    }
}
