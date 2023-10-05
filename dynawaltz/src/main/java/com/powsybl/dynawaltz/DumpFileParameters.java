/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.nio.file.Path;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
@JsonIgnoreProperties(value = { "dumpFilePath" })
public record DumpFileParameters(boolean exportDumpFile, boolean useDumpFile, Path exportDumpFileFolder, String dumpFile) {

    public static final boolean DEFAULT_EXPORT_DUMP = false;
    public static final boolean DEFAULT_USE_DUMP = false;
    public static final String DEFAULT_DUMP_FOLDER = null;
    public static final String DEFAULT_DUMP_NAME = null;

    public DumpFileParameters {
        if (useDumpFile) {
            Objects.requireNonNull(exportDumpFileFolder);
            Objects.requireNonNull(dumpFile);
        } else if (exportDumpFile) {
            Objects.requireNonNull(exportDumpFileFolder);
        }
    }

    public static DumpFileParameters createDefaultDumpFileParameters() {
        return new DumpFileParameters(DEFAULT_EXPORT_DUMP, DEFAULT_USE_DUMP, null, DEFAULT_DUMP_NAME);
    }

    public Path getDumpFilePath() {
        return exportDumpFileFolder != null ?
                exportDumpFileFolder.resolve(dumpFile)
                : null;
    }
}
