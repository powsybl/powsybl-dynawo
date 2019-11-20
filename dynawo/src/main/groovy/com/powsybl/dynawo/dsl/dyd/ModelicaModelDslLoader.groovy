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
        String parFile
        int parId

        void name(String name) {
            this.name = name
        }

        void moFile(String moFile) {
            this.moFile = moFile
        }

        void initName(String initName) {
            this.initName = initName
        }

        void parFile(String parFile) {
            this.parFile = parFile
        }

        void parId(int parId) {
            this.parId = parId
        }
    }

    static class UnitDynamicModelsSpec {
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

    static class ConnectionsSpec {
    }

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
        String id
    }

    static class MacroStaticRefsSpec {
    }

    static class ModelicaModelSpec {

        int id
        final UnitDynamicModelsSpec unitDynamicModelsSpec = new UnitDynamicModelsSpec()
        final ConnectionsSpec connectionsSpec = new ConnectionsSpec()
        final ConnectionsSpec initConnectionsSpec = new ConnectionsSpec()
        final StaticRefsSpec staticRefsSpec = new StaticRefsSpec()
        final MacroStaticRefsSpec macroStaticRefsSpec = new MacroStaticRefsSpec()

        void unitDynamicModels(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = unitDynamicModelsSpec
            cloned()
        }

        void connections(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = connectionsSpec
            cloned()
        }

        void initConnections(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = initConnectionsSpec
            cloned()
        }

        void staticRefs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = staticRefsSpec
            cloned()
        }

        void macroStaticRefs(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = macroStaticRefsSpec
            cloned()
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
            UnitDynamicModel unitDynamicModel = new UnitDynamicModel(id, unitDynamicModelSpec.name, unitDynamicModelSpec.moFile, unitDynamicModelSpec.initName, unitDynamicModelSpec.parFile, unitDynamicModelSpec.parId)
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
