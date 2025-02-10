/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.builders.VersionInterval;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.buses.AbstractBus;
import com.powsybl.dynawo.models.frequencysynchronizers.*;
import com.powsybl.iidm.network.Network;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractContextBuilder<T extends AbstractContextBuilder<T>> {

    protected final Network network;
    protected DynamicSimulationParameters simulationParameters = null;
    protected DynawoSimulationParameters dynawoParameters = null;
    protected String workingVariantId;
    protected List<BlackBoxModel> dynamicModels;
    protected Map<String, EquipmentBlackBoxModel> staticIdBlackBoxModelMap;
    protected FrequencySynchronizerModel frequencySynchronizer;
    protected List<BlackBoxModel> eventModels = Collections.emptyList();
    protected Map<OutputVariable.OutputType, List<OutputVariable>> outputVariables = Collections.emptyMap();
    protected FinalStepConfig finalStepConfig = null;
    protected List<BlackBoxModel> finalStepDynamicModels = Collections.emptyList();
    protected SimulationTime simulationTime;
    protected SimulationTime finalStepTime = null;
    protected DynawoVersion dynawoVersion = DynawoConstants.VERSION_MIN;
    protected ReportNode reportNode = ReportNode.NO_OP;

    protected AbstractContextBuilder(Network network, List<BlackBoxModel> dynamicModels) {
        this.network = network;
        this.dynamicModels = dynamicModels;
    }

    public T workingVariantId(String workingVariantId) {
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        return self();
    }

    public T dynawoParameters(DynawoSimulationParameters dynawoParameters) {
        this.dynawoParameters = Objects.requireNonNull(dynawoParameters);
        return self();
    }

    public T currentVersion(DynawoVersion currentVersion) {
        this.dynawoVersion = currentVersion;
        return self();
    }

    public T reportNode(ReportNode reportNode) {
        this.reportNode = DynawoSimulationReports.createDynawoSimulationContextReportNode(reportNode);
        return self();
    }

    protected void setup() {
        if (workingVariantId == null) {
            workingVariantId = network.getVariantManager().getWorkingVariantId();
        }
        if (dynawoParameters == null) {
            dynawoParameters = DynawoSimulationParameters.load();
        }
        setupSimulationTime();
        setupDynamicModels();
        setupFrequencySynchronizer();
    }

    protected void setupSimulationTime() {
        if (simulationParameters == null) {
            simulationParameters = DynamicSimulationParameters.load();
        }
        this.simulationTime = new SimulationTime(simulationParameters.getStartTime(), simulationParameters.getStopTime());
        if (finalStepConfig != null) {
            this.finalStepTime = new SimulationTime(simulationTime.stopTime(), finalStepConfig.stopTime());
        }
    }

    private void setupDynamicModels() {
        Stream<BlackBoxModel> uniqueIdsDynamicModels = Objects.requireNonNull(dynamicModels).stream()
                .filter(distinctByDynamicId(reportNode)
                        .and(distinctByStaticId(reportNode)
                                .and(supportedVersion(dynawoVersion, reportNode))));
        if (dynawoParameters.isUseModelSimplifiers()) {
            uniqueIdsDynamicModels = simplifyModels(uniqueIdsDynamicModels, reportNode);
        }

        if (finalStepConfig != null) {
            Map<Boolean, List<BlackBoxModel>> splitModels = uniqueIdsDynamicModels.collect(Collectors.partitioningBy(finalStepConfig.modelsPredicate()));
            dynamicModels = splitModels.get(false);
            finalStepDynamicModels = splitModels.get(true);
        } else {
            dynamicModels = uniqueIdsDynamicModels.toList();
        }
        setupDynamicModelsMap();
    }

    private void setupDynamicModelsMap() {
        staticIdBlackBoxModelMap = dynamicModels.stream()
                .filter(EquipmentBlackBoxModel.class::isInstance)
                .map(EquipmentBlackBoxModel.class::cast)
                .collect(Collectors.toMap(EquipmentBlackBoxModel::getStaticId, Function.identity()));
    }

    private Stream<BlackBoxModel> simplifyModels(Stream<BlackBoxModel> inputBbm, ReportNode reportNode) {
        Stream<BlackBoxModel> outputBbm = inputBbm;
        for (ModelsRemovalSimplifier modelsSimplifier : ServiceLoader.load(ModelsRemovalSimplifier.class)) {
            outputBbm = outputBbm.filter(modelsSimplifier.getModelRemovalPredicate(reportNode));
        }
        for (ModelsSubstitutionSimplifier modelsSimplifier : ServiceLoader.load(ModelsSubstitutionSimplifier.class)) {
            outputBbm = outputBbm.map(modelsSimplifier.getModelSubstitutionFunction(network, dynawoParameters, reportNode))
                    .filter(Objects::nonNull);
        }
        return outputBbm;
    }

    private void setupFrequencySynchronizer() {
        List<SignalNModel> signalNModels = filterDynamicModels(SignalNModel.class);
        List<FrequencySynchronizedModel> frequencySynchronizedModels = filterDynamicModels(FrequencySynchronizedModel.class);
        boolean hasSpecificBuses = dynamicModels.stream().anyMatch(AbstractBus.class::isInstance);
        boolean hasSignalNModel = !signalNModels.isEmpty();
        String defaultParFile = DynawoSimulationConstants.getSimulationParFile(network);
        if (!frequencySynchronizedModels.isEmpty() && hasSignalNModel) {
            throw new PowsyblException("Signal N and frequency synchronized generators cannot be used with one another");
        }
        if (hasSignalNModel) {
            frequencySynchronizer = new SignalN(signalNModels, defaultParFile);
        } else {
            frequencySynchronizer = hasSpecificBuses ? new SetPoint(frequencySynchronizedModels, defaultParFile)
                    : new OmegaRef(frequencySynchronizedModels, defaultParFile, dynawoParameters);
        }
    }

    private <R extends Model> List<R> filterDynamicModels(Class<R> modelClass) {
        return dynamicModels.stream()
                .filter(modelClass::isInstance)
                .map(modelClass::cast)
                .toList();
    }

    protected static Predicate<BlackBoxModel> distinctByStaticId(ReportNode reportNode) {
        Set<String> seen = new HashSet<>();
        return bbm -> {
            if (bbm instanceof EquipmentBlackBoxModel eBbm && !seen.add(eBbm.getStaticId())) {
                DynawoSimulationReports.reportDuplicateStaticId(reportNode, eBbm.getStaticId(), eBbm.getLib(), eBbm.getDynamicModelId());
                return false;
            }
            return true;
        };
    }

    protected static Predicate<BlackBoxModel> distinctByDynamicId(ReportNode reportNode) {
        Set<String> seen = new HashSet<>();
        return bbm -> {
            if (!seen.add(bbm.getDynamicModelId())) {
                DynawoSimulationReports.reportDuplicateDynamicId(reportNode, bbm.getDynamicModelId(), bbm.getName());
                return false;
            }
            return true;
        };
    }

    protected static Predicate<BlackBoxModel> supportedVersion(DynawoVersion currentVersion, ReportNode reportNode) {
        return bbm -> {
            VersionInterval versionInterval = bbm.getVersionInterval();
            if (currentVersion.compareTo(versionInterval.min()) < 0) {
                DynawoSimulationReports.reportDynawoVersionTooHigh(reportNode, bbm.getName(), bbm.getDynamicModelId(), versionInterval.min(), currentVersion);
                return false;
            }
            if (versionInterval.max() != null && currentVersion.compareTo(versionInterval.max()) >= 0) {
                DynawoSimulationReports.reportDynawoVersionTooLow(reportNode, bbm.getName(), bbm.getDynamicModelId(), versionInterval.max(), currentVersion, versionInterval.endCause());
                return false;
            }
            return true;
        };
    }

    protected abstract T self();

    public abstract DynawoSimulationContext build();
}
