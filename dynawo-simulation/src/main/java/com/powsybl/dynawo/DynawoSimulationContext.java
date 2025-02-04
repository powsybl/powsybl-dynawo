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
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.defaultmodels.DefaultModelsHandler;
import com.powsybl.dynawo.models.events.ContextDependentEvent;
import com.powsybl.dynawo.models.frequencysynchronizers.*;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.DydDataSupplier;
import com.powsybl.dynawo.xml.MacroStaticReference;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoSimulationContext implements DydDataSupplier {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DynawoSimulationContext.class);
    private static final String MODEL_ID_EXCEPTION = "The model identified by the static id %s does not match the expected model (%s)";
    private static final String MODEL_ID_LOG = "The model identified by the static id {} does not match the expected model ({})";

    protected final Network network;
    private final String workingVariantId;
    private final DynawoSimulationParameters dynawoSimulationParameters;
    //TODO create a model equivalent to FinalStepModels ?
    private final List<BlackBoxModel> dynamicModels;
    private final List<BlackBoxModel> eventModels;
    private final Map<String, EquipmentBlackBoxModel> staticIdBlackBoxModelMap;
    private final Map<OutputVariable.OutputType, List<OutputVariable>> outputVariables;
    private final Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
    private final List<MacroConnect> macroConnectList = new ArrayList<>();
    private final Map<String, MacroConnector> macroConnectorsMap = new LinkedHashMap<>();
    private final DefaultModelsHandler defaultModelsHandler = new DefaultModelsHandler();
    private final FrequencySynchronizerModel frequencySynchronizer;
    private final List<ParametersSet> dynamicModelsParameters = new ArrayList<>();
    protected final MacroConnectionsAdder macroConnectionsAdder;
    private final SimulationTime simulationTime;
    private final SimulationTime finalStepTime;
    private FinalStepModels finalStepModels;
    private ReportNode reportNode;

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

        protected void setup() {
            super.setup();
            setupEventModels();
        }

        public DynawoSimulationContext build() {
            setup();
            return new DynawoSimulationContext(this);
        }

        private void setupEventModels() {
            this.eventModels = Objects.requireNonNull(eventModels).stream()
                    .filter(distinctByDynamicId(reportNode)
                            .and(supportedVersion(dynawoVersion, reportNode)))
                    .toList();
        }

        protected Builder self() {
            return this;
        }
    }

    protected DynawoSimulationContext(AbstractContextBuilder<?> builder) {
        this.network = builder.network;
        this.workingVariantId = builder.workingVariantId;
        this.dynawoSimulationParameters = builder.dynawoParameters;
        this.simulationTime = builder.simulationTime;
        this.finalStepTime = builder.finalStepTime;
        this.dynamicModels = builder.dynamicModels;
        this.staticIdBlackBoxModelMap = builder.staticIdBlackBoxModelMap;
        this.frequencySynchronizer = builder.frequencySynchronizer;
        this.eventModels = builder.eventModels;
        this.outputVariables = builder.outputVariables;
        this.reportNode = builder.reportNode;
        this.macroConnectionsAdder = MacroConnectionsAdder.createFrom(this, macroConnectList::add, macroConnectorsMap::computeIfAbsent);

        // Late init on ContextDependentEvents
        this.eventModels.stream()
                .filter(ContextDependentEvent.class::isInstance)
                .map(ContextDependentEvent.class::cast)
                .forEach(e -> e.setEquipmentHasDynamicModel(this));

        // Write macro connection
        getBlackBoxDynamicModelStream().forEach(bbm -> {
            macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));
            bbm.createMacroConnections(macroConnectionsAdder);
            bbm.createDynamicModelParameters(dynamicModelsParameters::add);
        });

        ParametersSet networkParameters = getDynawoSimulationParameters().getNetworkParameters();
        for (BlackBoxModel bbem : eventModels) {
            bbem.createMacroConnections(macroConnectionsAdder);
            bbem.createDynamicModelParameters(dynamicModelsParameters::add);
            bbem.createNetworkParameter(networkParameters);
        }

        // Write final step macro connections
        if (!builder.finalStepDynamicModels.isEmpty()) {
            finalStepModels = new FinalStepModels(builder.finalStepDynamicModels, macroConnectionsAdder,
                    bbm -> !macroStaticReferences.containsKey(bbm.getName()),
                    n -> !macroConnectorsMap.containsKey(n));
            finalStepModels.getBlackBoxDynamicModels().forEach(bbm -> bbm.createDynamicModelParameters(dynamicModelsParameters::add));
        }
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

    @Override
    public Collection<MacroStaticReference> getMacroStaticReferences() {
        return macroStaticReferences.values();
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
        BlackBoxModel bbm = dynamicModels.stream()
                .filter(dm -> dynamicId.equals(dm.getDynamicModelId()))
                .filter(AbstractPureDynamicBlackBoxModel.class::isInstance)
                .findFirst().orElse(null);
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

    @Override
    public List<MacroConnect> getMacroConnectList() {
        return macroConnectList;
    }

    @Override
    public Collection<MacroConnector> getMacroConnectors() {
        return macroConnectorsMap.values();
    }

    private Stream<BlackBoxModel> getInputBlackBoxDynamicModelStream() {
        // Doesn't include the OmegaRef, it only concerns the DynamicModels provided by the user
        return dynamicModels.stream();
    }

    public Stream<BlackBoxModel> getBlackBoxDynamicModelStream() {
        if (frequencySynchronizer.isEmpty()) {
            return getInputBlackBoxDynamicModelStream();
        }
        return Stream.concat(getInputBlackBoxDynamicModelStream(), Stream.of(frequencySynchronizer));
    }

    @Override
    public List<BlackBoxModel> getBlackBoxDynamicModels() {
        return getBlackBoxDynamicModelStream().toList();
    }

    @Override
    public List<BlackBoxModel> getBlackBoxEventModels() {
        return eventModels;
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

    public Optional<DydDataSupplier> getFinalStepDydData() {
        return Optional.ofNullable(finalStepModels);
    }
}
