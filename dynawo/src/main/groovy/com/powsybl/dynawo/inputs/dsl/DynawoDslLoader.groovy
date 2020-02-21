/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.dsl

import org.codehaus.groovy.control.CompilationFailedException
import org.slf4j.LoggerFactory

import com.powsybl.dsl.DslLoader
import com.powsybl.dynawo.inputs.dsl.crv.DynawoCurveDslLoader
import com.powsybl.dynawo.inputs.dsl.dyd.DynawoDynamicModelDslLoader
import com.powsybl.dynawo.inputs.dsl.job.DynawoJobDslLoader
import com.powsybl.dynawo.inputs.dsl.par.DynawoParameterSetDslLoader
import com.powsybl.dynawo.inputs.dsl.par.DynawoSolverParameterSetDslLoader
import com.powsybl.dynawo.inputs.model.DynawoInputs
import com.powsybl.dynawo.inputs.model.crv.Curve
import com.powsybl.dynawo.inputs.model.dyd.DynawoDynamicModel
import com.powsybl.dynawo.inputs.model.job.Job
import com.powsybl.dynawo.inputs.model.par.ParameterSet
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoDslLoader extends DslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoDslLoader.class)

    DynawoDslLoader(GroovyCodeSource dslSrc) {
        super(dslSrc)
    }

    DynawoDslLoader(File dslFile) {
        super(dslFile)
    }

    DynawoDslLoader(String script) {
        super(script)
    }

    static void loadDsl(Binding binding, Network network, DynawoInputs dynawoInputs, DynawoDslLoaderObserver observer) {

        // set base network
        binding.setVariable("network", network)

        // jobs
        DynawoJobDslLoader.loadDsl(binding, network, {j -> dynawoInputs.addJob(j)}, observer)
        // curves
        DynawoCurveDslLoader.loadDsl(binding, network, {c -> dynawoInputs.addCurve(c)}, observer)
        // dynamicModels
        DynawoDynamicModelDslLoader.loadDsl(binding, network, {d -> dynawoInputs.addDynamicModel(d)}, observer)
        // parameterSets
        DynawoParameterSetDslLoader.loadDsl(binding, network, {p -> dynawoInputs.addParameterSet(p)}, observer)
        // solverParameterSets
        DynawoSolverParameterSetDslLoader.loadDsl(binding, network, {s -> dynawoInputs.addSolverParameterSet(s)}, observer)
    }

    DynawoInputs load(Network network) {
        load(network, null)
    }

    void load(Network network, DynawoInputs dynawoInputs, DynawoDslLoaderObserver observer) {

        LOGGER.debug("Loading DSL '{}'", dslSrc.getName())
        observer?.begin(dslSrc.getName())

        Binding binding = new Binding()

        loadDsl(binding, network, dynawoInputs, observer)
        try {

            def shell = createShell(binding)

            shell.evaluate(dslSrc)

            observer?.end()
        } catch (CompilationFailedException e) {
            throw new DynawoDslException(e.getMessage(), e)
        }
    }

    DynawoInputs load(Network network, DynawoDslLoaderObserver observer) {
        DynawoInputs dynawoInputs = new DynawoInputs(network)

        load(network, dynawoInputs, observer)

        dynawoInputs
    }
}
