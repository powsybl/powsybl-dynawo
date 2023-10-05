/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DumpFileParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class JobsXmlTest extends DynaWaltzTestUtil {

    @Test
    void writeJob() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, parameters, dynawoParameters);

        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", "jobs.xml", tmpDir.resolve(DynaWaltzConstants.JOBS_FILENAME));
    }

    @Test
    void writeJobWithDumpFile() throws SAXException, IOException, XMLStreamException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load()
                .setDumpFileParameters(new DumpFileParameters(true, true, Path.of("/dumpFiles"), "dump.dmp"));
        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, parameters, dynawoParameters);

        JobsXml.write(tmpDir, context);
        validate("jobs.xsd", "jobsWithDump.xml", tmpDir.resolve(DynaWaltzConstants.JOBS_FILENAME));
    }

}
