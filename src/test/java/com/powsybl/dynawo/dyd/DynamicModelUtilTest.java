/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dyd;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;

import org.junit.Before;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.dynawo.simulator.DynawoParametersDatabase;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynamicModelUtilTest {

    protected DynawoParametersDatabase parametersDatabase;

    @Before
    public void setup() throws IOException {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Files.copy(getClass().getResourceAsStream("/models.par"), fileSystem.getPath("/models.par"));

        parametersDatabase = DynawoParametersDatabase.load(fileSystem.getPath("/models.par"));
    }
}
