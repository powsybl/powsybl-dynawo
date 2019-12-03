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
import com.powsybl.dynawo.dyd.Connection
import com.powsybl.dynawo.dyd.DydComponent
import com.powsybl.dynawo.dyd.DynawoDynamicModel
import com.powsybl.dynawo.dyd.InitConnection
import com.powsybl.dynawo.dyd.ModelicaModel
import com.powsybl.dynawo.dyd.StaticRef
import com.powsybl.dynawo.dyd.UnitDynamicModel
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class ModelicaModelDslLoader {

    static LOGGER = LoggerFactory.getLogger(ModelicaModelDslLoader.class)

    static class UnitDynamicModelSpec {

        String id
        String name
        String moFile
        String initName
        String parametersFile
        String parametersId

        UnitDynamicModelSpec name(String name) {
			assert name != null
            this.name = name
			return this
        }

        UnitDynamicModelSpec moFile(String moFile) {
			assert moFile != null
            this.moFile = moFile
			return this
        }

        UnitDynamicModelSpec initName(String initName) {
			assert initName != null
            this.initName = initName
			return this
        }

        UnitDynamicModelSpec parametersFile(String parametersFile) {
			assert parametersFile != null
            this.parametersFile = parametersFile
			return this
        }

        UnitDynamicModelSpec parametersId(String parametersId) {
			assert parametersId != null
            this.parametersId = parametersId
			return this
        }
    }

    static class UnitDynamicModelsSpec {
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

    static class ConnectionsSpec {
    }

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

    static class ModelicaModelSpec {

        String id
        final UnitDynamicModelsSpec unitDynamicModelsSpec = new UnitDynamicModelsSpec()
        final ConnectionsSpec connectionsSpec = new ConnectionsSpec()
        final ConnectionsSpec initConnectionsSpec = new ConnectionsSpec()
        final StaticRefsSpec staticRefsSpec = new StaticRefsSpec()
        final MacroStaticRefsSpec macroStaticRefsSpec = new MacroStaticRefsSpec()

        ModelicaModelSpec unitDynamicModels(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = unitDynamicModelsSpec
            cloned()
			return this
        }

        ModelicaModelSpec connections(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = connectionsSpec
            cloned()
			return this
        }

        ModelicaModelSpec initConnections(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = initConnectionsSpec
            cloned()
			return this
        }

        ModelicaModelSpec staticRefs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = staticRefsSpec
            cloned()
			return this
        }

        ModelicaModelSpec macroStaticRefs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = macroStaticRefsSpec
            cloned()
			return this
        }
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoDynamicModel> consumer, DynawoDslLoaderObserver observer) {

        // modelica model
        binding.modelicaModel = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            ModelicaModelSpec modelicaModelSpec = new ModelicaModelSpec()

            List<UnitDynamicModel> unitDynamicModels = new ArrayList<>()
            addUnitDynamicModels(modelicaModelSpec.unitDynamicModelsSpec.metaClass, unitDynamicModels, binding)

            List<Connection> connections = new ArrayList<>()
            addConnections(modelicaModelSpec.connectionsSpec.metaClass, connections, binding)

            List<InitConnection> initConnections = new ArrayList<>()
            addInitConnections(modelicaModelSpec.initConnectionsSpec.metaClass, initConnections, binding)

            List<StaticRef> staticRefs = new ArrayList<>()
            addStaticRefs(modelicaModelSpec.staticRefsSpec.metaClass, staticRefs, binding)

            List<DydComponent> macroStaticRefs = new ArrayList<>()
            addMacroStaticReferences(modelicaModelSpec.macroStaticRefsSpec.metaClass, macroStaticRefs, binding)

            cloned.delegate = modelicaModelSpec
            cloned()

            // create dynamicModel
            ModelicaModel dynamicModel = new ModelicaModel(id)
            dynamicModel.addUnitDynamicModels(unitDynamicModels)
            dynamicModel.addConnections(connections)
            dynamicModel.addInitConnections(initConnections)
            dynamicModel.addStaticRefs(staticRefs)
            dynamicModel.addMacroStaticRefs(macroStaticRefs)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found modelicaModel '{}'", id)
            observer?.dynamicModelFound(id)
        }
    }

    static void addUnitDynamicModels(MetaClass unitDynamicModelsSpecMetaClass, List<UnitDynamicModel> unitDynamicModels, Binding binding) {

        unitDynamicModelsSpecMetaClass.unitDynamicModel = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            UnitDynamicModelSpec unitDynamicModelSpec = new UnitDynamicModelSpec()
            cloned.delegate = unitDynamicModelSpec
            cloned()
            UnitDynamicModel unitDynamicModel = new UnitDynamicModel(id, unitDynamicModelSpec.name, unitDynamicModelSpec.moFile, unitDynamicModelSpec.initName, unitDynamicModelSpec.parametersFile, unitDynamicModelSpec.parametersId)
            unitDynamicModels.add(unitDynamicModel)
        }
    }

    static void addConnections(MetaClass connectionsSpecMetaClass, List<Connection> connections, Binding binding) {

        connectionsSpecMetaClass.connection = { Closure<Void> closure ->
            def cloned = closure.clone()
            ConnectionSpec connectionSpec = new ConnectionSpec()
            cloned.delegate = connectionSpec
            cloned()
            Connection connection = new Connection(connectionSpec.id1, connectionSpec.var1, connectionSpec.id2, connectionSpec.var2)
            connections.add(connection)
        }
    }

    static void addInitConnections(MetaClass initConnectionsSpecMetaClass, List<InitConnection> initConnections, Binding binding) {

        initConnectionsSpecMetaClass.initConnection = { Closure<Void> closure ->
            def cloned = closure.clone()
            ConnectionSpec initConnectionSpec = new ConnectionSpec()
            cloned.delegate = initConnectionSpec
            cloned()
            InitConnection initConnection = new InitConnection(initConnectionSpec.id1, initConnectionSpec.var1, initConnectionSpec.id2, initConnectionSpec.var2)
            initConnections.add(initConnection)
        }
    }

    static void addStaticRefs(MetaClass staticRefsSpecMetaClass, List<StaticRef> staticRefs, Binding binding) {
        staticRefsSpecMetaClass.staticRef = { Closure<Void> closure ->
            def cloned = closure.clone()
            StaticRefSpec staticRefSpec = new StaticRefSpec()
            cloned.delegate = staticRefSpec
            cloned()
            StaticRef staticRef = new StaticRef(staticRefSpec.var, staticRefSpec.staticVar)
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
