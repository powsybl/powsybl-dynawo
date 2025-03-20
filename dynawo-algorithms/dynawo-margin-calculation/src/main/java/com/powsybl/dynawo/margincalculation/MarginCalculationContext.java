/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.*;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadVariationAreaAutomationSystem;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsProportionalScalable;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.loads.AbstractLoad;
import com.powsybl.dynawo.algorithms.ContingencyEventModels;
import com.powsybl.dynawo.algorithms.ContingencyEventModelsFactory;
import com.powsybl.dynawo.xml.DydDataSupplier;
import com.powsybl.iidm.modification.scalable.Scalable;
import com.powsybl.iidm.modification.scalable.ScalingParameters;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class MarginCalculationContext extends DynawoSimulationContext {

    private final MarginCalculationParameters marginCalculationParameters;
    private final List<ContingencyEventModels> contingencyEventModels;
    private final LoadVariationModels loadVariationModels;

    public static class Builder extends AbstractContextBuilder<Builder> {

        private final List<Contingency> contingencies;
        private final List<LoadsVariation> loadsVariations;
        private MarginCalculationParameters parameters;
        private LoadVariationModels loadVariationModels;
        private List<ContingencyEventModels> contingencyEventModels;

        public Builder(Network network, List<BlackBoxModel> dynamicModels, List<Contingency> contingencies,
                       List<LoadsVariation> loadsVariations) {
            super(network, dynamicModels);
            this.contingencies = contingencies;
            this.loadsVariations = loadsVariations;
        }

        public Builder marginCalculationParameters(MarginCalculationParameters parameters) {
            this.parameters = Objects.requireNonNull(parameters);
            return self();
        }

        @Override
        protected void setupData() {
            if (parameters == null) {
                parameters = MarginCalculationParameters.load();
            }
            finalStepConfig = configureFinalStep(parameters, loadsVariations);
            super.setupData();
        }

        @Override
        protected void setupMacroConnections() {
            super.setupMacroConnections();
            setupLoadVariationModels();
            setupContingencyEventModels();
        }

        @Override
        protected void setupSimulationTime() {
            this.simulationTime = new SimulationTime(parameters.getStartTime(), parameters.getMarginCalculationStartTime());
            this.finalStepTime = new SimulationTime(simulationTime.stopTime(), finalStepConfig.stopTime());
        }

        private void setupLoadVariationModels() {
            LoadVariationAreaAutomationSystem loadVariationArea = new LoadVariationAreaAutomationSystem(loadsVariations,
                    parameters.getLoadIncreaseStartTime(),
                    parameters.getLoadIncreaseStopTime(),
                    configureScaling(network));
            loadVariationModels = LoadVariationModels.createFrom(blackBoxModelSupplier, loadVariationArea, dynamicModelsParameters::add,
                    dynawoParameters.getNetworkParameters(), DynawoSimulationConstants.getSimulationParFile(network), reportNode);
        }

        private void setupContingencyEventModels() {
            this.contingencyEventModels = ContingencyEventModelsFactory.createFrom(contingencies,
                    parameters.getContingenciesStartTime(), network, blackBoxModelSupplier, reportNode);
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public MarginCalculationContext build() {
            setup();
            return new MarginCalculationContext(this);
        }

        private static FinalStepConfig configureFinalStep(MarginCalculationParameters parameters, List<LoadsVariation> loadsVariations) {
            return switch (parameters.getLoadModelsRule()) {
                case ALL_LOADS -> new FinalStepConfig(parameters.getStopTime(), AbstractLoad.class::isInstance);
                case TARGETED_LOADS -> {
                    Set<String> loadIds = loadsVariations.stream()
                            .flatMap(l -> l.loads().stream())
                            .map(Identifiable::getId)
                            .collect(Collectors.toSet());
                    yield new FinalStepConfig(parameters.getStopTime(),
                            bbm -> bbm instanceof AbstractLoad eBbm && loadIds.contains(eBbm.getStaticId()));
                }
            };
        }

        private static BiConsumer<LoadsProportionalScalable, Double> configureScaling(Network network) {
            ScalingParameters scalingParameters = new ScalingParameters()
                    .setScalingConvention(Scalable.ScalingConvention.LOAD)
                    .setConstantPowerFactor(true);
            return (s, v) -> s.scale(network, v, scalingParameters);
        }
    }

    private MarginCalculationContext(Builder builder) {
        super(builder);
        this.marginCalculationParameters = builder.parameters;
        this.contingencyEventModels = builder.contingencyEventModels;
        this.loadVariationModels = builder.loadVariationModels;
    }

    public MarginCalculationParameters getMarginCalculationParameters() {
        return marginCalculationParameters;
    }

    public List<ContingencyEventModels> getContingencyEventModels() {
        return contingencyEventModels;
    }

    public DydDataSupplier getLoadVariationAreaDydData() {
        return loadVariationModels;
    }
}
