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
import com.powsybl.dynawo.dyd.DynawoDynamicModel
import com.powsybl.dynawo.dyd.InitConnection
import com.powsybl.dynawo.dyd.ModelTemplate
import com.powsybl.dynawo.dyd.UnitDynamicModel
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class ModelTemplateDslLoader {

    static LOGGER = LoggerFactory.getLogger(ModelTemplateDslLoader.class)

    static class UnitDynamicModelSpec {

        String id
        String name
        String moFile
        String initName
        String parFile
        int parId

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

        UnitDynamicModelSpec parFile(String parFile) {
            assert parFile != null
            this.parFile = parFile
            return this
        }

        UnitDynamicModelSpec parId(int parId) {
            assert parId != null
            this.parId = parId
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

    static class ModelTemplateSpec {

        int id
        final UnitDynamicModelsSpec unitDynamicModelsSpec = new UnitDynamicModelsSpec()
        final ConnectionsSpec connectionsSpec = new ConnectionsSpec()
        final ConnectionsSpec initConnectionsSpec = new ConnectionsSpec()

        ModelTemplateSpec unitDynamicModels(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = unitDynamicModelsSpec
            cloned()
            return this
        }

        ModelTemplateSpec connections(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = connectionsSpec
            cloned()
            return this
        }

        ModelTemplateSpec initConnections(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = initConnectionsSpec
            cloned()
            return this
        }
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoDynamicModel> consumer, DynawoDslLoaderObserver observer) {

        // model template
        binding.modelTemplate = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            ModelTemplateSpec modelTemplateSpec = new ModelTemplateSpec()

            List<UnitDynamicModel> unitDynamicModels = new ArrayList<>()
            addUnitDynamicModels(modelTemplateSpec.unitDynamicModelsSpec.metaClass, unitDynamicModels, binding)

            List<Connection> connections = new ArrayList<>()
            addConnections(modelTemplateSpec.connectionsSpec.metaClass, connections, binding)

            List<InitConnection> initConnections = new ArrayList<>()
            addInitConnections(modelTemplateSpec.initConnectionsSpec.metaClass, initConnections, binding)

            cloned.delegate = modelTemplateSpec
            cloned()

            // create dynamicModel
            ModelTemplate dynamicModel = new ModelTemplate(id)
            dynamicModel.addUnitDynamicModels(unitDynamicModels)
            dynamicModel.addConnections(connections)
            dynamicModel.addInitConnections(initConnections)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found modelTemplate '{}'", id)
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
}
