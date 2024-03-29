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
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@JsonIgnoreProperties(value = { "dumpFilePath" })
public record DumpFileParameters(boolean exportDumpFile, boolean useDumpFile, Path dumpFileFolder, String dumpFile) {

    public static final boolean DEFAULT_EXPORT_DUMP = false;
    public static final boolean DEFAULT_USE_DUMP = false;
    public static final String DEFAULT_DUMP_FOLDER = null;
    public static final String DEFAULT_DUMP_NAME = null;

    public DumpFileParameters {
        if (useDumpFile) {
            Objects.requireNonNull(dumpFileFolder);
            Objects.requireNonNull(dumpFile);
        } else if (exportDumpFile) {
            Objects.requireNonNull(dumpFileFolder);
        }
    }

    public static DumpFileParameters createDefaultDumpFileParameters() {
        return new DumpFileParameters(DEFAULT_EXPORT_DUMP, DEFAULT_USE_DUMP, null, DEFAULT_DUMP_NAME);
    }

    public static DumpFileParameters createExportDumpFileParameters(Path dumpFileFolder) {
        return new DumpFileParameters(true, false, dumpFileFolder, null);
    }

    public static DumpFileParameters createImportDumpFileParameters(Path dumpFileFolder, String dumpFile) {
        return new DumpFileParameters(false, true, dumpFileFolder, dumpFile);
    }

    public static DumpFileParameters createImportExportDumpFileParameters(Path dumpFileFolder, String dumpFile) {
        return new DumpFileParameters(true, true, dumpFileFolder, dumpFile);
    }

    public Path getDumpFilePath() {
        return dumpFileFolder != null ?
                dumpFileFolder.resolve(dumpFile)
                : null;
    }
}
