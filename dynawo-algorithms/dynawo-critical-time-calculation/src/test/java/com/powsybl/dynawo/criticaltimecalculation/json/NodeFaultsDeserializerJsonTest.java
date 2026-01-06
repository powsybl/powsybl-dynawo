/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawo.criticaltimecalculation.json;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultEventData;
import com.powsybl.dynawo.suppliers.SupplierJsonDeserializer;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class NodeFaultsDeserializerJsonTest {

    @Test
    void testNodeFaults() throws IOException {
        Network network = EurostagTutorialExample1Factory.createWithMultipleConnectedComponents();
        try (var is = getClass().getResourceAsStream("/CriticalTimeCalculationNodeFaults.json")) {
            List<NodeFaultEventData> nodeFaultsList = new SupplierJsonDeserializer<>(
                    new CriticalTimeCalculationNodeFaultsJsonDeserializer(() -> new NodeFaultEventData.Builder(network, ReportNode.NO_OP)))
                    .deserialize(is);

            assertThat(nodeFaultsList).usingRecursiveFieldByFieldElementComparatorOnFields()
                    .containsExactlyInAnyOrderElementsOf(getNodeFaultsListFromEventData(network));
        }

    }

    private static List<NodeFaultEventData> getNodeFaultsListFromEventData(Network network) {
        return List.of(
                new NodeFaultEventData.Builder(network)
                        .setStaticId("NGEN")
                        .setFaultStartTime(1)
                        .setFaultStopTime(2)
                        .setFaultXPu(0.5)
                        .setFaultRPu(0.5)
                        .build(),
                new NodeFaultEventData.Builder(network)
                        .setStaticId("N1")
                        .setFaultStartTime(1)
                        .setFaultStopTime(2)
                        .setFaultXPu(0.001)
                        .setFaultRPu(0.001)
                        .build()
        );
    }
}
