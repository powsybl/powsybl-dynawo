/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.powsybl.iidm.network.test.EurostagTutorialExample1Factory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ContingencyEventModelsTest {

    @Test
    void test() {
        Network network = EurostagTutorialExample1Factory.create();
        BlackBoxModelSupplier bbmSupplier = new BlackBoxModelSupplier() {

            private final Map<String, EquipmentBlackBoxModel> equipments = Map.of("GEN",
                    BaseGeneratorBuilder.of(network)
                            .staticId("GEN")
                            .parameterSetId("gen")
                            .build());

            @Override
            public EquipmentBlackBoxModel getStaticIdBlackBoxModel(String id) {
                return equipments.get(id);
            }

            @Override
            public BlackBoxModel getPureDynamicModel(String id) {
                return null;
            }

            @Override
            public boolean hasDynamicModel(Identifiable<?> equipment) {
                return equipments.containsKey(equipment.getId());
            }
        };
        List<Contingency> contingencies = List.of(
                Contingency.load("LOAD"),
                Contingency.generator("GEN"),
                Contingency.line(NHV1_NHV2_1, VLHV1),
                Contingency.branch(NHV1_NHV2_2, "WRONG_ID"),
                Contingency.battery("BATTERY"));

        List<ContingencyEventModels> contingencyEvents = ContingencyEventModelsFactory.createFrom(contingencies,
                2, network, bbmSupplier, ReportNode.NO_OP);
        assertThat(contingencyEvents).hasSize(3);
        assertThat(contingencyEvents.get(0).eventModels())
                .hasSize(1)
                .map(BlackBoxModel::getLib).containsExactly("EventConnectedStatus");
        assertThat(contingencyEvents.get(1).eventModels())
                .hasSize(1)
                .map(BlackBoxModel::getLib).containsExactly("EventSetPointBoolean");
        assertThat(contingencyEvents.get(2).eventModels())
                .hasSize(1)
                .map(BlackBoxModel::getLib).containsExactly("EventQuadripoleDisconnection");
        ParametersSet parametersSet = contingencyEvents.get(2).eventParameters().get(0);
        assertTrue(parametersSet.getBool("event_disconnectOrigin"));
        assertFalse(parametersSet.getBool("event_disconnectExtremity"));
    }
}
