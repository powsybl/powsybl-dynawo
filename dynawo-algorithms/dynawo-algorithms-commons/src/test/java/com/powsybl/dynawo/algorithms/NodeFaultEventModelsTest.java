/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class NodeFaultEventModelsTest {

    @Test
    void test() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();
        BlackBoxModelSupplier bbmSupplier = BlackBoxModelSupplier.createFrom(List.of(
                BaseGeneratorBuilder.of(network)
                        .staticId("GEN")
                        .parameterSetId("gen")
                        .build()));

        List<List<NodeFaultEventData>> nodeFaults = List.of(
                List.of(new NodeFaultEventData.Builder()
                        .setStaticId("NGEN")
                        .setFaultStartTime(1)
                        .setFaultStopTime(5)
                        .setFaultXPu(0.001)
                        .setFaultRPu(0.001)
                        .build()),
                List.of(new NodeFaultEventData.Builder()
                        .setStaticId("VLGEN_0")
                        .setFaultStartTime(1)
                        .setFaultStopTime(5)
                        .setFaultXPu(0.001)
                        .setFaultRPu(0.001)
                        .build())
        );

        List<NodeFaultEventModels> nodeFaultEventModels = NodeFaultEventModelsFactory.createFrom(nodeFaults,
                network, bbmSupplier,
                n -> n.equalsIgnoreCase("CTC_EventNodeFault"),
                ReportNode.NO_OP);

        assertThat(nodeFaultEventModels).hasSize(1);
    }
}
