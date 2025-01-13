/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.*;
import com.powsybl.dynawo.commons.DynawoConstants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.xml.sax.SAXException;

import java.io.IOException;
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
        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, outputVariables, DynamicSimulationParameters.load(), parameters);
        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", xmlResult, tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME));
    }

    private static Stream<Arguments> provideParameters() {
        return Stream.of(
                Arguments.of("jobs.xml", DynawoSimulationParameters.load()),
                Arguments.of("jobsWithDump.xml", DynawoSimulationParameters.load()
                        .setDumpFileParameters(DumpFileParameters.createImportExportDumpFileParameters(Path.of("/dumpFiles"), "dump.dmp"))),
                Arguments.of("jobsWithSpecificLogs.xml", DynawoSimulationParameters.load()
                        .setSpecificLogs(EnumSet.allOf(DynawoSimulationParameters.SpecificLog.class))),
                Arguments.of("jobsWithCriteria.xml", DynawoSimulationParameters.load()
                        .setCriteriaFilePath(Path.of("criteria.crt")))
        );
    }

    @Test
    void writeJobWithPhase2() throws SAXException, IOException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load();
        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(),
                dynamicModels, eventModels, outputVariables, parameters, dynawoParameters,
                new Phase2Config(200, bbm -> bbm.getDynamicModelId().equalsIgnoreCase("BBM_LOAD2")),
                DynawoConstants.VERSION_MIN, ReportNode.NO_OP);

        JobsXml.writePhase2(tmpDir, context);
        validate("jobs.xsd", "jobsWithPhase2.xml", tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME));
    }
}
