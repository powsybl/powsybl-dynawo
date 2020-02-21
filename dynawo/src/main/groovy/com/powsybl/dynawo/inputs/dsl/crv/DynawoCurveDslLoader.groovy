/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.dsl.crv

import java.util.function.Consumer

import org.slf4j.LoggerFactory

import com.powsybl.dynawo.inputs.dsl.DynawoDslLoaderObserver
import com.powsybl.dynawo.inputs.model.crv.Curve
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoCurveDslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoCurveDslLoader.class)

    static class CurveSpec {

        String model
        String variable

        CurveSpec model(String model) {
            assert model != null
            this.model = model
            return this
        }

        CurveSpec variable(String variable) {
            assert variable != null
            this.variable = variable
            return this
        }
    }

    static void loadDsl(Binding binding, Network network, Consumer<Curve> consumer, DynawoDslLoaderObserver observer) {

        // curves
        binding.curve = { Closure<Void> closure ->
            def cloned = closure.clone()
            CurveSpec curveSpec = new CurveSpec()
            cloned.delegate = curveSpec
            cloned()
            // create curve
            Curve curve = new Curve(curveSpec.model, curveSpec.variable)
            consumer.accept(curve)

            LOGGER.debug("Found curve '{}'", curveSpec.model)
            observer?.curveFound(curveSpec.model)
        }
    }
}
