/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.Versionable;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawo.margincalculation.loadsvariation.supplier.LoadsVariationSupplier;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationResult;
import com.powsybl.iidm.network.Network;

import java.util.concurrent.CompletableFuture;

/**
 * Margin calculation main API. It is a utility class (so with only static methods) used as an entry point for running
 * a margin calculation (Dynawo being the only implementation).
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class MarginCalculation {

    private MarginCalculation() {
        throw new IllegalStateException("Utility class should not be instantiated");
    }

    /**
     * A margin calculation runner is responsible for providing convenient methods on top of {@link MarginCalculationProvider}:
     * several variants of synchronous and asynchronous run with default parameters.
     */
    public static final class Runner implements Versionable {

        private final MarginCalculationProvider provider;

        public Runner() {
            provider = new MarginCalculationProvider();
        }

        public CompletableFuture<MarginCalculationResult> runAsync(Network network, String workingVariantId,
                                                              DynamicModelsSupplier dynamicModelsSupplier,
                                                              ContingenciesProvider contingenciesProvider,
                                                              LoadsVariationSupplier loadsVariationSupplier,
                                                              MarginCalculationRunParameters runParameters) {
            return provider.run(network, workingVariantId, dynamicModelsSupplier, contingenciesProvider,
                    loadsVariationSupplier, runParameters);
        }

        public CompletableFuture<MarginCalculationResult> runAsync(Network network,
                                                                   DynamicModelsSupplier dynamicModelsSupplier,
                                                                   ContingenciesProvider contingenciesProvider,
                                                                   LoadsVariationSupplier loadsVariationSupplier,
                                                                   MarginCalculationRunParameters runParameters) {
            return provider.run(network, network.getVariantManager().getWorkingVariantId(), dynamicModelsSupplier, contingenciesProvider,
                    loadsVariationSupplier, runParameters);
        }

        public CompletableFuture<MarginCalculationResult> runAsync(Network network,
                                                                   DynamicModelsSupplier dynamicModelsSupplier,
                                                                   ContingenciesProvider contingenciesProvider,
                                                                   LoadsVariationSupplier loadsVariationSupplier) {
            return provider.run(network, network.getVariantManager().getWorkingVariantId(), dynamicModelsSupplier, contingenciesProvider,
                    loadsVariationSupplier, MarginCalculationRunParameters.getDefault());
        }

        public MarginCalculationResult run(Network network, String workingVariantId,
                                           DynamicModelsSupplier dynamicModelsSupplier,
                                           ContingenciesProvider contingenciesProvider,
                                           LoadsVariationSupplier loadsVariationSupplier,
                                           MarginCalculationRunParameters runParameters) {
            return runAsync(network, workingVariantId, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier,
                    runParameters).join();
        }

        public MarginCalculationResult run(Network network,
                                           DynamicModelsSupplier dynamicModelsSupplier,
                                           ContingenciesProvider contingenciesProvider,
                                           LoadsVariationSupplier loadsVariationSupplier,
                                           MarginCalculationRunParameters runParameters) {
            return runAsync(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier,
                    runParameters).join();
        }

        public MarginCalculationResult run(Network network,
                                           DynamicModelsSupplier dynamicModelsSupplier,
                                           ContingenciesProvider contingenciesProvider,
                                           LoadsVariationSupplier loadsVariationSupplier) {
            return runAsync(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier).join();
        }

        @Override
        public String getName() {
            return provider.getName();
        }

        @Override
        public String getVersion() {
            return provider.getVersion();
        }
    }

    public static Runner getRunner() {
        return new Runner();
    }

    public static CompletableFuture<MarginCalculationResult> runAsync(Network network, String workingVariantId,
                                                               DynamicModelsSupplier dynamicModelsSupplier,
                                                               ContingenciesProvider contingenciesProvider,
                                                               LoadsVariationSupplier loadsVariationSupplier,
                                                               MarginCalculationRunParameters runParameters) {
        return new Runner().runAsync(network, workingVariantId, dynamicModelsSupplier, contingenciesProvider,
                loadsVariationSupplier, runParameters);
    }

    public static CompletableFuture<MarginCalculationResult> runAsync(Network network,
                                                               DynamicModelsSupplier dynamicModelsSupplier,
                                                               ContingenciesProvider contingenciesProvider,
                                                               LoadsVariationSupplier loadsVariationSupplier,
                                                               MarginCalculationRunParameters runParameters) {
        return new Runner().runAsync(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier, runParameters);
    }

    public static CompletableFuture<MarginCalculationResult> runAsync(Network network,
                                                               DynamicModelsSupplier dynamicModelsSupplier,
                                                               ContingenciesProvider contingenciesProvider,
                                                               LoadsVariationSupplier loadsVariationSupplier) {
        return new Runner().runAsync(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier);
    }

    public static MarginCalculationResult run(Network network, String workingVariantId,
                                       DynamicModelsSupplier dynamicModelsSupplier,
                                       ContingenciesProvider contingenciesProvider,
                                       LoadsVariationSupplier loadsVariationSupplier,
                                       MarginCalculationRunParameters runParameters) {
        return new Runner().run(network, workingVariantId, dynamicModelsSupplier, contingenciesProvider,
                loadsVariationSupplier, runParameters);
    }

    public static MarginCalculationResult run(Network network,
                                       DynamicModelsSupplier dynamicModelsSupplier,
                                       ContingenciesProvider contingenciesProvider,
                                       LoadsVariationSupplier loadsVariationSupplier,
                                       MarginCalculationRunParameters runParameters) {
        return new Runner().run(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier, runParameters);
    }

    public static MarginCalculationResult run(Network network,
                                       DynamicModelsSupplier dynamicModelsSupplier,
                                       ContingenciesProvider contingenciesProvider,
                                       LoadsVariationSupplier loadsVariationSupplier) {
        return new Runner().run(network, dynamicModelsSupplier, contingenciesProvider, loadsVariationSupplier);
    }
}
