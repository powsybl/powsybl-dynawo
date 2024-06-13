/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawaltz.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawaltz.suppliers.events.*;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoEventModelsSupplierTest {

    @Test
    void testEventSupplier() {
        Network network = EurostagTutorialExample1Factory.create();
        List<EventModelConfig> eventModelConfigList = getEventConfigs();
        List<EventModel> events = new DynawoEventModelsSupplier(eventModelConfigList).get(network, ReportNode.NO_OP);

        EventModel disconnection = EventDisconnectionBuilder.of(network)
                .staticId("NHV1_NHV2_2")
                .startTime(1)
                .disconnectOnly(TwoSides.TWO)
                .build();
        EventModel step = EventActivePowerVariationBuilder.of(network)
                .staticId("LOAD")
                .startTime(1)
                .deltaP(20)
                .build();

        assertEquals(2, events.size());
        assertThat(events.get(0)).usingRecursiveComparison().isEqualTo(disconnection);
        assertThat(events.get(1)).usingRecursiveComparison().isEqualTo(step);
    }

    @Test
    void testWrongNameBuilder() {
        Network network = EurostagTutorialExample1Factory.create();
        List<EventModelConfig> eventModelConfigList = List.of(
                new EventModelConfig("WrongName", Collections.emptyList())
        );
        List<EventModel> events = new DynawoEventModelsSupplier(eventModelConfigList).get(network, ReportNode.NO_OP);
        assertTrue(events.isEmpty());
    }

    @Test
    void testSupplierFromPath() throws URISyntaxException {
        Network network = EurostagTutorialExample1Factory.create();
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/suppliers/mappingEvent.json")).toURI());
        List<EventModel> models = new DynawoEventModelsSupplier(path).get(network);
        assertEquals(1, models.size());
    }

    @Test
    void testEventModelConfigDeserializer() throws IOException {
        SupplierJsonDeserializer<EventModelConfig> deserializer = new SupplierJsonDeserializer<>(new EventModelConfigsJsonDeserializer());
        try (InputStream is = getClass().getResourceAsStream("/suppliers/mappingEvent.json")) {
            List<EventModelConfig> configs = deserializer.deserialize(is);
            assertEquals(1, configs.size());
            assertThat(configs.get(0)).usingRecursiveComparison().isEqualTo(getActivePowerVariationConfig());
        }
    }

    @Test
    void wrongPropertyException() {
        Network network = EurostagTutorialExample1Factory.create();
        EventModelConfig eventModelConfig = new EventModelConfig("Disconnect", List.of(
                new PropertyBuilder()
                        .name("wrongName")
                        .value("NHV1_NHV2_2")
                        .type(PropertyType.STRING)
                        .build()
        ));
        DynawoEventModelsSupplier supplier = new DynawoEventModelsSupplier(List.of(eventModelConfig));
        Exception e = assertThrows(PowsyblException.class, () -> supplier.get(network, ReportNode.NO_OP));
        assertEquals("Method wrongName not found for parameter NHV1_NHV2_2 on builder EventDisconnectionBuilder", e.getMessage());
    }

    private static List<EventModelConfig> getEventConfigs() {
        return List.of(
                new EventModelConfig("Disconnect", List.of(
                        new PropertyBuilder()
                                .name("staticId")
                                .value("NHV1_NHV2_2")
                                .type(PropertyType.STRING)
                                .build(),
                        new PropertyBuilder()
                                .name("startTime")
                                .value("1")
                                .type(PropertyType.DOUBLE)
                                .build(),
                        new PropertyBuilder()
                                .name("disconnectOnly")
                                .value("TWO")
                                .type(PropertyType.TWO_SIDES)
                                .build()
                )),
                getActivePowerVariationConfig()
        );
    }

    private static EventModelConfig getActivePowerVariationConfig() {
        return new EventModelConfig("Step", List.of(
                new PropertyBuilder()
                        .name("staticId")
                        .value("LOAD")
                        .type(PropertyType.STRING)
                        .build(),
                new PropertyBuilder()
                        .name("startTime")
                        .value("1")
                        .type(PropertyType.DOUBLE)
                        .build(),
                new PropertyBuilder()
                        .name("deltaP")
                        .value("20")
                        .type(PropertyType.DOUBLE)
                        .build()));
    }
}
