/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl

import java.util.function.Consumer

import org.codehaus.groovy.control.CompilationFailedException
import org.slf4j.LoggerFactory

import com.powsybl.dsl.DslException
import com.powsybl.dsl.DslLoader
import com.powsybl.dynawo.*
import com.powsybl.iidm.network.Network

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
class DynawoDynamicModelDslLoader extends DslLoader {

    static LOGGER = LoggerFactory.getLogger(DynawoDynamicModelDslLoader.class)

    static class DynamicModelSpec {

        String blackBoxModelId;
        String blackBoxModelLib;
        String parametersFile;
        int parametersId;
        String staticId;

        String connectionId1;
        String connectionVar1;
        String connectionId2;
        String connectionVar2;

        void blackBoxModelId(String blackBoxModelId) {
            this.blackBoxModelId = blackBoxModelId
        }
        
        void blackBoxModelLib(String blackBoxModelLib) {
            this.blackBoxModelLib = blackBoxModelLib
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
        
        void connectionId1(String connectionId1) {
            this.connectionId1 = connectionId1
        }
        
        void connectionVar1(String connectionVar1) {
            this.connectionVar1 = connectionVar1
        }
        
        void connectionId2(String connectionId2) {
            this.connectionId2 = connectionId2
        }
        
        void connectionVar2(String connectionVar2) {
            this.connectionVar2 = connectionVar2
        }

        boolean isBlackBoxModel() {
            return blackBoxModelId != null && blackBoxModelId.length() > 0
        }

        boolean isConnection() {
            return connectionId1 != null && connectionId1.length() > 0
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

        // dynamicModels
        binding.dynamicModel = { Closure<Void> closure ->
            def cloned = closure.clone()
            DynamicModelSpec dynamicModelSpec = new DynamicModelSpec()
            cloned.delegate = dynamicModelSpec
            cloned()

            // create dynamicModel
            if (dynamicModelSpec.isBlackBoxModel()) {
                DynawoDynamicModel dynamicModel = new DynawoDynamicModel(dynamicModelSpec.blackBoxModelId, dynamicModelSpec.blackBoxModelLib, dynamicModelSpec.parametersFile, dynamicModelSpec.parametersId, dynamicModelSpec.staticId)
                consumer.accept(dynamicModel)
                LOGGER.debug("Found dynamicModel '{}'", dynamicModelSpec.blackBoxModelId)
                observer?.dynamicModelFound(dynamicModelSpec.blackBoxModelId)
            }

            if (dynamicModelSpec.isConnection()) {
                DynawoDynamicModel dynamicModel = new DynawoDynamicModel(dynamicModelSpec.connectionId1, dynamicModelSpec.connectionVar1, dynamicModelSpec.connectionId2, dynamicModelSpec.connectionVar2)
                consumer.accept(dynamicModel)
                LOGGER.debug("Found dynamicModel '{}'", dynamicModelSpec.connectionId1)
                observer?.dynamicModelFound(dynamicModelSpec.connectionId1)
            }

        }
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
