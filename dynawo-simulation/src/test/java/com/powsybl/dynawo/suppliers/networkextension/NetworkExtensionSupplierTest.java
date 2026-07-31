/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.networkextension;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.extensions.api.model.DynawoEquipmentModelAdder;
import com.powsybl.dynawo.extensions.api.model.DynawoUnderVoltageModelAdder;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class NetworkExtensionSupplierTest {

    @Test
    void testSupplier() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        Generator generator = network.getGenerator("GEN");

        network.getLoad("LOAD").newExtension(DynawoEquipmentModelAdder.class)
                .setModelName("LoadAlphaBeta")
                .setParameterSetId("LAB")
                .add();
        generator.newExtension(DynawoEquipmentModelAdder.class)
                .setModelName("GeneratorPV")
                .setParameterSetId("GPV")
                .add();
        generator.newExtension(DynawoUnderVoltageModelAdder.class)
                .setModelName("UnderVoltage")
                .setDynamicModelId("UVA")
                .setParameterSetId("UVA")
                .add();

        DynamicModel expectedLoad = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId("LOAD")
                .parameterSetId("LAB")
                .build();
        DynamicModel expectedGen = SynchronizedGeneratorBuilder.of(network, "GeneratorPV")
                .staticId("GEN")
                .parameterSetId("GPV")
                .build();
        DynamicModel expectedUva = UnderVoltageAutomationSystemBuilder.of(network, "UnderVoltage")
                .dynamicModelId("UVA")
                .parameterSetId("UVA")
                .generator("GEN")
                .build();

        List<DynamicModel> dynamicModels = new NetworkExtensionSupplier().get(network);
        assertThat(dynamicModels).hasSize(3).satisfiesExactlyInAnyOrder(
                load -> assertThat(load).usingRecursiveComparison().isEqualTo(expectedLoad),
                gen -> assertThat(gen).usingRecursiveComparison().isEqualTo(expectedGen),
                uva -> assertThat(uva).usingRecursiveComparison().isEqualTo(expectedUva));
    }
}
