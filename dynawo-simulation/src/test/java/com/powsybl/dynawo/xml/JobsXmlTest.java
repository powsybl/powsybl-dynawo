/**
 * Copyright (c) 2020-2025,
 * RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 *
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class JobsXmlTest extends DynawoTestUtil {

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideParameters")
    void testJobXml(String xmlResult, DynawoSimulationParameters parameters) throws IOException, SAXException {
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .dynawoParameters(parameters)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();
        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", xmlResult, tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME));

    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of("jobs.xml", DynawoSimulationParameters.load()),
                Arguments.of("jobsWithSpecificLogs.xml",
                        DynawoSimulationParameters.load().setSpecificLogs(EnumSet.allOf(DynawoSimulationParameters.SpecificLog.class))),
                Arguments.of("jobsWithCriteria.xml",
                        DynawoSimulationParameters.load().setCriteriaFilePath(Path.of("criteria.crt")))
        );
    }

    @Test
    void testJobWithDump() throws IOException, SAXException {
        Files.createFile(fileSystem.getPath("tmp", "dump.dmp"));
        DynawoSimulationParameters parameters = DynawoSimulationParameters.load()
                .setDumpFileParameters(DumpFileParameters.createImportExportDumpFileParameters(fileSystem.getPath("tmp"), "dump.dmp"));
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .dynawoParameters(parameters)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();
        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", "jobsWithDump.xml", tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME));
    }

    @Test
    void testAdditionalDydJobXml() throws IOException, SAXException {
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();
        JobsXml.write(tmpDir, context, "additional_models.dyd");
        validate("jobs.xsd", "jobsWithAdditionalDyd.xml", tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME));
    }

    @Test
    void writeFinalStepJob() throws SAXException, IOException {
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .finalStepConfig(new FinalStepConfig(200, bbm -> bbm.getDynamicModelId().equalsIgnoreCase("LOAD2")))
                .build();
        JobsXml.writeFinalStep(tmpDir, context);
        validate("jobs.xsd", "jobsWithFinalStep.xml", tmpDir.resolve(DynawoSimulationConstants.FINAL_STEP_JOBS_FILENAME));
    }
}
