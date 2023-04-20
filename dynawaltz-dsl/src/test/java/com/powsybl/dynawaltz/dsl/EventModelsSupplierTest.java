/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl;

import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.*;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.models.events.AbstractEventModel;
import com.powsybl.dynawaltz.models.events.EventQuadripoleDisconnection;
import com.powsybl.dynawaltz.models.events.EventSetPointBoolean;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class EventModelsSupplierTest extends AbstractModelSupplierTest {

    private static final String FOLDER_NAME = "/eventModels/";
    protected static final List<EventModelGroovyExtension> EXTENSIONS = GroovyExtension.find(EventModelGroovyExtension.class, DynaWaltzProvider.NAME);

    @Test
    void testGroovyExtensionCount() {
        assertEquals(2, EXTENSIONS.size());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideEventModelData")
    void testEventModels(String groovyScriptName, Class<? extends AbstractEventModel> modelClass, Network network, String equipmentStaticId, String dynamicId, String lib, double startTime) {
        EventModelsSupplier supplier = new GroovyEventModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<EventModel> eventModels = supplier.get(network);
        assertEquals(1, eventModels.size());
        assertTrue(modelClass.isInstance(eventModels.get(0)));
        assertEventModel(modelClass.cast(eventModels.get(0)), dynamicId, equipmentStaticId, lib, startTime);
    }

    void assertEventModel(AbstractEventModel em, String dynamicId, String equipmentStaticId, String lib, double startTime) {
        assertEquals(dynamicId, em.getDynamicModelId());
        assertEquals(equipmentStaticId, em.getEquipmentStaticId());
        assertEquals(dynamicId, em.getParameterSetId());
        assertEquals(lib, em.getLib());
        assertEquals(startTime, em.getStartTime());
    }

    private static Stream<Arguments> provideEventModelData() {
        return Stream.of(
                Arguments.of("quadripoleDisconnection", EventQuadripoleDisconnection.class, EurostagTutorialExample1Factory.create(), "NHV1_NHV2_1", "EM_NHV1_NHV2_1", "EventQuadripoleDisconnection", 4),
                Arguments.of("setPointBoolean", EventSetPointBoolean.class, EurostagTutorialExample1Factory.create(), "GEN", "EM_GEN", "EventSetPointBoolean", 1)
        );
    }

    @Override
    String getFolderName() {
        return FOLDER_NAME;
    }
}
