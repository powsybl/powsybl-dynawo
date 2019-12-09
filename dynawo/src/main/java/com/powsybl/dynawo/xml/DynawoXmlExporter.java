/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.stream.XMLStreamException;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.FileDataSource;
import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoXmlExporter {

    private static final String DEFAULT_DYNAWO_CASE_NAME = "nrt/data/IEEE14/IEEE14_BasicTestCases/IEEE14_DisconnectLine/IEEE14.jobs";

    public DynawoXmlExporter() {
        this(PlatformConfig.defaultConfig());
    }

    public DynawoXmlExporter(PlatformConfig platformConfig) {
        this.platformConfig = platformConfig;
    }

    public String export(Network network, DynawoInputProvider dynawoProvider, Path workingDir) throws IOException, XMLStreamException {
        String dynawoJobsFile = DEFAULT_DYNAWO_CASE_NAME;
        DynawoInputs.prepare(network, dynawoProvider, workingDir);
        if (network != null) {
            Path jobsFile = workingDir.resolve("dynawoModel.jobs");
            XMLExporter xmlExporter = new XMLExporter(platformConfig);
            xmlExporter.export(network, null, new FileDataSource(workingDir, "dynawoModel"));
            // Warning: dynawo expects the country field in each substation element
            dynawoJobsFile = jobsFile.toAbsolutePath().toString();
        }
        return dynawoJobsFile;
    }

    private final PlatformConfig platformConfig;

}
