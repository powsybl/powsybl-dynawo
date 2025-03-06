/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DumpFileParameters;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class JobsXmlTest extends DynawoTestUtil {

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideParameters")
    void testJobXml(String xmlResult, Function<FileSystem, DynawoSimulationParameters> parametersSupplier) throws IOException, SAXException {
        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(),
                dynamicModels, eventModels, outputVariables, DynamicSimulationParameters.load(), parametersSupplier.apply(fileSystem));
        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", xmlResult, tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME));
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of("jobs.xml",
                        (Function<FileSystem, DynawoSimulationParameters>) fs ->
                                DynawoSimulationParameters.load()),
                Arguments.of("jobsWithDump.xml",
                        (Function<FileSystem, DynawoSimulationParameters>) fs -> {
                            try {
                                Files.createFile(fs.getPath("tmp", "dump.dmp"));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return DynawoSimulationParameters.load()
                                        .setDumpFileParameters(DumpFileParameters.createImportExportDumpFileParameters(fs.getPath("tmp"), "dump.dmp"));
                        }),
                Arguments.of("jobsWithSpecificLogs.xml",
                        (Function<FileSystem, DynawoSimulationParameters>) fs ->
                                DynawoSimulationParameters.load().setSpecificLogs(EnumSet.allOf(DynawoSimulationParameters.SpecificLog.class))),
                Arguments.of("jobsWithCriteria.xml",
                        (Function<FileSystem, DynawoSimulationParameters>) fs ->
                                DynawoSimulationParameters.load().setCriteriaFilePath(Path.of("criteria.crt")))
        );
    }
}
