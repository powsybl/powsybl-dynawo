/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

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
public class BuilderConfigTest {

    private static Network NETWORK;
    private static ModelConfigsHandler MODEL_CONFIGS_HANDLER;

    @BeforeAll
    static void setup() {
        NETWORK = EurostagTutorialExample1Factory.create();
        MODEL_CONFIGS_HANDLER = ModelConfigsHandler.getInstance();
    }

    @Test
    void testModelBuilders() {
        for (BuilderConfig builderConfig : MODEL_CONFIGS_HANDLER.getBuilderConfigs()) {
            String lib = builderConfig.getLibs().iterator().next();
            ModelBuilder<DynamicModel> tagBuilder = MODEL_CONFIGS_HANDLER.getModelBuilder(NETWORK, lib, ReportNode.NO_OP);
            ModelBuilder<DynamicModel> configBuilder = builderConfig.getBuilderConstructor().createBuilder(NETWORK, lib, ReportNode.NO_OP);
            assertNotNull(tagBuilder);
            assertEquals(tagBuilder.getClass(), configBuilder.getClass());
        }
    }

    @Test
    void testEventBuilders() {
        for (EventBuilderConfig eventBuilderConfig : MODEL_CONFIGS_HANDLER.getEventBuilderConfigs()) {
            ModelBuilder<EventModel> tagBuilder = MODEL_CONFIGS_HANDLER.getEventModelBuilder(NETWORK, eventBuilderConfig.getTag(), ReportNode.NO_OP);
            ModelBuilder<EventModel> configBuilder = eventBuilderConfig.getBuilderConstructor().createBuilder(NETWORK, ReportNode.NO_OP);
            assertNotNull(tagBuilder);
            assertEquals(tagBuilder.getClass(), configBuilder.getClass());
        }
    }
}
