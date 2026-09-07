/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.commons.config.ModuleConfig;
import com.powsybl.commons.parameters.Parameter;
import com.powsybl.commons.parameters.ParameterType;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public record DumpInitValuesParameters(boolean useDumpInit, Path dumpInitFolder) {

    public static final boolean DEFAULT_USE_DUMP_INIT = false;
    public static final Path DEFAULT_DUMP_INIT_FOLDER = null;
    private static final DumpInitValuesParameters DEFAULT_DUMP_FILE_PARAMETERS =
            new DumpInitValuesParameters(DEFAULT_USE_DUMP_INIT, DEFAULT_DUMP_INIT_FOLDER);

    private static final String DUMP_INIT_USE = "dumpInitValues.export";
    private static final String DUMP_INIT_FOLDER = "dumpInitValues.exportfolder";

    public static final List<Parameter> SPECIFIC_PARAMETERS = List.of(
            new Parameter(DUMP_INIT_USE, ParameterType.BOOLEAN, "Write the values of the initialization model", DEFAULT_USE_DUMP_INIT),
            new Parameter(DUMP_INIT_FOLDER, ParameterType.STRING, "Folder to export the values of the initialization model", DEFAULT_DUMP_INIT_FOLDER)
    );

    public static DumpInitValuesParameters createDefaultDumpInitValuesParameters() {
        return DEFAULT_DUMP_FILE_PARAMETERS;
    }

    public static DumpInitValuesParameters createDumpInitValuesParameters(boolean useDumbInit, Path dumpInitFolder) {
        return new DumpInitValuesParameters(useDumbInit, dumpInitFolder);
    }

    public static DumpInitValuesParameters createDumpInitValuesParametersFromConfig(ModuleConfig config, Function<String, Path> pathGetter) {
        boolean useDumpInit = config.getOptionalBooleanProperty(DUMP_INIT_USE).orElse(DEFAULT_USE_DUMP_INIT);
        Path dumpInitFolder = config.getOptionalStringProperty(DUMP_INIT_FOLDER).map(pathGetter).orElse(DEFAULT_DUMP_INIT_FOLDER);
        return new DumpInitValuesParameters(useDumpInit, dumpInitFolder);
    }

    public static DumpInitValuesParameters updateDumpInitValuesParametersFromPropertiesMap(Map<String, String> properties,
                                                                                           DumpInitValuesParameters dumpInitValuesParameters, Function<String, Path> pathGetter) {
        boolean useDumpInit = Optional.ofNullable(properties.get(DUMP_INIT_USE)).map(Boolean::valueOf).orElse(dumpInitValuesParameters.useDumpInit);
        Path dumpInitFolder = Optional.ofNullable(properties.get(DUMP_INIT_FOLDER)).map(pathGetter).orElse(dumpInitValuesParameters.dumpInitFolder);
        return new DumpInitValuesParameters(useDumpInit, dumpInitFolder);
    }

    public void addParametersToMap(BiConsumer<String, Object> mapAdder) {
        mapAdder.accept(DUMP_INIT_USE, useDumpInit);
        mapAdder.accept(DUMP_INIT_FOLDER, dumpInitFolder);
    }
}
