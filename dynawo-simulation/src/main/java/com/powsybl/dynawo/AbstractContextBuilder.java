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
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.buses.AbstractBus;
import com.powsybl.dynawo.models.frequencysynchronizers.*;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;

import java.util.*;
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
    protected BlackBoxModelSupplier blackBoxModelSupplier;
    protected List<BlackBoxModel> eventModels = Collections.emptyList();
    protected List<ParametersSet> dynamicModelsParameters = new ArrayList<>();
    protected SimulationModels simulationModels;
    protected Map<OutputVariable.OutputType, List<OutputVariable>> outputVariables = Collections.emptyMap();
    protected FinalStepConfig finalStepConfig = null;
    protected List<BlackBoxModel> finalStepDynamicModels = Collections.emptyList();
    protected FinalStepModels finalStepModels = null;
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

    protected final void setup() {
        setupData();
        setupMacroConnections();
    }

    protected void setupData() {
        if (workingVariantId == null) {
            workingVariantId = network.getVariantManager().getWorkingVariantId();
        }
        if (dynawoParameters == null) {
            dynawoParameters = DynawoSimulationParameters.load();
        }
        setupSimulationTime();
        setupDynamicModels();
    }

    protected void setupMacroConnections() {
        simulationModels = SimulationModels.createFrom(blackBoxModelSupplier, dynamicModels, eventModels, dynamicModelsParameters::add,
                dynawoParameters.getNetworkParameters(), reportNode);
        if (!finalStepDynamicModels.isEmpty()) {
            finalStepModels = FinalStepModels.createFrom(blackBoxModelSupplier, simulationModels, finalStepDynamicModels,
                    dynamicModelsParameters::add, reportNode);
        }
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
                .filter(distinctByDynamicId(reportNode).and(supportedVersion(dynawoVersion, reportNode)));
        if (dynawoParameters.isUseModelSimplifiers()) {
            uniqueIdsDynamicModels = simplifyModels(uniqueIdsDynamicModels);
        }

        if (finalStepConfig != null) {
            Map<Boolean, List<BlackBoxModel>> splitModels = uniqueIdsDynamicModels
                    .collect(Collectors.partitioningBy(finalStepConfig.modelsPredicate(), Collectors.toCollection(ArrayList::new)));
            dynamicModels = splitModels.get(false);
            finalStepDynamicModels = splitModels.get(true);
        } else {
            dynamicModels = uniqueIdsDynamicModels.collect(Collectors.toCollection(ArrayList::new));
        }
        setupFrequencySynchronizer();
        blackBoxModelSupplier = BlackBoxModelSupplier.createFrom(dynamicModels);
        checkForbiddenDefaultModels();
    }

    private void checkForbiddenDefaultModels() {
        if (dynamicModels.stream().anyMatch(BlackBoxModel::needMandatoryDynamicModels)) {
            checkEquipmentDynamicModels(network.getLines());
            checkEquipmentDynamicModels(network.getTwoWindingsTransformers());
            checkEquipmentDynamicModels(network.getBusBreakerView().getBuses());
        }
    }

    private <E extends Identifiable<E>> void checkEquipmentDynamicModels(Iterable<E> equipments) {
        for (E equipment : equipments) {
            if (!blackBoxModelSupplier.hasDynamicModel(equipment)) {
                throw new PowsyblException(String.format("At least one dynamic model forbid default models and the equipment %s does not possess a dynamic model", equipment.getId()));
            }
        }
    }

    private Stream<BlackBoxModel> simplifyModels(Stream<BlackBoxModel> inputBbm) {
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
        boolean hasFrequencySynchronizedModels = !frequencySynchronizedModels.isEmpty();
        boolean hasSignalNModels = !signalNModels.isEmpty();
        String defaultParFile = DynawoSimulationConstants.getSimulationParFile(network);
        if (hasFrequencySynchronizedModels && hasSignalNModels) {
            throw new PowsyblException("Signal N and frequency synchronized generators cannot be used with one another");
        }
        if (hasSignalNModels) {
            dynamicModels.add(new SignalN(signalNModels, defaultParFile));
        } else if (hasFrequencySynchronizedModels) {
            dynamicModels.add(hasSpecificBuses
                    ? new SetPoint(frequencySynchronizedModels, defaultParFile)
                    : new OmegaRef(frequencySynchronizedModels, defaultParFile, dynawoParameters));
        }
    }

    private <R extends Model> List<R> filterDynamicModels(Class<R> modelClass) {
        return dynamicModels.stream()
                .filter(modelClass::isInstance)
                .map(modelClass::cast)
                .toList();
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
