/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.ModuleConfig;

import java.nio.file.FileSystem;
import java.nio.file.Files;
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
    private static final DumpFileParameters DEFAULT_DUMP_FILE_PARAMETERS = new DumpFileParameters(DEFAULT_EXPORT_DUMP, DEFAULT_USE_DUMP, null, DEFAULT_DUMP_NAME);

    public DumpFileParameters {
        if (useDumpFile) {
            Objects.requireNonNull(dumpFileFolder);
            Objects.requireNonNull(dumpFile);
        } else if (exportDumpFile) {
            Objects.requireNonNull(dumpFileFolder);
        }
    }

    public static DumpFileParameters createDefaultDumpFileParameters() {
        return DEFAULT_DUMP_FILE_PARAMETERS;
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

    public static DumpFileParameters createDumpFileParametersFromConfig(ModuleConfig config, FileSystem fileSystem) {
        boolean exportDumpFile = config.getOptionalBooleanProperty("dump.export").orElse(DumpFileParameters.DEFAULT_EXPORT_DUMP);
        String exportDumpFileFolder = config.getOptionalStringProperty("dump.exportFolder").orElse(DumpFileParameters.DEFAULT_DUMP_FOLDER);
        Path exportDumpFileFolderPath = exportDumpFileFolder != null ? fileSystem.getPath(exportDumpFileFolder) : null;
        boolean exportFolderNotFound = exportDumpFileFolderPath == null || !Files.exists(exportDumpFileFolderPath);
        if (exportDumpFile && exportFolderNotFound) {
            throw new PowsyblException("Folder " + exportDumpFileFolder + " set in 'dumpFileFolder' property cannot be found");
        }
        boolean useDumpFile = config.getOptionalBooleanProperty("dump.useAsInput").orElse(DumpFileParameters.DEFAULT_USE_DUMP);
        String dumpFile = config.getOptionalStringProperty("dump.fileName").orElse(DumpFileParameters.DEFAULT_DUMP_NAME);
        if (useDumpFile && (exportFolderNotFound || dumpFile == null || !Files.exists(exportDumpFileFolderPath.resolve(dumpFile)))) {
            throw new PowsyblException("File " + dumpFile + " set in 'dumpFile' property cannot be found");
        }
        return new DumpFileParameters(exportDumpFile, useDumpFile, exportDumpFileFolderPath, dumpFile);
    }

    public Path getDumpFilePath() {
        return dumpFileFolder != null ?
                dumpFileFolder.resolve(dumpFile)
                : null;
    }
}
