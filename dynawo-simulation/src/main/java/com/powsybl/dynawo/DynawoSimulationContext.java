/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.defaultmodels.DefaultModelsHandler;
import com.powsybl.dynawo.models.events.ContextDependentEvent;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.DydDataSupplier;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoSimulationContext {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DynawoSimulationContext.class);
    private static final String MODEL_ID_EXCEPTION = "The model identified by the static id %s does not match the expected model (%s)";
    private static final String MODEL_ID_LOG = "The model identified by the static id {} does not match the expected model ({})";

    protected final Network network;
    private final String workingVariantId;
    private final DynawoSimulationParameters dynawoSimulationParameters;
    private final Map<String, EquipmentBlackBoxModel> staticIdBlackBoxModelMap;
    private final Map<String, BlackBoxModel> pureDynamicModelMap;
    private final Map<OutputVariable.OutputType, List<OutputVariable>> outputVariables;
    private final DefaultModelsHandler defaultModelsHandler = new DefaultModelsHandler();
    private final List<ParametersSet> dynamicModelsParameters = new ArrayList<>();
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
        protected void setup() {
            super.setup();
            setupEventModels();
        }

        private void setupEventModels() {
            this.eventModels = Objects.requireNonNull(eventModels).stream()
                    .filter(distinctByDynamicId(reportNode)
                            .and(supportedVersion(dynawoVersion, reportNode)))
                    .toList();
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
        this.staticIdBlackBoxModelMap = builder.staticIdBlackBoxModelMap;
        this.pureDynamicModelMap = builder.pureDynamicModelMap;
        this.outputVariables = builder.outputVariables;
        this.reportNode = builder.reportNode;

        // Late init on ContextDependentEvents
        builder.eventModels.stream()
                .filter(ContextDependentEvent.class::isInstance)
                .map(ContextDependentEvent.class::cast)
                .forEach(e -> e.setEquipmentHasDynamicModel(this));

        simulationModels = SimulationModels.createFrom(this, builder.dynamicModels, builder.eventModels);

        // Write final step macro connections
        //TODO reference firstStep in final step
        finalStepModels = !builder.finalStepDynamicModels.isEmpty() ?
                new FinalStepModels(this, builder.finalStepDynamicModels,
                    bbm -> !simulationModels.hasMacroStaticReference(bbm),
                    n -> !simulationModels.hasMacroConnector(n),
                    dynamicModelsParameters::add)
            : null;
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

    public <T extends Model> T getDynamicModel(Identifiable<?> equipment, Class<T> connectableClass, boolean throwException) {
        BlackBoxModel bbm = staticIdBlackBoxModelMap.get(equipment.getId());
        if (bbm == null) {
            return defaultModelsHandler.getDefaultModel(equipment, connectableClass, throwException);
        }
        if (connectableClass.isInstance(bbm)) {
            return connectableClass.cast(bbm);
        }
        if (throwException) {
            throw new PowsyblException(String.format(MODEL_ID_EXCEPTION, equipment.getId(), connectableClass.getSimpleName()));
        } else {
            LOGGER.warn(MODEL_ID_LOG, equipment.getId(), connectableClass.getSimpleName());
            return null;
        }
    }

    public <T extends Model> T getPureDynamicModel(String dynamicId, Class<T> connectableClass, boolean throwException) {
        BlackBoxModel bbm = pureDynamicModelMap.get(dynamicId);
        if (bbm == null) {
            if (throwException) {
                throw new PowsyblException("Pure dynamic model " + dynamicId + " not found");
            } else {
                LOGGER.warn("Pure dynamic model {} not found", dynamicId);
                return null;
            }
        }
        if (connectableClass.isInstance(bbm)) {
            return connectableClass.cast(bbm);
        }
        if (throwException) {
            throw new PowsyblException(String.format(MODEL_ID_EXCEPTION, dynamicId, connectableClass.getSimpleName()));
        } else {
            LOGGER.warn(MODEL_ID_LOG, dynamicId, connectableClass.getSimpleName());
            return null;
        }
    }

    public boolean hasDynamicModel(Identifiable<?> equipment) {
        return staticIdBlackBoxModelMap.containsKey(equipment.getId());
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
