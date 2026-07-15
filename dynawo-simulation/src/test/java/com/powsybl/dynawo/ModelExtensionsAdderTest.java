/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.dynawo.extensions.api.info.DynawoEquipmentModelInfo;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ModelExtensionsAdderTest {

    @Test
    void addExtensions() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        List<BlackBoxModel> dynamicModels = List.of(
                SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                        .staticId("GEN")
                        .build());
        new ModelExtensionsAdder(network, dynamicModels).addModelExtensions();
        DynawoEquipmentModelInfo<Generator> modelInfo = network.getGenerator("GEN").getExtension(DynawoEquipmentModelInfo.class);
        assertEquals("GeneratorPQ", modelInfo.getModelName());
    }
}
