/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.suppliers.dynamicmodels.DynamicModelConfig;
import com.powsybl.dynawo.suppliers.dynamicmodels.DynamicModelConfigsJsonDeserializer;
import com.powsybl.dynawo.suppliers.dynamicmodels.DynawoModelsSupplier;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoModelsSupplierTest {

    @Test
    void testDynamicModelSupplier() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        List<DynamicModelConfig> modelConfigList = getModelConfigs();
        List<DynamicModel> models = new DynawoModelsSupplier(modelConfigList).get(network, ReportNode.NO_OP);

        DynamicModel gen = SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                .staticId("GEN")
                .parameterSetId("DM_GEN")
                .build();
        DynamicModel tc = TapChangerAutomationSystemBuilder.of(network)
                .dynamicModelId("TC")
                .parameterSetId("tc_par")
                .staticId("LOAD")
                .side(TransformerSide.LOW_VOLTAGE)
                .build();
        DynamicModel tcb = TapChangerBlockingAutomationSystemBuilder.of(network)
                .dynamicModelId("TCB1")
                .parameterSetId("tcb_par")
                .transformers("NGEN_NHV1", "NHV2_NLOAD")
                .uMeasurements(new Collection[]{List.of("OldNGen", "NGEN"), List.of("NHV1", "NHV2")})
                .build();
        DynamicModel tcb2 = TapChangerBlockingAutomationSystemBuilder.of(network)
                .dynamicModelId("TCB2")
                .parameterSetId("tcb_par")
                .transformers("NGEN_NHV2")
                .uMeasurements("NHV2")
                .build();

        assertEquals(4, models.size());
        assertThat(models.get(0)).usingRecursiveComparison().isEqualTo(gen);
        assertThat(models.get(1)).usingRecursiveComparison().isEqualTo(tc);
        assertThat(models.get(2)).usingRecursiveComparison().isEqualTo(tcb);
        assertThat(models.get(3)).usingRecursiveComparison().isEqualTo(tcb2);
    }

    @Test
    void testWrongNameBuilder() {
        Network network = EurostagTutorialExample1Factory.create();
        List<DynamicModelConfig> modelConfigList = List.of(
                new DynamicModelConfig("WrongName", "param", Collections.emptyList())
        );
        List<DynamicModel> models = new DynawoModelsSupplier(modelConfigList).get(network, ReportNode.NO_OP);
        assertTrue(models.isEmpty());
    }

    @Test
    void testSupplierFromPath() throws URISyntaxException {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        Path path = Path.of(Objects.requireNonNull(getClass().getResource("/suppliers/dynamicModels.json")).toURI());
        List<DynamicModel> models = DynawoModelsSupplier.load(path).get(network);
        assertEquals(2, models.size());
    }

    @Test
    void testModelConfigDeserializer() throws IOException {
        SupplierJsonDeserializer<DynamicModelConfig> deserializer = new SupplierJsonDeserializer<>(new DynamicModelConfigsJsonDeserializer());
        try (InputStream is = getClass().getResourceAsStream("/suppliers/dynamicModels.json")) {
            List<DynamicModelConfig> configs = deserializer.deserialize(is);
            assertEquals(2, configs.size());
            assertThat(configs.get(0)).usingRecursiveComparison().isEqualTo(getLoadConfig());
            assertThat(configs.get(1)).usingRecursiveComparison().isEqualTo(getTcbConfig());
        }
    }

    @Test
    void groupTypeException() {
        Network network = EurostagTutorialExample1Factory.create();
        List<Property> properties = List.of(
                new PropertyBuilder()
                        .name("propertyName")
                        .value("LOAD")
                        .type(PropertyType.STRING)
                        .build());
        DynamicModelConfig modelConfig = new DynamicModelConfig("LoadAlphaBeta", "_DM", SetGroupType.SUFFIX, properties);
        DynawoModelsSupplier supplier = new DynawoModelsSupplier(List.of(modelConfig));
        PowsyblException e = assertThrows(PowsyblException.class, () -> supplier.get(network));
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
        DynawoModelsSupplier supplier = new DynawoModelsSupplier(List.of(modelConfig));
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
                new DynamicModelConfig("TapChangerAutomaton", "tc_par", SetGroupType.FIXED, List.of(
                        new PropertyBuilder()
                                .name("dynamicModelId")
                                .value("TC")
                                .type(PropertyType.STRING)
                                .build(),
                        new PropertyBuilder()
                                .name("staticId")
                                .value("LOAD")
                                .type(PropertyType.STRING)
                                .build(),
                        new PropertyBuilder()
                                .name("side")
                                .value("LOW_VOLTAGE")
                                .type(PropertyType.STRING)
                                .build())),
                getTcbConfig(),
                getSimpleTcbConfig()
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
                        .type(PropertyType.STRING)
                        .build(),
                new PropertyBuilder()
                        .name("uMeasurements")
                        .arrays(List.of(List.of("OldNGen", "NGEN"), List.of("NHV1", "NHV2")))
                        .type(PropertyType.STRING)
                        .build()
        ));
    }

    private static DynamicModelConfig getSimpleTcbConfig() {
        return new DynamicModelConfig("TapChangerBlockingAutomaton", "tcb_par", SetGroupType.FIXED, List.of(
                new PropertyBuilder()
                        .name("dynamicModelId")
                        .value("TCB2")
                        .type(PropertyType.STRING)
                        .build(),
                new PropertyBuilder()
                        .name("transformers")
                        .value("NGEN_NHV2")
                        .type(PropertyType.STRING)
                        .build(),
                new PropertyBuilder()
                        .name("uMeasurements")
                        .value("NHV2")
                        .type(PropertyType.STRING)
                        .build()
        ));
    }
}
