/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl.par

import java.util.function.Consumer

import org.slf4j.LoggerFactory

import com.powsybl.dynawo.dsl.DynawoDslLoaderObserver
import com.powsybl.dynawo.par.DynawoParameter
import com.powsybl.dynawo.par.DynawoParameterSet
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoSolverParameterSetDslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoSolverParameterSetDslLoader.class)

    static class ParameterSpec {

        String name;
        String type;
        String value;
        String origName;
        String origData;

        ParameterSpec name(String name) {
			assert name != null
            this.name = name
			return this
        }

        ParameterSpec type(String type) {
			assert type != null
            this.type = type
			return this
        }

        ParameterSpec value(String value) {
			assert value != null
            this.value = value
			return this
        }

        ParameterSpec origName(String origName) {
			assert origName != null
            this.origName = origName
			return this
        }

        ParameterSpec origData(String origData) {
			assert origData != null
            this.origData = origData
			return this
        }
    }

    static class ParametersSpec {
    }

    static class ParameterSetSpec {

        String id
        final ParametersSpec parametersSpec = new ParametersSpec()

        ParameterSetSpec parameters(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = parametersSpec
            cloned()
			return this
        }
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoParameterSet> consumer, DynawoDslLoaderObserver observer) {

        // parameterSets
        binding.solverParameterSet = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            ParameterSetSpec parameterSetSpec = new ParameterSetSpec()

            List<DynawoParameter> parameters = new ArrayList<>()
            addToSpec(parameterSetSpec.parametersSpec.metaClass, parameters, binding)

            cloned.delegate = parameterSetSpec
            cloned()

            // create parameterSet
            DynawoParameterSet parameterSet = new DynawoParameterSet(id)
            parameterSet.addParameters(parameters)
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
}
