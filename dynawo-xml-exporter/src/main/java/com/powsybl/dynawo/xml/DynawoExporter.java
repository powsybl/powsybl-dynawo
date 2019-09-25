/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.nio.file.Path;
import java.util.Properties;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.FileDataSource;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoExporter {

    private static final String DEFAULT_DYNAWO_CASE_NAME = "nrt/data/IEEE14/IEEE14_BasicTestCases/IEEE14_DisconnectLine/IEEE14.jobs";

    public DynawoExporter(Network network, DynawoProvider dynawoProvider) {
        this.network = network;
        this.dynawoProvider = dynawoProvider;
    }

    public String export(Path workingDir, PlatformConfig platformConfig) {
        String dynawoJobsFile = DEFAULT_DYNAWO_CASE_NAME;
        new DynawoJobs(network, dynawoProvider).prepareFile(workingDir);
        new DynawoDynamicModels(network, dynawoProvider).prepareFile(workingDir);
        new DynawoSimulationParameters(network, dynawoProvider).prepareFile(workingDir);
        new DynawoSolverParameters(network, dynawoProvider).prepareFile(workingDir);
        new DynawoCurves(network, dynawoProvider).prepareFile(workingDir);
        if (network != null) {
            Path jobsFile = workingDir.resolve("dynawoModel.jobs");
            XMLExporter xmlExporter = new XMLExporter(platformConfig);
            Properties properties = new Properties();
            properties.put(XMLExporter.ANONYMISED, "false");
            xmlExporter.export(network, properties, new FileDataSource(workingDir, "dynawoModel"));
            dynawoJobsFile = jobsFile.toAbsolutePath().toString();
        }
        return dynawoJobsFile;
    }

    private final Network network;
    private final DynawoProvider dynawoProvider;
}
