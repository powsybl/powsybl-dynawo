/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.events.ContextDependentEvent;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.DydDataSupplier;
import com.powsybl.iidm.network.Network;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoSimulationContext {

    protected final Network network;
    private final String workingVariantId;
    private final DynawoSimulationParameters dynawoSimulationParameters;
    private final Map<OutputVariable.OutputType, List<OutputVariable>> outputVariables;
    private final List<ParametersSet> dynamicModelsParameters;
    private final SimulationModels simulationModels;
    private final FinalStepModels finalStepModels;
    private final SimulationTime simulationTime;
    private final SimulationTime finalStepTime;
    private final ReportNode reportNode;

    public static class Builder extends AbstractContextBuilder<Builder> {

        public Builder(Network network, List<BlackBoxModel> dynamicModels) {
            super(network, dynamicModels);
        }

        public Builder dynamicSimulationParameters(DynamicSimulationParameters parameters) {
            this.simulationParameters = Objects.requireNonNull(parameters);
            return self();
        }

        public Builder eventModels(List<BlackBoxModel> eventModels) {
            this.eventModels = eventModels;
            return self();
        }

        public Builder outputVariables(List<OutputVariable> outputVariables) {
            this.outputVariables = Objects.requireNonNull(outputVariables).stream()
                    .collect(Collectors.groupingBy(OutputVariable::getOutputType));
            return self();
        }

        public Builder finalStepConfig(FinalStepConfig finalStepConfig) {
            this.finalStepConfig = Objects.requireNonNull(finalStepConfig);
            return self();
        }

        @Override
        protected void setupData() {
            super.setupData();
            eventModels = Objects.requireNonNull(eventModels).stream()
                    .filter(distinctByDynamicId(reportNode)
                            .and(supportedVersion(dynawoVersion, reportNode)))
                    .toList();
            // Late init on ContextDependentEvents
            eventModels.stream()
                    .filter(ContextDependentEvent.class::isInstance)
                    .map(ContextDependentEvent.class::cast)
                    .forEach(e -> e.setEquipmentHasDynamicModel(blackBoxModelSupplier.hasDynamicModel(e.getEquipment())));
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public DynawoSimulationContext build() {
            setup();
            return new DynawoSimulationContext(this);
        }
    }

    protected DynawoSimulationContext(AbstractContextBuilder<?> builder) {
        this.network = builder.network;
        this.workingVariantId = builder.workingVariantId;
        this.dynawoSimulationParameters = builder.dynawoParameters;
        this.simulationTime = builder.simulationTime;
        this.finalStepTime = builder.finalStepTime;
        this.dynamicModelsParameters = builder.dynamicModelsParameters;
        this.outputVariables = builder.outputVariables;
        this.reportNode = builder.reportNode;
        this.simulationModels = builder.simulationModels;
        this.finalStepModels = builder.finalStepModels;
    }

    public Network getNetwork() {
        return network;
    }

    public String getWorkingVariantId() {
        return workingVariantId;
    }

    public SimulationTime getSimulationTime() {
        return simulationTime;
    }

    public SimulationTime getFinalStepSimulationTime() {
        return finalStepTime;
    }

    public DynawoSimulationParameters getDynawoSimulationParameters() {
        return dynawoSimulationParameters;
    }

    public List<BlackBoxModel> getBlackBoxDynamicModels() {
        return simulationModels.getBlackBoxDynamicModels();
    }

    public List<BlackBoxModel> getBlackBoxEventModels() {
        return simulationModels.getBlackBoxEventModels();
    }

    public List<OutputVariable> getOutputVariables(OutputVariable.OutputType type) {
        return outputVariables.get(type);
    }

    public boolean withCurveVariables() {
        return outputVariables.containsKey(OutputVariable.OutputType.CURVE);
    }

    public boolean withFsvVariables() {
        return outputVariables.containsKey(OutputVariable.OutputType.FINAL_STATE);
    }

    public boolean withConstraints() {
        return false;
    }

    public List<ParametersSet> getDynamicModelsParameters() {
        return dynamicModelsParameters;
    }

    public ReportNode getReportNode() {
        return reportNode;
    }

    public String getSimulationParFile() {
        return getNetwork().getId() + ".par";
    }

    public DydDataSupplier getSimulationDydData() {
        return simulationModels;
    }

    public Optional<DydDataSupplier> getFinalStepDydData() {
        return Optional.ofNullable(finalStepModels);
    }
}
