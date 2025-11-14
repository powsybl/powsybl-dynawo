/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation;

import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultsProvider;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResults;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationTest {
    private static final MockedConstruction.MockInitializer<CriticalTimeCalculationProvider> MOCK_INITIALIZER =
            (mock, context) -> when(mock.run(any(), any(), any(), any(), any()))
                    .thenReturn(CompletableFuture.completedFuture(CriticalTimeCalculationResults.empty()));

    private static Network network;
    private static String variantId;
    private static DynamicModelsSupplier dynamicModelsSupplier;
    private static NodeFaultsProvider nodeFaultsProvider;
    private static CriticalTimeCalculationRunParameters runParameters;

    @BeforeAll
    static void setup() {
        network = Mockito.mock(Network.class);
        VariantManager variantManager = Mockito.mock(VariantManager.class);
        Mockito.when(network.getVariantManager()).thenReturn(variantManager);
        Mockito.when(variantManager.getWorkingVariantId()).thenReturn("v");
        variantId = "";
        dynamicModelsSupplier = Mockito.mock(DynamicModelsSupplier.class);
        nodeFaultsProvider = Mockito.mock(NodeFaultsProvider.class);
        runParameters = Mockito.mock(CriticalTimeCalculationRunParameters.class);
    }

    @Test
    void testRunCombinations() {
        try (MockedConstruction<CriticalTimeCalculationProvider> provider = Mockito.mockConstruction(CriticalTimeCalculationProvider.class, MOCK_INITIALIZER)) {
            assertNotNull(CriticalTimeCalculation.run(network, variantId, dynamicModelsSupplier, nodeFaultsProvider, runParameters));
            assertNotNull(CriticalTimeCalculation.run(network, dynamicModelsSupplier, nodeFaultsProvider, runParameters));
            assertNotNull(CriticalTimeCalculation.run(network, dynamicModelsSupplier, nodeFaultsProvider));
        }
    }

    @Test
    void testRunAsyncCombinations() {
        try (MockedConstruction<CriticalTimeCalculationProvider> provider = Mockito.mockConstruction(CriticalTimeCalculationProvider.class, MOCK_INITIALIZER)) {
            assertNotNull(CriticalTimeCalculation.runAsync(network, variantId, dynamicModelsSupplier, nodeFaultsProvider, runParameters));
            assertNotNull(CriticalTimeCalculation.runAsync(network, dynamicModelsSupplier, nodeFaultsProvider, runParameters));
            assertNotNull(CriticalTimeCalculation.runAsync(network, dynamicModelsSupplier, nodeFaultsProvider));
        }
    }
}
