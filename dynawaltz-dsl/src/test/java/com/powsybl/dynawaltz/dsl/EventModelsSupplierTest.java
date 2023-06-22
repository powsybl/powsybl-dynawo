/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyEventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.models.events.*;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import com.powsybl.iidm.network.test.HvdcTestNetwork;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
class EventModelsSupplierTest extends AbstractModelSupplierTest {

    protected static final List<EventModelGroovyExtension> EXTENSIONS = GroovyExtension.find(EventModelGroovyExtension.class, DynaWaltzProvider.NAME);

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideEventModelData")
    void testEventModels(String groovyScriptName, Class<? extends AbstractEventModel> modelClass, Network network, String equipmentStaticId, String dynamicId, String lib, double startTime) {
        EventModelsSupplier supplier = new GroovyEventModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<EventModel> eventModels = supplier.get(network);
        assertEquals(1, eventModels.size());
        assertTrue(modelClass.isInstance(eventModels.get(0)));
        assertEventModel(modelClass.cast(eventModels.get(0)), dynamicId, equipmentStaticId, lib, startTime);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideWarningsModel")
    void testDslWarnings(String groovyScriptName, Network network) {
        EventModelsSupplier supplier = new GroovyEventModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<EventModel> eventModels = supplier.get(network);
        assertEquals(1, eventModels.size());
        assertNull(eventModels.get(0));
    }

    void assertEventModel(AbstractEventModel em, String dynamicId, String equipmentStaticId, String lib, double startTime) {
        assertEquals(dynamicId, em.getDynamicModelId());
        assertEquals(equipmentStaticId, em.getEquipment().getId());
        assertEquals(dynamicId, em.getParameterSetId());
        assertEquals(startTime, em.getStartTime());
        if (lib != null) {
            assertEquals(lib, em.getLib());
        } else {
            Exception e = assertThrows(PowsyblException.class, em::getLib);
            assertEquals("The associated library depends on context", e.getMessage());
        }
    }

    private static Stream<Arguments> provideEventModelData() {
        return Stream.of(
                Arguments.of("/eventModels/quadripoleDisconnection.groovy", EventQuadripoleDisconnection.class, EurostagTutorialExample1Factory.create(), "NHV1_NHV2_1", "Disconnect_NHV1_NHV2_1", "EventQuadripoleDisconnection", 4),
                Arguments.of("/eventModels/equipmentDisconnection.groovy", EventInjectionDisconnection.class, EurostagTutorialExample1Factory.create(), "GEN", "Disconnect_GEN", null, 1),
                Arguments.of("/eventModels/hvdcDisconnection.groovy", EventHvdcDisconnection.class, HvdcTestNetwork.createVsc(), "L", "Disconnect_L", null, 2),
                Arguments.of("/eventModels/nodeFault.groovy", NodeFaultEvent.class, EurostagTutorialExample1Factory.create(), "NGEN", "Node_Fault_NGEN", "NodeFault", 1),
                Arguments.of("/eventModels/step.groovy", EventActivePowerVariation.class, EurostagTutorialExample1Factory.create(), "LOAD", "Step_LOAD", null, 2)
        );
    }

    private static Stream<Arguments> provideWarningsModel() {
        return Stream.of(
                Arguments.of("/eventWarnings/missingStaticId.groovy", EurostagTutorialExample1Factory.create()),
                Arguments.of("/eventWarnings/missingStartTime.groovy", EurostagTutorialExample1Factory.create()),
                Arguments.of("/eventWarnings/missingNodeFaultParameters.groovy", EurostagTutorialExample1Factory.create()),
                Arguments.of("/eventWarnings/missingAPVParameters.groovy", EurostagTutorialExample1Factory.create()),
                Arguments.of("/eventWarnings/missingDisconnectionEquipment.groovy", EurostagTutorialExample1Factory.create()),
                Arguments.of("/eventWarnings/missingDisconnectionSide.groovy", EurostagTutorialExample1Factory.create())
        );
    }
}
