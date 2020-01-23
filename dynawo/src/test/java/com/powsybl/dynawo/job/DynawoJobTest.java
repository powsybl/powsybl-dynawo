/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.job;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.powsybl.dynawo.inputs.model.job.Job;
import com.powsybl.dynawo.inputs.model.job.Modeler;
import com.powsybl.dynawo.inputs.model.job.Outputs;
import com.powsybl.dynawo.inputs.model.job.Simulation;
import com.powsybl.dynawo.inputs.model.job.Solver;
import com.powsybl.dynawo.inputs.model.job.LogAppender;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoJobTest {

    @Test
    public void test() {

        Solver solver = new Solver("lib", "file", "1");
        Modeler modeler = new Modeler("compile", "iidm", "parameters", "1", "dyd");
        Simulation simulation = new Simulation(0, 1, false, 1e-6);
        LogAppender appender = new LogAppender("tag", "file", "lvlFilter");
        Outputs outputs = new Outputs("directory", "curve");
        outputs.add(appender);

        Job job = new Job("job", solver, modeler, simulation, outputs);

        assertEquals("job", job.getName());
        assertNotNull(job.getSolver());
        assertNotNull(job.getModeler());
        assertNotNull(job.getSimulation());
        assertNotNull(job.getOutputs());
        assertTrue(!job.getOutputs().getAppenders().isEmpty());

        assertEquals("lib", job.getSolver().getLib());
        assertEquals("file", job.getSolver().getParFile());
        assertEquals("1", job.getSolver().getParId());

        assertEquals("compile", job.getModeler().getCompileDir());
        assertNull(job.getModeler().getPreCompiledModelsDir());
        assertTrue(job.getModeler().isUseStandardModelsPreCompiledModels());
        assertNull(job.getModeler().getModelicaModelsDir());
        assertTrue(job.getModeler().isUseStandardModelsModelicaModels());
        assertEquals("iidm", job.getModeler().getIidm());
        assertEquals("parameters", job.getModeler().getParameters());
        assertEquals("1", job.getModeler().getParameterId());
        assertEquals("dyd", job.getModeler().getDyd());
        assertNull(job.getModeler().getInitialState());

        assertEquals(0, job.getSimulation().getStartTime());
        assertEquals(1, job.getSimulation().getStopTime());
        assertTrue(!job.getSimulation().isActiveCriteria());

        assertEquals("directory", job.getOutputs().getDirectory());
        assertEquals("curve", job.getOutputs().getCurve());
        assertTrue(job.getOutputs().isDumpLocalInitValues());
        assertTrue(job.getOutputs().isDumpGlobalInitValues());
        assertNull(job.getOutputs().getConstraints());
        assertEquals("TXT", job.getOutputs().getTimeLine());
        assertTrue(!job.getOutputs().isExportFinalState());
        assertTrue(!job.getOutputs().isExportIidmFile());
        assertTrue(!job.getOutputs().isExportDumpFile());
        assertEquals("CSV", job.getOutputs().getExportMode());

        assertEquals(1, job.getOutputs().getAppenders().size());
        assertEquals("tag", job.getOutputs().getAppenders().get(0).getTag());
        assertEquals("file", job.getOutputs().getAppenders().get(0).getFile());
        assertEquals("lvlFilter", job.getOutputs().getAppenders().get(0).getLvlFilter());
    }
}
