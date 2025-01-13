/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.Phase2Config;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadVariationAreaAutomationSystem;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsProportionalScalable;
import com.powsybl.dynawo.margincalculation.loadsvariation.LoadsVariation;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.loads.AbstractLoad;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
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
public class MarginCalculationContext extends DynawoSimulationContext {

    private final MarginCalculationParameters marginCalculationParameters;
    private final List<ContingencyEventModels> contingencyEventModels;
    private final LoadVariationAreaAutomationSystem loadVariationArea;
    private final List<MacroConnect> loadVariationMacroConnectList = new ArrayList<>();
    private final Map<String, MacroConnector> loadVariationMacroConnectorsMap = new LinkedHashMap<>();

    public MarginCalculationContext(Network network, String workingVariantId,
                                    List<BlackBoxModel> dynamicModels,
                                    MarginCalculationParameters parameters,
                                    List<Contingency> contingencies,
                                    List<LoadsVariation> loadsVariations) {
        this(network, workingVariantId, dynamicModels, parameters, contingencies,
                loadsVariations, DynawoConstants.VERSION_MIN, ReportNode.NO_OP);
    }

    public MarginCalculationContext(Network network, String workingVariantId,
                                    List<BlackBoxModel> dynamicModels,
                                    MarginCalculationParameters parameters,
                                    List<Contingency> contingencies,
                                    List<LoadsVariation> loadsVariations,
                                    DynawoVersion currentVersion,
                                    ReportNode reportNode) {
        super(network, workingVariantId, dynamicModels, Collections.emptyList(), Collections.emptyList(),
                new DynamicSimulationParameters(parameters.getStartTime(), parameters.getMarginCalculationStartTime()),
                parameters.getDynawoParameters(),
                configurePhase2(parameters, loadsVariations),
                currentVersion, reportNode);
        this.marginCalculationParameters = parameters;
        double contingenciesStartTime = parameters.getContingenciesStartTime();
        this.contingencyEventModels = ContingencyEventModelsFactory
                .createFrom(contingencies, this, macroConnectionsAdder, contingenciesStartTime, reportNode);
        this.loadVariationArea = new LoadVariationAreaAutomationSystem(loadsVariations,
                parameters.getLoadIncreaseStartTime(),
                parameters.getLoadIncreaseStopTime(),
                configureScaling(network));

        macroConnectionsAdder.setMacroConnectorAdder(loadVariationMacroConnectorsMap::computeIfAbsent);
        macroConnectionsAdder.setMacroConnectAdder(loadVariationMacroConnectList::add);
        loadVariationArea.createMacroConnections(macroConnectionsAdder);
        loadVariationArea.createDynamicModelParameters(this, getDynamicModelsParameters()::add);
    }

    public MarginCalculationParameters getMarginCalculationParameters() {
        return marginCalculationParameters;
    }

    public List<ContingencyEventModels> getContingencyEventModels() {
        return contingencyEventModels;
    }

    public DydDataSupplier getLoadVariationAreaDydData() {
        return new DydDataSupplier() {

            @Override
            public List<BlackBoxModel> getBlackBoxDynamicModels() {
                return List.of(loadVariationArea);
            }

            @Override
            public Collection<MacroConnector> getMacroConnectors() {
                return loadVariationMacroConnectorsMap.values();
            }

            @Override
            public List<MacroConnect> getMacroConnectList() {
                return loadVariationMacroConnectList;
            }

            @Override
            public String getParFileName() {
                return DynawoSimulationConstants.getSimulationParFile(getNetwork());
            }
        };
    }

    private static Phase2Config configurePhase2(MarginCalculationParameters parameters, List<LoadsVariation> loadsVariations) {
        return switch (parameters.getLoadModelsRule()) {
            case ALL_LOADS -> new Phase2Config(parameters.getStopTime(), AbstractLoad.class::isInstance);
            case TARGETED_LOADS -> {
                Set<String> loadIds = loadsVariations.stream()
                        .flatMap(l -> l.loads().stream())
                        .map(Identifiable::getId)
                        .collect(Collectors.toSet());
                yield new Phase2Config(parameters.getStopTime(),
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
