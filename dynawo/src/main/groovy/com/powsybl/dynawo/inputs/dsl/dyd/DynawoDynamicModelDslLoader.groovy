/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.dsl.dyd

import java.util.function.Consumer

import org.slf4j.LoggerFactory

import com.powsybl.dynawo.inputs.dsl.DynawoDslLoaderObserver
import com.powsybl.dynawo.inputs.model.dyd.BlackBoxModel
import com.powsybl.dynawo.inputs.model.dyd.Connection
import com.powsybl.dynawo.inputs.model.dyd.DydComponent
import com.powsybl.dynawo.inputs.model.dyd.DynawoDynamicModel
import com.powsybl.dynawo.inputs.model.dyd.InitConnection
import com.powsybl.dynawo.inputs.model.dyd.MacroConnection
import com.powsybl.dynawo.inputs.model.dyd.ModelTemplateExpansion
import com.powsybl.dynawo.inputs.model.dyd.StaticRef
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoDynamicModelDslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoDynamicModelDslLoader.class)

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
        String id
    }

    static class MacroStaticRefsSpec {
    }

    static class BlackBoxModelSpec {

        String id;
        String lib;
        String parametersFile;
        String parametersId;
        String staticId;
        final StaticRefsSpec staticRefsSpec = new StaticRefsSpec()
        final MacroStaticRefsSpec macroStaticRefsSpec = new MacroStaticRefsSpec()

        BlackBoxModelSpec lib(String lib) {
            assert lib != null
            this.lib = lib
            return this
        }

        BlackBoxModelSpec parametersFile(String parametersFile) {
            assert parametersFile != null
            this.parametersFile = parametersFile
            return this
        }

        BlackBoxModelSpec parametersId(String parametersId) {
            assert parametersId != null
            this.parametersId = parametersId
            return this
        }

        BlackBoxModelSpec staticId(String staticId) {
            assert staticId != null
            this.staticId = staticId
            return this
        }

        BlackBoxModelSpec staticRefs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = staticRefsSpec
            cloned()
            return this
        }

        BlackBoxModelSpec macroStaticRefs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = macroStaticRefsSpec
            cloned()
            return this
        }
    }

    static class ModelTemplateExpansionSpec {

        String id
        String templateId
        String parametersFile
        String parametersId

        ModelTemplateExpansionSpec templateId(String templateId) {
            assert templateId != null
            this.templateId = templateId
            return this
        }

        ModelTemplateExpansionSpec parametersFile(String parametersFile) {
            assert parametersFile != null
            this.parametersFile = parametersFile
            return this
        }

        ModelTemplateExpansionSpec parametersId(String parametersId) {
            assert parametersId != null
            this.parametersId = parametersId
            return this
        }
    }

    static class ConnectionSpec {

        String id1
        String var1
        String id2
        String var2

        ConnectionSpec id1(String id1) {
            assert id1 != null
            this.id1 = id1
            return this
        }

        ConnectionSpec var1(String var1) {
            assert var1 != null
            this.var1 = var1
            return this
        }

        ConnectionSpec id2(String id2) {
            assert id2 != null
            this.id2 = id2
            return this
        }

        ConnectionSpec var2(String var2) {
            assert var2 != null
            this.var2 = var2
            return this
        }
    }

    static class MacroConnectionSpec {

        String connector
        String id1
        String id2

        MacroConnectionSpec connector(String connector) {
            assert connector != null
            this.connector = connector
            return this
        }

        MacroConnectionSpec id1(String id1) {
            assert id1 != null
            this.id1 = id1
            return this
        }

        MacroConnectionSpec id2(String id2) {
            assert id2 != null
            this.id2 = id2
            return this
        }
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoDynamicModel> consumer, DynawoDslLoaderObserver observer) {

        // blackBoxModels
        binding.blackBoxModel = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            BlackBoxModelSpec blackBoxModelSpec = new BlackBoxModelSpec()

            List<StaticRef> staticRefs = new ArrayList<>()
            addStaticRefs(blackBoxModelSpec.staticRefsSpec.metaClass, staticRefs, binding)

            List<DydComponent> macroStaticRefs = new ArrayList<>()
            addMacroStaticReferences(blackBoxModelSpec.macroStaticRefsSpec.metaClass, macroStaticRefs, binding)

            cloned.delegate = blackBoxModelSpec
            cloned()

            // create dynamicModel
            DynawoDynamicModel dynamicModel = new BlackBoxModel(id, blackBoxModelSpec.lib, blackBoxModelSpec.parametersFile, blackBoxModelSpec.parametersId, blackBoxModelSpec.staticId)
            dynamicModel.addStaticRefs(staticRefs)
            dynamicModel.addMacroStaticRefs(macroStaticRefs)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found blackBoxModel '{}'", id)
            observer?.dynamicModelFound(id)
        }

        // modelicaModels
        ModelicaModelDslLoader.loadDsl(binding, network, {d -> consumer.accept(d)}, observer)

        // modelTemplates
        ModelTemplateDslLoader.loadDsl(binding, network, {d -> consumer.accept(d)}, observer)

        // modelTemplateExpansions
        binding.modelTemplateExpansion = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            ModelTemplateExpansionSpec modelTemplateExpansionSpec = new ModelTemplateExpansionSpec()
            cloned.delegate = modelTemplateExpansionSpec
            cloned()

            // create dynamicModel
            DynawoDynamicModel dynamicModel = new ModelTemplateExpansion(id, modelTemplateExpansionSpec.templateId, modelTemplateExpansionSpec.parametersFile, modelTemplateExpansionSpec.parametersId)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found modelTemplateExpansion '{}'", id)
            observer?.dynamicModelFound(id)
        }

        // connections
        binding.connection = { Closure<Void> closure ->
            def cloned = closure.clone()
            ConnectionSpec connectionSpec = new ConnectionSpec()
            cloned.delegate = connectionSpec
            cloned()

            // create dynamicModel
            DynawoDynamicModel dynamicModel = new Connection(connectionSpec.id1, connectionSpec.var1, connectionSpec.id2, connectionSpec.var2)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found connection '{}'", connectionSpec.id1)
            observer?.dynamicModelFound(connectionSpec.id1)
        }

        // initConnections
        binding.initConnection = { Closure<Void> closure ->
            def cloned = closure.clone()
            ConnectionSpec connectionSpec = new ConnectionSpec()
            cloned.delegate = connectionSpec
            cloned()

            // create dynamicModel
            DynawoDynamicModel dynamicModel = new InitConnection(connectionSpec.id1, connectionSpec.var1, connectionSpec.id2, connectionSpec.var2)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found initConnection '{}'", connectionSpec.id1)
            observer?.dynamicModelFound(connectionSpec.id1)
        }

        // macroConnectors
        MacroConnectorDslLoader.loadDsl(binding, network, {d -> consumer.accept(d)}, observer)

        // macroConnections
        binding.macroConnection = { Closure<Void> closure ->
            def cloned = closure.clone()
            MacroConnectionSpec macroConnectionSpec = new MacroConnectionSpec()
            cloned.delegate = macroConnectionSpec
            cloned()

            // create dynamicModel
            DynawoDynamicModel dynamicModel = new MacroConnection(macroConnectionSpec.connector, macroConnectionSpec.id1, macroConnectionSpec.id2)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found macroConnection '{}'", macroConnectionSpec.connector)
            observer?.dynamicModelFound(macroConnectionSpec.connector)
        }

        // macroStaticRefs
        MacroStaticRefDslLoader.loadDsl(binding, network, {d -> consumer.accept(d)}, observer)
    }

    static void addStaticRefs(MetaClass staticRefsSpecMetaClass, List<StaticRef> staticRefs, Binding binding) {
        binding.setVariable("staticRefs", staticRefs)
        staticRefsSpecMetaClass.staticRef = { Closure<Void> closure ->
            def cloned = closure.clone()
            StaticRefSpec staticRefSpec = new StaticRefSpec()
            cloned.delegate = staticRefSpec
            cloned()
            StaticRef staticRef = new StaticRef(staticRefSpec.var, staticRefSpec.staticVar)
            staticRefs = binding.getVariable("staticRefs")
            staticRefs.add(staticRef)
        }
    }

    static void addMacroStaticReferences(MetaClass macroStaticRefsSpecMetaClass, List<DydComponent> macroStaticRefs, Binding binding) {

        macroStaticRefsSpecMetaClass.macroStaticRef = { String id ->
            DydComponent macroStaticRef = new DydComponent(id)
            macroStaticRefs.add(macroStaticRef)
        }
    }
}
