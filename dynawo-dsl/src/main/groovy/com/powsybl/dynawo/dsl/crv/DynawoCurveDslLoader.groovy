/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl.crv

import java.util.function.Consumer

import org.codehaus.groovy.control.CompilationFailedException
import org.slf4j.LoggerFactory

import com.powsybl.dsl.DslException
import com.powsybl.dsl.DslLoader
import com.powsybl.dynawo.*
import com.powsybl.dynawo.crv.DynawoCurve
import com.powsybl.dynawo.dsl.DynawoDslLoaderObserver
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoCurveDslLoader extends DslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoCurveDslLoader.class)

    static class CurveSpec {

        String model
        String variable
        
        void model(String model) {
            this.model = model
        }
        
        void variable(String variable) {
            this.variable = variable
        }
    }

    DynawoCurveDslLoader(GroovyCodeSource dslSrc) {
        super(dslSrc)
    }

    DynawoCurveDslLoader(File dslFile) {
        super(dslFile)
    }

    DynawoCurveDslLoader(String script) {
        super(script)
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoCurve> consumer, DynawoDslLoaderObserver observer) {

        // curves
        binding.curve = { Closure<Void> closure ->
            def cloned = closure.clone()
            CurveSpec curveSpec = new CurveSpec()
            cloned.delegate = curveSpec
            cloned()
            // create curve
            DynawoCurve curve = new DynawoCurve(curveSpec.model, curveSpec.variable)
            consumer.accept(curve)

            LOGGER.debug("Found curve '{}'", curveSpec.model)
            observer?.curveFound(curveSpec.model)
        }
    }
}
