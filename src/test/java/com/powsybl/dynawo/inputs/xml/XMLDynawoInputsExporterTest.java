/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.xml;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import javax.xml.stream.XMLStreamException;

import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.inputs.model.DynawoInputs;
import com.powsybl.dynawo.inputs.model.job.Job;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class XMLDynawoInputsExporterTest extends AbstractConverterTest {

    private final int startTime = 1;
    private final int stopTime = 100;
    private final String parametersFile = "/home/user/parametersFile";
    private final String networkParametersFile = "/home/user/networkParametersFile";
    private final String networkParametersId = "networkParametersId";
    private final SolverType solverType = SolverType.IDA;
    private final String solverParametersFile = "/home/user/solverParametersFile";
    private final String solverParametersId = "solverParametersId";

    private InMemoryPlatformConfig platformConfig;
    private DynamicSimulationParameters parameters;

    @Before
    public void setUp() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        tmpDir = Files.createDirectory(fileSystem.getPath("tmp"));

        platformConfig = new InMemoryPlatformConfig(fileSystem);
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynamic-simulation-default-parameters");
        moduleConfig.setStringProperty("startTime", Integer.toString(startTime));
        moduleConfig.setStringProperty("stopTime", Integer.toString(stopTime));

        moduleConfig = platformConfig.createModuleConfig("dynawo-default-parameters");
        moduleConfig.setStringProperty("parametersFile", parametersFile);
        moduleConfig.setStringProperty("network.parametersFile", networkParametersFile);
        moduleConfig.setStringProperty("solver.parametersFile", solverParametersFile);
        moduleConfig.setStringProperty("network.ParametersId", networkParametersId);
        moduleConfig.setStringProperty("solver.type", solverType.toString());
        moduleConfig.setStringProperty("solver.parametersId", solverParametersId);
        parameters = DynamicSimulationParameters.load(platformConfig);
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load(platformConfig);
        parameters.addExtension(DynawoSimulationParameters.class, dynawoParameters);
    }

    @Test
    public void export() throws IOException, XMLStreamException {

        DynawoSimulationParameters dynawoParameters = parameters.getExtension(DynawoSimulationParameters.class);
        DynawoInputs inputs = new DynawoInputs();
        inputs.addJob(new Job("Job test", parameters, dynawoParameters));

        XMLDynawoInputsExporter export = new XMLDynawoInputsExporter(platformConfig);
        export.export(inputs, tmpDir);
        Files.walk(tmpDir).forEach(file -> {
            if (Files.isRegularFile(file)) {
                try (InputStream is = Files.newInputStream(file)) {
                    assertNotNull(is);
                    compareXml(getClass().getResourceAsStream("/" + file.getFileName()), is);
                } catch (IOException ignored) {
                }
            }
        });
    }
}
