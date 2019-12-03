/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl.dyd

import java.util.function.Consumer

import org.slf4j.LoggerFactory

import com.powsybl.dynawo.dsl.DynawoDslLoaderObserver
import com.powsybl.dynawo.dyd.DynawoDynamicModel
import com.powsybl.dynawo.dyd.MacroStaticReference
import com.powsybl.dynawo.dyd.StaticRef
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class MacroStaticRefDslLoader {

    static LOGGER = LoggerFactory.getLogger(MacroStaticRefDslLoader.class)

    static class StaticRefSpec {

        String var
        String staticVar

        StaticRefSpec var(String var) {
			assert var != null
            this.var = var
			return this
        }

        StaticRefSpec staticVar(String staticVar) {
			assert staticVar != null
            this.staticVar = staticVar
			return this
        }
    }

    static class StaticRefsSpec {
    }

    static class MacroStaticRefSpec {

        int id
        final StaticRefsSpec staticRefsSpec = new StaticRefsSpec()

        MacroStaticRefSpec staticRefs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = staticRefsSpec
            cloned()
			return this
        }
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoDynamicModel> consumer, DynawoDslLoaderObserver observer) {

        // macro static ref
        binding.macroStaticRef = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            MacroStaticRefSpec macroStaticRefSpec = new MacroStaticRefSpec()

            List<StaticRef> staticRefs = new ArrayList<>()
            addStaticRefs(macroStaticRefSpec.staticRefsSpec.metaClass, staticRefs, binding)

            cloned.delegate = macroStaticRefSpec
            cloned()

            // create dynamicModel
            MacroStaticReference dynamicModel = new MacroStaticReference(id)
            dynamicModel.addStaticRefs(staticRefs)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found modelicaModel '{}'", id)
            observer?.dynamicModelFound(id)
        }
    }

    static void addStaticRefs(MetaClass connectionsSpecMetaClass, List<StaticRef> staticRefs, Binding binding) {

        connectionsSpecMetaClass.staticRef = { Closure<Void> closure ->
            def cloned = closure.clone()
            StaticRefSpec staticRefSpec = new StaticRefSpec()
            cloned.delegate = staticRefSpec
            cloned()
            StaticRef staticRef = new StaticRef(staticRefSpec.var, staticRefSpec.staticVar)
            staticRefs.add(staticRef)
        }
    }
}
