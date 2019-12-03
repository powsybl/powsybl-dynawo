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
        String id

        SolverSpec lib(String lib) {
            assert lib != null
            this.lib = lib
            return this
        }

        SolverSpec file(String file) {
            assert file != null
            this.file = file
            return this
        }

        SolverSpec id(String id) {
            assert id != null
            this.id = id
            return this
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
        String parameterId
        String dyd
        String initialState

        ModelerSpec compile(String compile) {
            assert compile != null
            this.compile = compile
            return this
        }

        ModelerSpec useStandardModelsPreCompiledModels(boolean useStandardModelsPreCompiledModels) {
            assert useStandardModelsPreCompiledModels != null
            this.useStandardModelsPreCompiledModels = useStandardModelsPreCompiledModels
            return this
        }

        ModelerSpec useStandardModelsModelicaModels(boolean useStandardModelsModelicaModels) {
            assert useStandardModelsModelicaModels != null
            this.useStandardModelsModelicaModels = useStandardModelsModelicaModels
            return this
        }

        ModelerSpec preCompiledModelsDir(String preCompiledModelsDir) {
            assert preCompiledModelsDir != null
            this.preCompiledModelsDir = preCompiledModelsDir
            return this
        }

        ModelerSpec modelicaModelsDir(String modelicaModelsDir) {
            assert modelicaModelsDir != null
            this.modelicaModelsDir = modelicaModelsDir
            return this
        }

        ModelerSpec iidm(String iidm) {
            assert iidm != null
            this.iidm = iidm
            return this
        }

        ModelerSpec parameters(String parameters) {
            assert parameters != null
            this.parameters = parameters
            return this
        }

        ModelerSpec parameterId(String parameterId) {
            assert parameterId != null
            this.parameterId = parameterId
            return this
        }

        ModelerSpec dyd(String dyd) {
            assert dyd != null
            this.dyd = dyd
            return this
        }

        ModelerSpec initialState(String initialState) {
            assert initialState != null
            this.initialState = initialState
            return this
        }
    }

    static class SimulationSpec {

        int startTime
        int stopTime
        boolean activeCriteria

        SimulationSpec startTime(int startTime) {
            assert startTime != null
            this.startTime = startTime
            return this
        }

        SimulationSpec stopTime(int stopTime) {
            assert stopTime != null
            this.stopTime = stopTime
            return this
        }

        SimulationSpec activeCriteria(boolean activeCriteria) {
            assert activeCriteria != null
            this.activeCriteria = activeCriteria
            return this
        }
    }

    static class LogAppenderSpec {

        String tag
        String file
        String lvlFilter

        LogAppenderSpec tag(String tag) {
            assert tag != null
            this.tag = tag
            return this
        }

        LogAppenderSpec file(String file) {
            assert file != null
            this.file = file
            return this
        }

        LogAppenderSpec lvlFilter(String lvlFilter) {
            assert lvlFilter != null
            this.lvlFilter = lvlFilter
            return this
        }
    }

    static class LogAppendersSpec {
    }

    static class OutputsSpec {

        String directory
        String curve
        final LogAppendersSpec appendersSpec = new LogAppendersSpec()

        OutputsSpec directory(String directory) {
            assert directory != null
            this.directory = directory
            return this
        }

        OutputsSpec curve(String curve) {
            assert curve != null
            this.curve = curve
            return this
        }

        OutputsSpec appenders(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = appendersSpec
            cloned()
            return this
        }
    }

    static class JobSpec {

        String name

        final SolverSpec solverSpec = new SolverSpec()
        final ModelerSpec modelerSpec = new ModelerSpec()
        final SimulationSpec simulationSpec = new SimulationSpec()
        final OutputsSpec outputsSpec = new OutputsSpec()

        JobSpec solver(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = solverSpec
            cloned()
            return this
        }

        JobSpec modeler(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = modelerSpec
            cloned()
            return this
        }

        JobSpec simulation(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = simulationSpec
            cloned()
            return this
        }

        JobSpec outputs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = outputsSpec
            cloned()
            return this
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
