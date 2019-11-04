/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl.dyd

import java.util.function.Consumer

import org.codehaus.groovy.control.CompilationFailedException
import org.slf4j.LoggerFactory

import com.powsybl.dsl.DslException
import com.powsybl.dsl.DslLoader
import com.powsybl.dynawo.dsl.DynawoDslLoaderObserver
import com.powsybl.dynawo.dyd.DydConnection
import com.powsybl.dynawo.dyd.DynawoDynamicModel
import com.powsybl.dynawo.dyd.MacroStaticReference
import com.powsybl.dynawo.dyd.StaticRef
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class MacroStaticRefDslLoader extends DslLoader {

    static LOGGER = LoggerFactory.getLogger(MacroStaticRefDslLoader.class)

    static class StaticRefSpec {

        String var
        String staticVar

        void var(String var) {
            this.var = var
        }

        void staticVar(String staticVar) {
            this.staticVar = staticVar
        }
    }

    static class StaticRefsSpec {
    }

    static class MacroStaticRefSpec {

        int id
        final StaticRefsSpec staticRefsSpec = new StaticRefsSpec()

        void staticRefs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = staticRefsSpec
            cloned()
        }
    }

    MacroStaticRefDslLoader(GroovyCodeSource dslSrc) {
        super(dslSrc)
    }

    MacroStaticRefDslLoader(File dslFile) {
        super(dslFile)
    }

    MacroStaticRefDslLoader(String script) {
        super(script)
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
