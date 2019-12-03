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

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoJobTest {

    @Test
    public void test() {

        DynawoSolver solver = new DynawoSolver("lib", "file", 1);
        DynawoModeler modeler = new DynawoModeler("compile", "iidm", "parameters", 1, "dyd");
        DynawoSimulation simulation = new DynawoSimulation(0, 1, false);
        LogAppender appender = new LogAppender("tag", "file", "lvlFilter");
        DynawoOutputs outputs = new DynawoOutputs("directory", "curve");
        outputs.add(appender);

        DynawoJob job = new DynawoJob("job", solver, modeler, simulation, outputs);

        assertEquals("job", job.getName());
        assertNotNull(job.getSolver());
        assertNotNull(job.getModeler());
        assertNotNull(job.getSimulation());
        assertNotNull(job.getOutputs());
        assertTrue(!job.getOutputs().getAppenders().isEmpty());

        assertEquals("lib", job.getSolver().getLib());
        assertEquals("file", job.getSolver().getFile());
        assertEquals(1, job.getSolver().getId());

        assertEquals("compile", job.getModeler().getCompileDir());
        assertNull(job.getModeler().getPreCompiledModelsDir());
        assertTrue(job.getModeler().isUseStandardModelsPreCompiledModels());
        assertNull(job.getModeler().getModelicaModelsDir());
        assertTrue(job.getModeler().isUseStandardModelsModelicaModels());
        assertEquals("iidm", job.getModeler().getIidm());
        assertEquals("parameters", job.getModeler().getParameters());
        assertEquals(1, job.getModeler().getParameterId());
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
