/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.computation.local.LocalCommandExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
abstract class AbstractLocalCommandExecutor implements LocalCommandExecutor {

    @Override
    public void stop(Path workingDir) {
        // Nothing to do
    }

    @Override
    public void stopForcibly(Path workingDir) {
        // Nothing to do
    }

    protected void copyFile(String source, Path target) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(source)) {
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
