/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DumpFileParameters;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumSet;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class JobsXmlTest extends DynawoTestUtil {

    @Test
    void writeJob() throws SAXException, IOException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load();
        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, parameters, dynawoParameters);

        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", "jobs.xml", tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME));
    }

    @Test
    void writeJobWithDumpFile() throws SAXException, IOException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load()
                .setDumpFileParameters(DumpFileParameters.createImportExportDumpFileParameters(Path.of("/dumpFiles"), "dump.dmp"));
        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, parameters, dynawoParameters);

        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", "jobsWithDump.xml", tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME));
    }

    @Test
    void writeJobWithSpecificLogs() throws SAXException, IOException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load()
                .setSpecificLogs(EnumSet.allOf(DynawoSimulationParameters.SpecificLog.class));
        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, parameters, dynawoParameters);

        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", "jobsWithSpecificLogs.xml", tmpDir.resolve(DynawoSimulationConstants.JOBS_FILENAME));
    }

}
