/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.xml;

import static com.powsybl.dynawo.inputs.xml.DynawoConstants.JOBS_FILENAME;
import static com.powsybl.dynawo.inputs.xml.DynawoConstants.PAR_FILENAME;
import static com.powsybl.dynawo.inputs.xml.DynawoConstants.PAR_SIM_FILENAME;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;

import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.commons.datasource.FileDataSource;
import com.powsybl.dynawo.inputs.model.DynawoInputs;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.XMLExporter;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoInputsXmlExporter {

    public DynawoInputsXmlExporter() {
        this(PlatformConfig.defaultConfig());
    }

    public DynawoInputsXmlExporter(PlatformConfig platformConfig) {
        this.platformConfig = Objects.requireNonNull(platformConfig);
    }

    public Path export(DynawoInputs inputs, Path workingDir) throws IOException, XMLStreamException {
        exportNetwork(inputs.getNetwork(), workingDir);
        exportDynawoFiles(inputs, workingDir);
        return workingDir.resolve(JOBS_FILENAME);
    }

    private void exportNetwork(Network network, Path workingDir) {
        // It is possible and valid that we do not have a network
        // when all the information required for the dynamic simulation
        // is provided by the dyd data
        if (network != null) {
            XMLExporter xmlExporter = new XMLExporter(platformConfig);
            xmlExporter.export(network, null, new FileDataSource(workingDir, "powsybl_network"));
            // Warning: dynawo expects the country field in each substation element
        }
    }

    private static void exportDynawoFiles(DynawoInputs inputs, Path workingDir) throws IOException, XMLStreamException {
        new JobsXml().write(workingDir, inputs.getJobs());
        new DynamicModelsXml().write(workingDir, inputs.getDynamicModels());
        new ParameterSetsXml(PAR_FILENAME).write(workingDir, inputs.getParameterSets());
        new ParameterSetsXml(PAR_SIM_FILENAME).write(workingDir, inputs.getSolverParameterSets());
        new CurvesXml().write(workingDir, inputs.getCurves());
    }

    private final PlatformConfig platformConfig;

}
