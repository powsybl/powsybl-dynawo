/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawaltz.suppliers.dynamicmodels.DynamicModelConfig;
import com.powsybl.dynawaltz.suppliers.dynamicmodels.*;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoDynamicModelsSuppliersTest {

    @Test
    void testDynamicModelSupplier() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        List<DynamicModelConfig> modelConfigList = getModelConfigs();
        List<DynamicModel> models = new DynawoDynamicModelSupplier(modelConfigList).get(network, ReportNode.NO_OP);

        DynamicModel gen = SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                .staticId("GEN")
                .parameterSetId("DM_GEN")
                .build();
        DynamicModel tcb = TapChangerBlockingAutomationSystemBuilder.of(network)
                .dynamicModelId("TCB1")
                .parameterSetId("tcb_par")
                .transformers("NGEN_NHV1", "NHV2_NLOAD")
                .uMeasurements(new Collection[]{List.of("OldNGen", "NGEN"), List.of("NHV1", "NHV2")})
                .build();

        assertEquals(2, models.size());
        assertThat(models.get(0)).usingRecursiveComparison().isEqualTo(gen);
        assertThat(models.get(1)).usingRecursiveComparison().isEqualTo(tcb);
    }

    @Test
    void testWrongNameBuilder() {
        Network network = EurostagTutorialExample1Factory.create();
        List<DynamicModelConfig> modelConfigList = List.of(
                new DynamicModelConfig("WrongName", "param", Collections.emptyList())
        );
        List<DynamicModel> models = new DynawoDynamicModelSupplier(modelConfigList).get(network, ReportNode.NO_OP);
        assertTrue(models.isEmpty());
    }

    @Test
    void testModelConfigDeserializer() throws IOException {
        ObjectMapper objectMapper = setupObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/suppliers/mappingDynamicModel.json")) {
            List<DynamicModelConfig> configs = objectMapper.readValue(is, new TypeReference<>() {
            });
            assertEquals(2, configs.size());
            assertThat(configs.get(0)).usingRecursiveComparison().isEqualTo(getLoadConfig());
            assertThat(configs.get(1)).usingRecursiveComparison().isEqualTo(getTcbConfig());
        }
    }

    @Test
    void groupTypeException() {
        PowsyblException e = assertThrows(PowsyblException.class, () -> new DynamicModelConfig("LoadAlphaBeta", "_DM", SetGroupType.SUFFIX, List.of(
                new PropertyBuilder()
                        .name("propertyName")
                        .value("LOAD")
                        .type(PropertyType.STRING)
                        .build())));
        assertEquals("No ID found for parameter set id", e.getMessage());
    }

    @Test
    void wrongPropertyException() {
        Network network = EurostagTutorialExample1Factory.create();
        DynamicModelConfig modelConfig = new DynamicModelConfig("LoadAlphaBeta", "LAB", List.of(
                new PropertyBuilder()
                        .name("wrongName")
                        .value("LOAD")
                        .type(PropertyType.STRING)
                        .build()
        ));
        DynawoDynamicModelSupplier supplier = new DynawoDynamicModelSupplier(List.of(modelConfig));
        Exception e = assertThrows(PowsyblException.class, () -> supplier.get(network, ReportNode.NO_OP));
        assertEquals("Method wrongName not found for parameter LOAD on builder BaseLoadBuilder", e.getMessage());
    }

    private static List<DynamicModelConfig> getModelConfigs() {
        return List.of(
                new DynamicModelConfig("GeneratorPQ", "DM_", SetGroupType.PREFIX, List.of(
                        new PropertyBuilder()
                                .name("staticId")
                                .value("GEN")
                                .type(PropertyType.STRING)
                                .build())),
                getTcbConfig()
        );
    }

    private static DynamicModelConfig getLoadConfig() {
        return new DynamicModelConfig("LoadAlphaBeta", "_DM", SetGroupType.SUFFIX, List.of(
                new PropertyBuilder()
                        .name("staticId")
                        .value("LOAD")
                        .type(PropertyType.STRING)
                        .build()));
    }

    private static DynamicModelConfig getTcbConfig() {
        return new DynamicModelConfig("TapChangerBlockingAutomaton", "tcb_par", SetGroupType.FIXED, List.of(
                new PropertyBuilder()
                        .name("dynamicModelId")
                        .value("TCB1")
                        .type(PropertyType.STRING)
                        .build(),
                new PropertyBuilder()
                        .name("transformers")
                        .values(List.of("NGEN_NHV1", "NHV2_NLOAD"))
                        .type(PropertyType.STRINGS)
                        .build(),
                new PropertyBuilder()
                        .name("uMeasurements")
                        .arrays(List.of(List.of("OldNGen", "NGEN"), List.of("NHV1", "NHV2")))
                        .type(PropertyType.STRINGS_ARRAYS)
                        .build()
        ));
    }

    private static ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(List.class, new DynamicModelConfigsJsonDeserializer());
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
