/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class BuilderConfigTest {

    private static Network network;
    private static ModelConfigsHandler modelConfigsHandler;

    @BeforeAll
    static void setup() {
        network = EurostagTutorialExample1Factory.create();
        modelConfigsHandler = ModelConfigsHandler.getInstance();
    }

    @Test
    void testModelBuilders() {
        for (BuilderConfig builderConfig : modelConfigsHandler.getBuilderConfigs()) {
            String modelName = builderConfig.getModelInfos().iterator().next().name();
            ModelBuilder<DynamicModel> handlerBuilder = modelConfigsHandler.getModelBuilder(network, modelName, ReportNode.NO_OP);
            ModelBuilder<DynamicModel> configBuilder = builderConfig.getBuilderConstructor().createBuilder(network, modelName, ReportNode.NO_OP);
            assertNotNull(handlerBuilder);
            assertEquals(handlerBuilder.getClass(), configBuilder.getClass());
        }
    }

    @Test
    void testEventBuilders() {
        for (EventBuilderConfig eventBuilderConfig : modelConfigsHandler.getEventBuilderConfigs()) {
            ModelBuilder<EventModel> handlerBuilder = modelConfigsHandler.getEventModelBuilder(network, eventBuilderConfig.getEventModelInfo().name(), ReportNode.NO_OP);
            ModelBuilder<EventModel> configBuilder = eventBuilderConfig.getBuilderConstructor().createBuilder(network, ReportNode.NO_OP);
            assertNotNull(handlerBuilder);
            assertEquals(handlerBuilder.getClass(), configBuilder.getClass());
        }
    }
}
