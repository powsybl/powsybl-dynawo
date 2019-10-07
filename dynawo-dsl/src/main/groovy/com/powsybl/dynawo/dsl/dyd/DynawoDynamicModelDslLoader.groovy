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
import com.powsybl.dynawo.dyd.BlackBoxModel
import com.powsybl.dynawo.dyd.Connection
import com.powsybl.dynawo.dyd.DynawoDynamicModel
import com.powsybl.dynawo.dyd.InitConnection
import com.powsybl.dynawo.dyd.MacroConnection
import com.powsybl.dynawo.dyd.ModelTemplateExpansion
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoDynamicModelDslLoader extends DslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoDynamicModelDslLoader.class)

    static class BlackBoxModelSpec {

        String id;
        String lib;
        String parametersFile;
        int parametersId;
        String staticId;

        void id(String id) {
            this.id = id
        }

        void lib(String lib) {
            this.lib = lib
        }

        void parametersFile(String parametersFile) {
            this.parametersFile = parametersFile
        }

        void parametersId(int parametersId) {
            this.parametersId = parametersId
        }

        void staticId(String staticId) {
            this.staticId = staticId
        }
    }

    static class ModelTemplateExpansionSpec {

        String id
        String templateId
        String parametersFile
        int parametersId

        void id(String id) {
            this.id = id
        }

        void templateId(String templateId) {
            this.templateId = templateId
        }

        void parametersFile(String parametersFile) {
            this.parametersFile = parametersFile
        }

        void parametersId(int parametersId) {
            this.parametersId = parametersId
        }
    }

    static class ConnectionSpec {

        String id1
        String var1
        String id2
        String var2

        void id1(String id1) {
            this.id1 = id1
        }

        void var1(String var1) {
            this.var1 = var1
        }

        void id2(String id2) {
            this.id2 = id2
        }

        void var2(String var2) {
            this.var2 = var2
        }
    }

    static class MacroConnectionSpec {

        String connector
        String id1
        String id2

        void connector(String connector) {
            this.connector = connector
        }

        void id1(String id1) {
            this.id1 = id1
        }

        void id2(String id2) {
            this.id2 = id2
        }
    }

    DynawoDynamicModelDslLoader(GroovyCodeSource dslSrc) {
        super(dslSrc)
    }

    DynawoDynamicModelDslLoader(File dslFile) {
        super(dslFile)
    }

    DynawoDynamicModelDslLoader(String script) {
        super(script)
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoDynamicModel> consumer, DynawoDslLoaderObserver observer) {

        // set base network
        binding.setVariable("network", network)

        // blackBoxModels
        binding.blackBoxModel = { Closure<Void> closure ->
            def cloned = closure.clone()
            BlackBoxModelSpec blackBoxModelSpec = new BlackBoxModelSpec()
            cloned.delegate = blackBoxModelSpec
            cloned()

            // create dynamicModel
            DynawoDynamicModel dynamicModel = new BlackBoxModel(blackBoxModelSpec.id, blackBoxModelSpec.lib, blackBoxModelSpec.parametersFile, blackBoxModelSpec.parametersId, blackBoxModelSpec.staticId)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found blackBoxModel '{}'", blackBoxModelSpec.id)
            observer?.dynamicModelFound(blackBoxModelSpec.id)
        }

        // modelicaModels
        ModelicaModelDslLoader.loadDsl(binding, network, {d -> consumer.accept(d)}, observer)

        // modelTemplates
        ModelTemplateDslLoader.loadDsl(binding, network, {d -> consumer.accept(d)}, observer)

        // modelTemplateExpansions
        binding.modelTemplateExpansion = { Closure<Void> closure ->
            def cloned = closure.clone()
            ModelTemplateExpansionSpec modelTemplateExpansionSpec = new ModelTemplateExpansionSpec()
            cloned.delegate = modelTemplateExpansionSpec
            cloned()

            // create dynamicModel
            DynawoDynamicModel dynamicModel = new ModelTemplateExpansion(modelTemplateExpansionSpec.id, modelTemplateExpansionSpec.templateId, modelTemplateExpansionSpec.parametersFile, modelTemplateExpansionSpec.parametersId)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found modelTemplateExpansion '{}'", modelTemplateExpansionSpec.id)
            observer?.dynamicModelFound(modelTemplateExpansionSpec.id)
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
            LOGGER.debug("Found initConnection '{}'", connectionSpec.id)
            observer?.dynamicModelFound(connectionSpec.id)
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

    List<DynawoDynamicModel> load(Network network) {
        load(network, null)
    }

    List<DynawoDynamicModel> load(Network network, DynawoDslLoaderObserver observer) {

        List<DynawoDynamicModel> dynamicModels = new ArrayList<>()

        try {
            observer?.begin(dslSrc.getName())

            Binding binding = new Binding()

            loadDsl(binding, network, dynamicModels.&add, observer)

            // set base network
            binding.setVariable("network", network)

            def shell = createShell(binding)

            shell.evaluate(dslSrc)

            observer?.end()

            dynamicModels

        } catch (CompilationFailedException e) {
            throw new DslException(e.getMessage(), e)
        }
    }

}
