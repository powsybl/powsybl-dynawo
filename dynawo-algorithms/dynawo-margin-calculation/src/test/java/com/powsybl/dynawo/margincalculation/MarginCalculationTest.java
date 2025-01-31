/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawo.margincalculation.loadsvariation.supplier.LoadsVariationSupplier;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationResult;
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
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class MarginCalculationTest {

    private static final MockedConstruction.MockInitializer<MarginCalculationProvider> MOCK_INITIALIZER =
            (mock, context) -> when(mock.run(any(), any(), any(), any(), any(), any()))
                    .thenReturn(CompletableFuture.completedFuture(MarginCalculationResult.empty()));

    private static Network network;
    private static String variantId;
    private static DynamicModelsSupplier dynamicModelsSupplier;
    private static ContingenciesProvider contingenciesProvider;
    private static LoadsVariationSupplier loadsVariationSupplier;
    private static MarginCalculationRunParameters runParameters;

    @BeforeAll
    static void setup() {
        network = Mockito.mock(Network.class);
        VariantManager variantManager = Mockito.mock(VariantManager.class);
        Mockito.when(network.getVariantManager()).thenReturn(variantManager);
        Mockito.when(variantManager.getWorkingVariantId()).thenReturn("v");
        variantId = "";
        dynamicModelsSupplier = Mockito.mock(DynamicModelsSupplier.class);
        contingenciesProvider = Mockito.mock(ContingenciesProvider.class);
        loadsVariationSupplier = Mockito.mock(LoadsVariationSupplier.class);
        runParameters = Mockito.mock(MarginCalculationRunParameters.class);
    }

    @Test
    void testRunCombinations() {
        try (MockedConstruction<MarginCalculationProvider> provider = Mockito.mockConstruction(MarginCalculationProvider.class, MOCK_INITIALIZER)) {
            assertNotNull(MarginCalculation.run(network, variantId, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier, runParameters));
            assertNotNull(MarginCalculation.run(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier, runParameters));
            assertNotNull(MarginCalculation.run(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier));
        }
    }

    @Test
    void testRunAsyncCombinations() {
        try (MockedConstruction<MarginCalculationProvider> provider = Mockito.mockConstruction(MarginCalculationProvider.class, MOCK_INITIALIZER)) {
            assertNotNull(MarginCalculation.runAsync(network, variantId, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier, runParameters));
            assertNotNull(MarginCalculation.runAsync(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier, runParameters));
            assertNotNull(MarginCalculation.runAsync(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier));
        }
    }

}
