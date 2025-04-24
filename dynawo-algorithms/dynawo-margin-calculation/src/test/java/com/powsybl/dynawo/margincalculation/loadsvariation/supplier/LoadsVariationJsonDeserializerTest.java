/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.loadsvariation.supplier;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariationBuilder;
import com.powsybl.dynawo.suppliers.SupplierJsonDeserializer;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class LoadsVariationJsonDeserializerTest {

    @Test
    void testLoadVariationSupplier() throws IOException {
        Network network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        try (InputStream is = getClass().getResourceAsStream("/load_variations.json")) {
            List<LoadsVariation> loadsVariations = new SupplierJsonDeserializer<>(
                    new LoadsVariationJsonDeserializer(() -> new LoadsVariationBuilder(network, ReportNode.NO_OP)))
                    .deserialize(is);
            assertThat(loadsVariations).usingRecursiveFieldByFieldElementComparatorOnFields()
                    .containsExactlyInAnyOrderElementsOf(getExpectedLoadsVariations(network));
        }
    }

    private static List<LoadsVariation> getExpectedLoadsVariations(Network network) {
        return List.of(
            new LoadsVariationBuilder(network, ReportNode.NO_OP)
                    .loads("LOAD", "LOAD2")
                    .variationValue(20)
                    .build());
    }
}
