/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.xml.DydXml;
import com.powsybl.dynawo.xml.OutputVariablesXml;
import com.powsybl.dynawo.xml.ParametersXml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynawoFilesUtils {

    private DynawoFilesUtils() {
    }

    public static void deleteExistingFile(Path basePath, String... elements) throws IOException {
        Path finalPath = basePath;
        for (String element : elements) {
            finalPath = finalPath.resolve(element);
            if (!Files.exists(finalPath)) {
                return;
            }
        }
        Files.delete(finalPath);
    }

    public static void writeInputFiles(Path workingDir, DynawoSimulationContext context) throws IOException {
        DydXml.write(workingDir, context);
        ParametersXml.write(workingDir, context);
        if (context.withCurveVariables()) {
            OutputVariablesXml.writeCurve(workingDir, context);
        }
        if (context.withFsvVariables()) {
            OutputVariablesXml.writeFsv(workingDir, context);
        }
        writeDumpFiles(workingDir, context.getDynawoSimulationParameters().getDumpFileParameters());
        writeCriteriaFile(workingDir, context.getDynawoSimulationParameters());
    }

    private static void writeDumpFiles(Path workingDir, DumpFileParameters dumpFileParameters) throws IOException {
        if (dumpFileParameters.useDumpFile()) {
            Path dumpFilePath = dumpFileParameters.getDumpFilePath();
            if (dumpFilePath != null) {
                Files.copy(dumpFilePath, workingDir.resolve(dumpFileParameters.dumpFile()), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private static void writeCriteriaFile(Path workingDir, DynawoSimulationParameters parameters) {
        parameters.getCriteriaFilePath().ifPresent(filePath -> {
            try {
                Files.copy(filePath, workingDir.resolve(filePath.getFileName()), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new PowsyblException("Simulation criteria file error", e);
            }
        });
    }
}
