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
import com.powsybl.dynawo.dsl.DynawoParameterSetDslLoader.ParameterSpec
import com.powsybl.dynawo.dsl.DynawoParameterSetDslLoader.ParametersSpec
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoSolverParameterSetDslLoader extends DslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoSolverParameterSetDslLoader.class)

    static class ParameterSpec {

        String name;
        String type;
        String value;
        String origName;
        String origData;

        void name(String name) {
            this.name = name
        }

        void type(String type) {
            this.type = type
        }

        void value(String value) {
            this.value = value
        }

        void origName(String origName) {
            this.origName = origName
        }

        void origData(String origData) {
            this.origData = origData
        }

        boolean isReference() {
            return origName != null && origName.length() > 0
        }
    }

    static class ParametersSpec {
    }

    static class ParameterSetSpec {

        int id
        final ParametersSpec parametersSpec = new ParametersSpec()

        void id(int id) {
            this.id = id
        }

        void parameters(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parametersSpec
            cloned()
        }
    }

    DynawoSolverParameterSetDslLoader(GroovyCodeSource dslSrc) {
        super(dslSrc)
    }

    DynawoSolverParameterSetDslLoader(File dslFile) {
        super(dslFile)
    }

    DynawoSolverParameterSetDslLoader(String script) {
        super(script)
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoParameterSet> consumer, DynawoDslLoaderObserver observer) {

        // set base network
        binding.setVariable("network", network)

        // parameterSets
        binding.solverParameterSet = { int id, Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterSetSpec parameterSetSpec = new ParameterSetSpec()

            List<DynawoParameter> parameters = new ArrayList<>()
            addToSpec(parameterSetSpec.parametersSpec.metaClass, parameters, binding)

            cloned.delegate = parameterSetSpec
            cloned()

            // create parameterSet
            DynawoParameterSet parameterSet = new DynawoParameterSet(id, parameters)
            consumer.accept(parameterSet)

            LOGGER.debug("Found solverParameterSet '{}'", id)
            observer?.solverParameterSetFound(id)
        }
    }

    static void addToSpec(MetaClass parametersSpecMetaClass, List<DynawoParameter> parameters, Binding binding) {

        parametersSpecMetaClass.parameter = { Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterSpec parameterSpec = new ParameterSpec()
            cloned.delegate = parameterSpec
            cloned()
            DynawoParameter parameter = new DynawoParameter(parameterSpec.name, parameterSpec.type, parameterSpec.value)
            parameters.add(parameter)

        }
    }

    List<DynawoParameterSet> load(Network network) {
        load(network, null)
    }

    List<DynawoParameterSet> load(Network network, DynawoDslLoaderObserver observer) {

        List<DynawoParameterSet> parameterSets = new ArrayList<>()

        try {
            observer?.begin(dslSrc.getName())

            Binding binding = new Binding()

            loadDsl(binding, network, parameterSets.&add, observer)

            // set base network
            binding.setVariable("network", network)

            def shell = createShell(binding)

            shell.evaluate(dslSrc)

            observer?.end()

            parameterSets

        } catch (CompilationFailedException e) {
            throw new DslException(e.getMessage(), e)
        }
    }

}
