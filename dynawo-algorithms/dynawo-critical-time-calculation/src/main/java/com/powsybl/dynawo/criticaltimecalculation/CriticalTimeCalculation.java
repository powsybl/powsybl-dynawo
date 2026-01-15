/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation;

import com.powsybl.commons.Versionable;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultsProvider;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResults;

import com.powsybl.iidm.network.Network;

import java.util.concurrent.CompletableFuture;

/**
 * Critical time Calculation main API. It is a utility class (so with only static methods) used as an entry point for running
 * a critical time calculation (Dynawo being the only implementation).
 *
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class CriticalTimeCalculation {
    private CriticalTimeCalculation() {
        throw new IllegalStateException("Utility class should not be instantiated");
    }

    /**
     * A critical time calculation runner is responsible for providing convenient methods on top of {@link CriticalTimeCalculationProvider}:
     * several variants of synchronous and asynchronous run with default parameters.
     */
    public static final class Runner implements Versionable {

        private final CriticalTimeCalculationProvider provider;

        public Runner() {
            provider = new CriticalTimeCalculationProvider();
        }

        public CompletableFuture<CriticalTimeCalculationResults> runAsync(Network network, String workingVariantId,
                                                                          DynamicModelsSupplier dynamicModelsSupplier,
                                                                          NodeFaultsProvider nodeFaultsProvider,
                                                                          CriticalTimeCalculationRunParameters runParameters) {
            return provider.run(network, workingVariantId, dynamicModelsSupplier, nodeFaultsProvider, runParameters);
        }

        public CompletableFuture<CriticalTimeCalculationResults> runAsync(Network network,
                                                                          DynamicModelsSupplier dynamicModelsSupplier,
                                                                          NodeFaultsProvider nodeFaultsProvider,
                                                                          CriticalTimeCalculationRunParameters runParameters) {
            return provider.run(network, network.getVariantManager().getWorkingVariantId(), dynamicModelsSupplier, nodeFaultsProvider,
                    runParameters);
        }

        public CompletableFuture<CriticalTimeCalculationResults> runAsync(Network network,
                                                                          DynamicModelsSupplier dynamicModelsSupplier,
                                                                          NodeFaultsProvider nodeFaultsProvider) {
            return provider.run(network, network.getVariantManager().getWorkingVariantId(), dynamicModelsSupplier, nodeFaultsProvider,
                    CriticalTimeCalculationRunParameters.getDefault());
        }

        public CriticalTimeCalculationResults run(Network network, String workingVariantId,
                                                  DynamicModelsSupplier dynamicModelsSupplier,
                                                  NodeFaultsProvider nodeFaultsProvider,
                                                  CriticalTimeCalculationRunParameters runParameters) {
            return runAsync(network, workingVariantId, dynamicModelsSupplier, nodeFaultsProvider,
                    runParameters).join();
        }

        public CriticalTimeCalculationResults run(Network network,
                                                  DynamicModelsSupplier dynamicModelsSupplier,
                                                  NodeFaultsProvider nodeFaultsProvider,
                                                  CriticalTimeCalculationRunParameters runParameters) {
            return runAsync(network, dynamicModelsSupplier, nodeFaultsProvider, runParameters).join();
        }

        public CriticalTimeCalculationResults run(Network network,
                                                  DynamicModelsSupplier dynamicModelsSupplier,
                                                  NodeFaultsProvider nodeFaultsProvider) {
            return runAsync(network, dynamicModelsSupplier, nodeFaultsProvider).join();
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

    public static CompletableFuture<CriticalTimeCalculationResults> runAsync(Network network, String workingVariantId,
                                                                             DynamicModelsSupplier dynamicModelsSupplier,
                                                                             NodeFaultsProvider nodeFaultsProvider,
                                                                             CriticalTimeCalculationRunParameters runParameters) {
        return new Runner().runAsync(network, workingVariantId, dynamicModelsSupplier, nodeFaultsProvider, runParameters);
    }

    public static CompletableFuture<CriticalTimeCalculationResults> runAsync(Network network,
                                                                             DynamicModelsSupplier dynamicModelsSupplier,
                                                                             NodeFaultsProvider nodeFaultsProvider,
                                                                             CriticalTimeCalculationRunParameters runParameters) {
        return new Runner().runAsync(network, dynamicModelsSupplier, nodeFaultsProvider, runParameters);
    }

    public static CompletableFuture<CriticalTimeCalculationResults> runAsync(Network network,
                                                                             DynamicModelsSupplier dynamicModelsSupplier,
                                                                             NodeFaultsProvider nodeFaultsProvider) {
        return new Runner().runAsync(network, dynamicModelsSupplier, nodeFaultsProvider);
    }

    public static CriticalTimeCalculationResults run(Network network, String workingVariantId,
                                                     DynamicModelsSupplier dynamicModelsSupplier,
                                                     NodeFaultsProvider nodeFaultsProvider,
                                                     CriticalTimeCalculationRunParameters runParameters) {
        return new Runner().run(network, workingVariantId, dynamicModelsSupplier, nodeFaultsProvider, runParameters);
    }

    public static CriticalTimeCalculationResults run(Network network,
                                                     DynamicModelsSupplier dynamicModelsSupplier,
                                                     NodeFaultsProvider nodeFaultsProvider,
                                                     CriticalTimeCalculationRunParameters runParameters) {
        return new Runner().run(network, dynamicModelsSupplier, nodeFaultsProvider, runParameters);
    }

    public static CriticalTimeCalculationResults run(Network network,
                                                     DynamicModelsSupplier dynamicModelsSupplier,
                                                     NodeFaultsProvider nodeFaultsProvider) {
        return new Runner().run(network, dynamicModelsSupplier, nodeFaultsProvider);
    }
}
