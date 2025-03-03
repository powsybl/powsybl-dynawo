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
import com.powsybl.commons.parameters.Parameter;
import com.powsybl.commons.parameters.ParameterType;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@JsonIgnoreProperties(value = { "dumpFilePath" })
public record DumpFileParameters(boolean exportDumpFile, boolean useDumpFile, Path dumpFileFolder, String dumpFile) {

    public static final boolean DEFAULT_EXPORT_DUMP = false;
    public static final boolean DEFAULT_USE_DUMP = false;
    public static final Path DEFAULT_DUMP_FOLDER = null;
    public static final String DEFAULT_DUMP_NAME = null;
    private static final DumpFileParameters DEFAULT_DUMP_FILE_PARAMETERS =
            new DumpFileParameters(DEFAULT_EXPORT_DUMP, DEFAULT_USE_DUMP, DEFAULT_DUMP_FOLDER, DEFAULT_DUMP_NAME);

    private static final String DUMP_EXPORT = "dump.export";
    private static final String DUMP_EXPORT_FOLDER = "dump.exportFolder";
    private static final String DUMP_USE_AS_INPUT = "dump.useAsInput";
    private static final String DUMP_FILE_NAME = "dump.fileName";

    public static final List<Parameter> SPECIFIC_PARAMETERS = List.of(
            new Parameter(DUMP_EXPORT, ParameterType.BOOLEAN, "Static Var Compensator regulation on", DEFAULT_EXPORT_DUMP),
            new Parameter(DUMP_EXPORT_FOLDER, ParameterType.STRING, "Static Var Compensator regulation on", DEFAULT_USE_DUMP),
            new Parameter(DUMP_USE_AS_INPUT, ParameterType.BOOLEAN, "Static Var Compensator regulation on", DEFAULT_DUMP_FOLDER),
            new Parameter(DUMP_FILE_NAME, ParameterType.STRING, "Static Var Compensator regulation on", DEFAULT_DUMP_FOLDER)
    );

    public DumpFileParameters {
        boolean exportFolderNotFound = dumpFileFolder == null || Files.notExists(dumpFileFolder);
        if (exportDumpFile && exportFolderNotFound) {
            throw new PowsyblException("Folder " + dumpFileFolder + " set in 'dumpFileFolder' property cannot be found");
        }
        if (useDumpFile && (exportFolderNotFound || dumpFile == null || Files.notExists(dumpFileFolder.resolve(dumpFile)))) {
            throw new PowsyblException("File " + dumpFile + " set in 'dumpFile' property cannot be found");
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

    public static DumpFileParameters createDumpFileParametersFromConfig(ModuleConfig config, Function<String, Path> pathGetter) {
        boolean exportDumpFile = config.getOptionalBooleanProperty(DUMP_EXPORT).orElse(DEFAULT_EXPORT_DUMP);
        Path dumpFileFolder = config.getOptionalStringProperty(DUMP_EXPORT_FOLDER).map(pathGetter).orElse(DEFAULT_DUMP_FOLDER);
        boolean useDumpFile = config.getOptionalBooleanProperty(DUMP_USE_AS_INPUT).orElse(DEFAULT_USE_DUMP);
        String dumpFile = config.getOptionalStringProperty(DUMP_FILE_NAME).orElse(DEFAULT_DUMP_NAME);
        return new DumpFileParameters(exportDumpFile, useDumpFile, dumpFileFolder, dumpFile);
    }

    public static DumpFileParameters createDumpFileParametersFromPropertiesMap(Map<String, String> properties, Function<String, Path> pathGetter) {
        boolean exportDumpFile = Optional.ofNullable(properties.get(DUMP_EXPORT)).map(Boolean::valueOf).orElse(DumpFileParameters.DEFAULT_EXPORT_DUMP);
        Path dumpFileFolder = Optional.ofNullable(properties.get(DUMP_EXPORT_FOLDER)).map(pathGetter).orElse(DEFAULT_DUMP_FOLDER);
        boolean useDumpFile = Optional.ofNullable(properties.get(DUMP_USE_AS_INPUT)).map(Boolean::valueOf).orElse(DEFAULT_USE_DUMP);
        String dumpFile = Optional.ofNullable(properties.get(DUMP_FILE_NAME)).orElse(DEFAULT_DUMP_NAME);
        return new DumpFileParameters(exportDumpFile, useDumpFile, dumpFileFolder, dumpFile);
    }

    public static DumpFileParameters updateDumpFileParametersFromPropertiesMap(Map<String, String> properties,
                                                                        DumpFileParameters dumpFileParameters, Function<String, Path> pathGetter) {
        boolean exportDumpFile = Optional.ofNullable(properties.get(DUMP_EXPORT)).map(Boolean::valueOf).orElse(dumpFileParameters.exportDumpFile);
        Path dumpFileFolder = Optional.ofNullable(properties.get(DUMP_EXPORT_FOLDER)).map(pathGetter).orElse(dumpFileParameters.dumpFileFolder);
        boolean useDumpFile = Optional.ofNullable(properties.get(DUMP_USE_AS_INPUT)).map(Boolean::valueOf).orElse(dumpFileParameters.useDumpFile);
        String dumpFile = Optional.ofNullable(properties.get(DUMP_FILE_NAME)).orElse(dumpFileParameters.dumpFile);
        return new DumpFileParameters(exportDumpFile, useDumpFile, dumpFileFolder, dumpFile);
    }

    public void addParametersToMap(BiConsumer<String, Object> mapAdder) {
        mapAdder.accept(DUMP_EXPORT, exportDumpFile);
        mapAdder.accept(DUMP_EXPORT_FOLDER, dumpFileFolder);
        mapAdder.accept(DUMP_USE_AS_INPUT, useDumpFile);
        mapAdder.accept(DUMP_FILE_NAME, dumpFile);
    }

    public Path getDumpFilePath() {
        return dumpFileFolder != null ?
                dumpFileFolder.resolve(dumpFile)
                : null;
    }
}
