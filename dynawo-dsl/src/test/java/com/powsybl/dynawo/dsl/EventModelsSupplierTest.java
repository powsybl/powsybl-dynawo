/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.dsl;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynamicsimulation.EventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.EventModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyEventModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.dynawo.LfResultsUtils;
import com.powsybl.dynawo.models.events.*;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class EventModelsSupplierTest extends AbstractModelSupplierTest {

    protected static final List<EventModelGroovyExtension> EXTENSIONS = GroovyExtension.find(EventModelGroovyExtension.class, DynawoSimulationProvider.NAME);

    @Test
    void testLibsInfo() {
        for (EventModelGroovyExtension extension : EXTENSIONS) {
            assertNotNull(extension.getModelNames());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideEventModelData")
    void testEventModels(String groovyScriptName, Class<? extends AbstractEvent> modelClass, Network network, String equipmentStaticId, String dynamicId, String lib, double startTime) {
        EventModelsSupplier supplier = new GroovyEventModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        List<EventModel> eventModels = supplier.get(network);
        assertEquals(1, eventModels.size());
        assertTrue(modelClass.isInstance(eventModels.get(0)));
        assertEventModel(modelClass.cast(eventModels.get(0)), dynamicId, equipmentStaticId, lib, startTime);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideWarningsModel")
    void testDslWarnings(String groovyScriptName, Network network, String report) throws IOException {
        EventModelsSupplier supplier = new GroovyEventModelsSupplier(getResourceAsStream(groovyScriptName), EXTENSIONS);
        assertTrue(supplier.get(network, reportNode).isEmpty());
        checkReportNode(report);
    }

    void assertEventModel(AbstractEvent em, String dynamicId, String equipmentStaticId, String lib, double startTime) {
        assertEquals(dynamicId, em.getDynamicModelId());
        assertEquals(equipmentStaticId, em.getEquipment().getId());
        assertEquals(dynamicId, em.getParameterSetId());
        assertEquals(startTime, em.getStartTime());
        if (lib != null) {
            assertEquals(lib, em.getLib());
        } else {
            Exception e = assertThrows(PowsyblException.class, em::getLib);
            assertEquals("Field has not been initialized", e.getMessage());
        }
    }

    private static Stream<Arguments> provideEventModelData() {
        return Stream.of(
                Arguments.of("/eventModels/branchDisconnection.groovy", EventBranchDisconnection.class, EurostagTutorialExample1Factory.createWithLFResults(), "NHV1_NHV2_1", "Disconnect_NHV1_NHV2_1", "EventQuadripoleDisconnection", 4),
                Arguments.of("/eventModels/equipmentDisconnection.groovy", EventInjectionDisconnection.class, EurostagTutorialExample1Factory.createWithLFResults(), "GEN", "Disconnect_GEN", null, 1),
                Arguments.of("/eventModels/hvdcDisconnection.groovy", EventHvdcDisconnection.class, LfResultsUtils.createHvdcTestNetworkVscWithLFResults(), "L", "Disconnect_L", null, 2),
                Arguments.of("/eventModels/nodeFault.groovy", NodeFaultEvent.class, EurostagTutorialExample1Factory.createWithLFResults(), "NGEN", "NodeFault_NGEN", "NodeFault", 1),
                Arguments.of("/eventModels/step.groovy", EventActivePowerVariation.class, EurostagTutorialExample1Factory.createWithLFResults(), "LOAD", "Step_LOAD", null, 2)
        );
    }

    private static Stream<Arguments> provideWarningsModel() {
        return Stream.of(
                Arguments.of("/eventWarnings/missingStaticId.groovy", EurostagTutorialExample1Factory.createWithLFResults(),
                        """
                        + DSL tests
                           + Groovy Event Models Supplier
                              + Model NodeFault NodeFault_GEN instantiation KO
                                 'staticId' field value 'GEN' not found for equipment type(s) BUS
                        """),
                Arguments.of("/eventWarnings/missingStartTime.groovy", EurostagTutorialExample1Factory.createWithLFResults(),
                        """
                        + DSL tests
                           + Groovy Event Models Supplier
                              + Model NodeFault NodeFault_NGEN instantiation KO
                                 'startTime' field is not set
                        """),
                Arguments.of("/eventWarnings/missingNodeFaultParameters.groovy", EurostagTutorialExample1Factory.createWithLFResults(),
                        """
                        + DSL tests
                           + Groovy Event Models Supplier
                              + Model NodeFault NodeFault_NGEN instantiation KO
                                 faultTime should be strictly positive (0.0)
                        """),
                Arguments.of("/eventWarnings/missingAPVParameters.groovy",
                        LfResultsUtils.createSvcTestCaseWithLFResults(),
                        """
                        + DSL tests
                           + Groovy Event Models Supplier
                              + Model Step Step_SVC2 instantiation KO
                                 'staticId' field value 'SVC2' not found for equipment type(s) GENERATOR/LOAD
                                 'deltaP' field is not set
                        """),
                Arguments.of("/eventWarnings/missingDisconnectionEquipment.groovy", EurostagTutorialExample1Factory.createWithLFResults(),
                        """
                        + DSL tests
                           + Groovy Event Models Supplier
                              + Model Disconnect Disconnect_WRONG_ID instantiation KO
                                 'staticId' field value 'WRONG_ID' not found for equipment type(s) Disconnectable equipment
                        """),
                Arguments.of("/eventWarnings/missingDisconnectionSide.groovy", EurostagTutorialExample1Factory.createWithLFResults(),
                        """
                        + DSL tests
                           + Groovy Event Models Supplier
                              + Model Disconnect Disconnect_GEN instantiation KO
                                 'disconnectOnly' field is set but GENERATOR GEN does not possess this option
                        """)
        );
    }
}
