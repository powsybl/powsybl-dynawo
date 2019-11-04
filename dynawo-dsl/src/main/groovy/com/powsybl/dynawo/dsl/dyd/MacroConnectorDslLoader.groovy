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
import com.powsybl.dynawo.dyd.Connection
import com.powsybl.dynawo.dyd.DydConnection
import com.powsybl.dynawo.dyd.DynawoDynamicModel
import com.powsybl.dynawo.dyd.MacroConnector
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class MacroConnectorDslLoader extends DslLoader {

    static LOGGER = LoggerFactory.getLogger(MacroConnectorDslLoader.class)

    static class ConnectionSpec {

        String var1
        String var2

        void var1(String var1) {
            this.var1 = var1
        }

        void var2(String var2) {
            this.var2 = var2
        }
    }

    static class ConnectionsSpec {
    }

    static class MacroConnectorSpec {

        int id
        final ConnectionsSpec connectionsSpec = new ConnectionsSpec()

        void connections(Closure<Void> closure) {
            def cloned = closure.clone()
            cloned.delegate = connectionsSpec
            cloned()
        }
    }

    MacroConnectorDslLoader(GroovyCodeSource dslSrc) {
        super(dslSrc)
    }

    MacroConnectorDslLoader(File dslFile) {
        super(dslFile)
    }

    MacroConnectorDslLoader(String script) {
        super(script)
    }

    static void loadDsl(Binding binding, Network network, Consumer<DynawoDynamicModel> consumer, DynawoDslLoaderObserver observer) {

        // macro connector
        binding.macroConnector = { String id, Closure<Void> closure ->
            def cloned = closure.clone()
            MacroConnectorSpec macroConnectorSpec = new MacroConnectorSpec()

            List<DydConnection> connections = new ArrayList<>()
            addConnections(macroConnectorSpec.connectionsSpec.metaClass, connections, binding)

            cloned.delegate = macroConnectorSpec
            cloned()

            // create dynamicModel
            MacroConnector dynamicModel = new MacroConnector(id)
            dynamicModel.addConnections(connections)
            consumer.accept(dynamicModel)
            LOGGER.debug("Found modelicaModel '{}'", id)
            observer?.dynamicModelFound(id)
        }
    }

    static void addConnections(MetaClass connectionsSpecMetaClass, List<Connection> connections, Binding binding) {

        connectionsSpecMetaClass.connection = { Closure<Void> closure ->
            def cloned = closure.clone()
            ConnectionSpec connectionSpec = new ConnectionSpec()
            cloned.delegate = connectionSpec
            cloned()
            DydConnection connection = new DydConnection(connectionSpec.var1, connectionSpec.var2)
            connections.add(connection)
        }
    }
}
