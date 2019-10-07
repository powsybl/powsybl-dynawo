/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl

import org.codehaus.groovy.control.CompilationFailedException
import org.slf4j.LoggerFactory

import com.powsybl.dsl.DslLoader
import com.powsybl.dynawo.crv.DynawoCurve
import com.powsybl.dynawo.dsl.crv.DynawoCurveDslLoader
import com.powsybl.dynawo.dsl.dyd.DynawoDynamicModelDslLoader
import com.powsybl.dynawo.dsl.job.DynawoJobDslLoader
import com.powsybl.dynawo.dsl.par.DynawoParameterSetDslLoader
import com.powsybl.dynawo.dsl.par.DynawoSolverParameterSetDslLoader
import com.powsybl.dynawo.dyd.DynawoDynamicModel
import com.powsybl.dynawo.job.DynawoJob
import com.powsybl.dynawo.par.DynawoParameterSet
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoDslLoader extends DslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoDslLoader.class)

    static class SolverSpec {
        
        String lib
        String name
        int id 
    }

    static class ModelerSpec {
        
        String compile
        String iidm
        String parameters
        int parameterId
        String dyd
    }

    static class SimulationSpec {
        
        int startTime
        int stopTime
        boolean activeCriteria
    }

    static class OutputsSpec {
        
        String directory
        String curve
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

    DynawoDslLoader(GroovyCodeSource dslSrc) {
        super(dslSrc)
    }

    DynawoDslLoader(File dslFile) {
        super(dslFile)
    }

    DynawoDslLoader(String script) {
        super(script)
    }

    static void loadDsl(Binding binding, Network network, DynawoDslHandler handler, DynawoDslLoaderObserver observer) {

        // set base network
        binding.setVariable("network", network)

        // jobs
        DynawoJobDslLoader.loadDsl(binding, network, {j -> handler.addJob(j)}, observer)
        // curves
        DynawoCurveDslLoader.loadDsl(binding, network, {c -> handler.addCurve(c)}, observer)
        // dynamicModels
        DynawoDynamicModelDslLoader.loadDsl(binding, network, {d -> handler.addDynamicModel(d)}, observer)
        // parameterSets
        DynawoParameterSetDslLoader.loadDsl(binding, network, {p -> handler.addParameterSet(p)}, observer)
        // solverParameterSets
        DynawoSolverParameterSetDslLoader.loadDsl(binding, network, {s -> handler.addSolverParameterSet(s)}, observer)
    }

    DynawoDb load(Network network) {
        load(network, null)
    }

    void load(Network network, DynawoDslHandler handler, DynawoDslLoaderObserver observer) {

        LOGGER.debug("Loading DSL '{}'", dslSrc.getName())
        observer?.begin(dslSrc.getName())

        Binding binding = new Binding()

        loadDsl(binding, network, handler, observer)
        try {

            def shell = createShell(binding)

            shell.evaluate(dslSrc)

            observer?.end()
        } catch (CompilationFailedException e) {
            throw new DynawoDslException(e.getMessage(), e)
        }
    }

    DynawoDb load(Network network, DynawoDslLoaderObserver observer) {
        DynawoDb dynawoDb = new DynawoDb()

        //Handler to create an ActionDb instance
        DynawoDslHandler dynawoDbBuilder = new DynawoDslHandler() {

                    @Override
                    void addJob(DynawoJob job) {
                        dynawoDb.addJob(job)
                    }

                    @Override
                    void addCurve(DynawoCurve curve) {
                        dynawoDb.addCurve(curve)
                    }

                    @Override
                    void addDynamicModel(DynawoDynamicModel dynamicModel) {
                        dynawoDb.addDynamicModel(dynamicModel)
                    }

                    @Override
                    void addParameterSet(DynawoParameterSet parameterSet) {
                        dynawoDb.addParameterSet(parameterSet)
                    }

                    @Override
                    void addSolverParameterSet(DynawoParameterSet solverParameterSet) {
                        dynawoDb.addSolverParameterSet(solverParameterSet)
                    }
                }

        load(network, dynawoDbBuilder, observer)

        dynawoDb
    }
}
