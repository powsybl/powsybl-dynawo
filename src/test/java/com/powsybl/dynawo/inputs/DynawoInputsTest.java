/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
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
public class DynawoInputsTest {

    private final int startTime = 1;
    private final int stopTime = 100;
    private final String parametersFile = "/home/user/parametersFile";
    private final String networkParametersFile = "/home/user/networkParametersFile";
    private final String networkParametersId = "networkParametersId";
    private final SolverType solverType = SolverType.IDA;
    private final String solverParametersFile = "/home/user/solverParametersFile";
    private final String solverParametersId = "solverParametersId";

    private InMemoryPlatformConfig platformConfig;
    private FileSystem fileSystem;
    private DynamicSimulationParameters parameters;

    @Before
    public void setUp() {

        fileSystem = Jimfs.newFileSystem(Configuration.unix());
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

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void jobs() {

        DynawoSimulationParameters dynawoParameters = parameters.getExtension(DynawoSimulationParameters.class);
        DynawoInputs inputs = new DynawoInputs();
        inputs.addJob(new Job("Job test", parameters, dynawoParameters));

        assertFalse(inputs.getJobs().isEmpty());
        assertEquals(1, inputs.getJobs().size());

        Job job = inputs.getJobs().get(0);
        assertEquals("Job test", job.getName());
        assertNotNull(job.getSolver());
        assertNotNull(job.getModeler());
        assertNotNull(job.getSimulation());
        assertNotNull(job.getOutputs());
        assertFalse(job.getOutputs().getAppenders().isEmpty());

        assertEquals("lib", job.getSolver().getLib());
        assertEquals(solverParametersFile, job.getSolver().getParFile());
        assertEquals(solverParametersId, job.getSolver().getParId());

        assertEquals("outputs/compilation", job.getModeler().getCompileDir());
        assertEquals("iidm", job.getModeler().getIidm());
        assertEquals(networkParametersFile, job.getModeler().getParameters());
        assertEquals(networkParametersId, job.getModeler().getParameterId());
        assertEquals("dyd", job.getModeler().getDyd());

        assertEquals(startTime, job.getSimulation().getStartTime());
        assertEquals(stopTime, job.getSimulation().getStopTime());

        assertEquals("outputs", job.getOutputs().getDirectory());
        assertEquals("curve", job.getOutputs().getCurve());
        assertEquals("TXT", job.getOutputs().getTimeLine());
        assertTrue(job.getOutputs().isExportIidmFile());
        assertFalse(job.getOutputs().isExportDumpFile());
        assertEquals("CSV", job.getOutputs().getExportMode());

        assertEquals(1, job.getOutputs().getAppenders().size());
        assertEquals("", job.getOutputs().getAppenders().get(0).getTag());
        assertEquals("dynawo.log", job.getOutputs().getAppenders().get(0).getFile());
        assertEquals("DEBUG", job.getOutputs().getAppenders().get(0).getLvlFilter());
    }
}
