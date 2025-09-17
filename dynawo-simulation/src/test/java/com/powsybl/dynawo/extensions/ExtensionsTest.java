/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.extensions.DynamicModelInfo;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ExtensionsTest {

    @Test
    void getExtensions() {
        Network network = EurostagTutorialExample1Factory.create();
        Generator gen = network.getGenerator("GEN");
        List<BlackBoxModel> dynamicModels = List.of(
                BaseGeneratorBuilder.of(network, "GeneratorFictitious")
                        .equipment(gen)
                        .parameterSetId("gen")
                        .build()
        );
        new DynawoSimulationContext.Builder(network, dynamicModels).build();

        assertThat(gen.getExtensions()).extracting(e -> ((DynamicModelInfo<?>) e).getModelName())
                .containsExactly("GeneratorFictitious");

    }
}
