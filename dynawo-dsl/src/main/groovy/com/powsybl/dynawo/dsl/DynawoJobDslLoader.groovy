/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl

import java.util.function.Consumer

import org.codehaus.groovy.control.CompilationFailedException
import org.slf4j.LoggerFactory

import com.powsybl.dsl.DslException
import com.powsybl.dsl.DslLoader
import com.powsybl.dynawo.*
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoJobDslLoader extends DslLoader {

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
        String iidm
        String parameters
        int parameterId
        String dyd

        void compile(String compile) {
            this.compile = compile
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

    static class OutputsSpec {
        
        String directory
        String curve
        
        void directory(String directory) {
            this.directory = directory
        }
        
        void curve(String curve) {
            this.curve = curve
        }
    }

    static class JobSpec {

        String name

        final SolverSpec solverSpec = new SolverSpec()
        final ModelerSpec modelerSpec = new ModelerSpec()
        final SimulationSpec simulationSpec = new SimulationSpec()
        final OutputsSpec outputsSpec = new OutputsSpec()

        void name(String name) {
            this.name = name
        }

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

    DynawoJobDslLoader(GroovyCodeSource dslSrc) {
        super(dslSrc)
    }

    DynawoJobDslLoader(File dslFile) {
        super(dslFile)
    }

    DynawoJobDslLoader(String script) {
        super(script)
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoJob> consumer, DynawoDslLoaderObserver observer) {

        // set base network
        binding.setVariable("network", network)

        // jobs
        binding.job = { String name, Closure<Void> closure ->
            def cloned = closure.clone()
            JobSpec jobSpec = new JobSpec()
            cloned.delegate = jobSpec
            cloned()

            // create solver
            DynawoSolver solver = new DynawoSolver(jobSpec.solverSpec.lib, jobSpec.solverSpec.file, jobSpec.solverSpec.id)
            // create modeler
            DynawoModeler modeler = new DynawoModeler(jobSpec.modelerSpec.compile, jobSpec.modelerSpec.iidm, jobSpec.modelerSpec.parameters, jobSpec.modelerSpec.parameterId, , jobSpec.modelerSpec.dyd)
            // create simulation
            DynawoSimulation simulation = new DynawoSimulation(jobSpec.simulationSpec.startTime, jobSpec.simulationSpec.stopTime, jobSpec.simulationSpec.activeCriteria)
            // create outputs
            DynawoOutputs outputs = new DynawoOutputs(jobSpec.outputsSpec.directory, jobSpec.outputsSpec.curve)
            // create job
            DynawoJob job = new DynawoJob(name, solver, modeler, simulation, outputs)
            consumer.accept(job)

            LOGGER.debug("Found job '{}'", name)
            observer?.jobFound(name)
        }
    }

    List<DynawoJob> load(Network network) {
        load(network, null)
    }

    List<DynawoJob> load(Network network, DynawoDslLoaderObserver observer) {

        List<DynawoJob> jobs = new ArrayList<>()
        
        try {
            observer?.begin(dslSrc.getName())

            Binding binding = new Binding()

            loadDsl(binding, network, jobs.&add, observer)

            // set base network
            binding.setVariable("network", network)

            def shell = createShell(binding)

            shell.evaluate(dslSrc)

            observer?.end()

            jobs

        } catch (CompilationFailedException e) {
            throw new DslException(e.getMessage(), e)
        }
    }

}
