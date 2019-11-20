/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl.job

import java.util.function.Consumer

import org.slf4j.LoggerFactory

import com.powsybl.dynawo.dsl.DynawoDslLoaderObserver
import com.powsybl.dynawo.job.DynawoJob
import com.powsybl.dynawo.job.DynawoModeler
import com.powsybl.dynawo.job.DynawoOutputs
import com.powsybl.dynawo.job.DynawoSimulation
import com.powsybl.dynawo.job.DynawoSolver
import com.powsybl.dynawo.job.LogAppender
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoJobDslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoJobDslLoader.class)

    static class SolverSpec {

        String lib
        String file
        int id

        void lib(String lib) {
            this.lib = lib
        }

        void file(String file) {
            this.file = file
        }

        void id(int id) {
            this.id = id
        }
    }

    static class ModelerSpec {

        String compile
        String preCompiledModelsDir
        boolean useStandardModelsPreCompiledModels
        String modelicaModelsDir
        boolean useStandardModelsModelicaModels
        String iidm
        String parameters
        int parameterId
        String dyd
        String initialState

        void compile(String compile) {
            this.compile = compile
        }

        void useStandardModelsPreCompiledModels(boolean useStandardModelsPreCompiledModels) {
            this.useStandardModelsPreCompiledModels = useStandardModelsPreCompiledModels
        }

        void useStandardModelsModelicaModels(boolean useStandardModelsModelicaModels) {
            this.useStandardModelsModelicaModels = useStandardModelsModelicaModels
        }

        void preCompiledModelsDir(String preCompiledModelsDir) {
            this.preCompiledModelsDir = preCompiledModelsDir
        }

        void modelicaModelsDir(String modelicaModelsDir) {
            this.modelicaModelsDir = modelicaModelsDir
        }

        void iidm(String iidm) {
            this.iidm = iidm
        }

        void parameters(String parameters) {
            this.parameters = parameters
        }

        void parameterId(int parameterId) {
            this.parameterId = parameterId
        }

        void dyd(String dyd) {
            this.dyd = dyd
        }

        void initialState(String initialState) {
            this.initialState = initialState
        }
    }

    static class SimulationSpec {

        int startTime
        int stopTime
        boolean activeCriteria

        void startTime(int startTime) {
            this.startTime = startTime
        }

        void stopTime(int stopTime) {
            this.stopTime = stopTime
        }

        void activeCriteria(boolean activeCriteria) {
            this.activeCriteria = activeCriteria
        }
    }
    
    static class LogAppenderSpec {
        
        String tag
        String file
        String lvlFilter
        
        void tag(String tag) {
            this.tag = tag
        }
        
        void file(String file) {
            this.file = file
        }
        
        void lvlFilter(String lvlFilter) {
            this.lvlFilter = lvlFilter
        }
    }

    static class LogAppendersSpec {
        
    }

    static class OutputsSpec {

        String directory
        String curve
        final LogAppendersSpec appendersSpec = new LogAppendersSpec()

        void directory(String directory) {
            this.directory = directory
        }

        void curve(String curve) {
            this.curve = curve
        }

        void appenders(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = appendersSpec
            cloned()
        }
    }

    static class JobSpec {

        String name

        final SolverSpec solverSpec = new SolverSpec()
        final ModelerSpec modelerSpec = new ModelerSpec()
        final SimulationSpec simulationSpec = new SimulationSpec()
        final OutputsSpec outputsSpec = new OutputsSpec()

        void solver(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = solverSpec
            cloned()
        }

        void modeler(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = modelerSpec
            cloned()
        }

        void simulation(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = simulationSpec
            cloned()
        }

        void outputs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = outputsSpec
            cloned()
        }
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoJob> consumer, DynawoDslLoaderObserver observer) {

        // jobs
        binding.job = { String name, Closure<Void> closure ->
            def cloned = closure.clone()
            JobSpec jobSpec = new JobSpec()

            List<LogAppender> appenders = new ArrayList<>()
            addAppenders(jobSpec.outputsSpec.appendersSpec.metaClass, appenders, binding)

            cloned.delegate = jobSpec
            cloned()

            // create solver
            DynawoSolver solver = new DynawoSolver(jobSpec.solverSpec.lib, jobSpec.solverSpec.file, jobSpec.solverSpec.id)
            // create modeler
            DynawoModeler modeler = new DynawoModeler(jobSpec.modelerSpec.compile, jobSpec.modelerSpec.preCompiledModelsDir, jobSpec.modelerSpec.useStandardModelsPreCompiledModels, jobSpec.modelerSpec.modelicaModelsDir, jobSpec.modelerSpec.useStandardModelsModelicaModels, jobSpec.modelerSpec.iidm, jobSpec.modelerSpec.parameters, jobSpec.modelerSpec.parameterId, , jobSpec.modelerSpec.dyd, jobSpec.modelerSpec.initialState)
            // create simulation
            DynawoSimulation simulation = new DynawoSimulation(jobSpec.simulationSpec.startTime, jobSpec.simulationSpec.stopTime, jobSpec.simulationSpec.activeCriteria)
            // create outputs
            DynawoOutputs outputs = new DynawoOutputs(jobSpec.outputsSpec.directory, jobSpec.outputsSpec.curve)
            outputs.addAppenders(appenders)
            // create job
            DynawoJob job = new DynawoJob(name, solver, modeler, simulation, outputs)
            consumer.accept(job)

            LOGGER.debug("Found job '{}'", name)
            observer?.jobFound(name)
        }
    }

    static void addAppenders(MetaClass appendersSpecMetaClass, List<LogAppender> appenders, Binding binding) {

        appendersSpecMetaClass.appender = { Closure<Void> closure ->
            def cloned = closure.clone()
            LogAppenderSpec logAppenderSpec = new LogAppenderSpec()
            cloned.delegate = logAppenderSpec
            cloned()
            LogAppender appender = new LogAppender(logAppenderSpec.tag, logAppenderSpec.file, logAppenderSpec.lvlFilter)
            appenders.add(appender)
        }
    }
}
