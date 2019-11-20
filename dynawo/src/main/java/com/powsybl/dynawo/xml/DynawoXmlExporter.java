/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.FileDataSource;
import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.dynawo.dsl.GroovyDslDynawoInputProvider;
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
        this.defaultsLoadProvider = new GroovyDslDynawoInputProvider(getClass().getResourceAsStream("/defaultsLoad.groovy"));
        this.defaultsGeneratorProvider = new GroovyDslDynawoInputProvider(getClass().getResourceAsStream("/defaultsGenerator.groovy"));
        this.defaultsOmegaRefProvider = new GroovyDslDynawoInputProvider(getClass().getResourceAsStream("/defaultsOmegaRef.groovy"));
    }

    public String export(Network network, DynawoInputProvider dynawoProvider, Path workingDir) {
        String dynawoJobsFile = DEFAULT_DYNAWO_CASE_NAME;
        try {
            DynawoInputs.prepare(network, dynawoProvider, defaultsOmegaRefProvider, defaultsLoadProvider, defaultsGeneratorProvider, workingDir);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        if (network != null) {
            Path jobsFile = workingDir.resolve("dynawoModel.jobs");
            XMLExporter xmlExporter = new XMLExporter(platformConfig);
            Properties properties = new Properties();
            properties.put(XMLExporter.ANONYMISED, "false");
            xmlExporter.export(network, properties, new FileDataSource(workingDir, "dynawoModel"));
            // Error in dynawo because substation is exported without country field
            dynawoJobsFile = jobsFile.toAbsolutePath().toString();
        }
        return dynawoJobsFile;
    }

    private final PlatformConfig platformConfig;
    private DynawoInputProvider defaultsLoadProvider;
    private DynawoInputProvider defaultsGeneratorProvider;
    private DynawoInputProvider defaultsOmegaRefProvider;
    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoXmlExporter.class);

}
