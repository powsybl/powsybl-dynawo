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
import com.powsybl.dynawo.builders.VersionInterval;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.dynawo.models.Model;
import com.powsybl.dynawo.models.buses.AbstractBus;
import com.powsybl.dynawo.models.defaultmodels.DefaultModelsHandler;
import com.powsybl.dynawo.models.events.ContextDependentEvent;
import com.powsybl.dynawo.models.frequencysynchronizers.*;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.xml.MacroStaticReference;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final DynamicSimulationParameters parameters;
    private final DynawoSimulationParameters dynawoSimulationParameters;
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

    public DynawoSimulationContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                                   List<OutputVariable> outputVariables, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoSimulationParameters) {
        this(network, workingVariantId, dynamicModels, eventModels, outputVariables, parameters, dynawoSimulationParameters, DynawoConstants.VERSION_MIN, ReportNode.NO_OP);
    }

    public DynawoSimulationContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                                   List<OutputVariable> outputVariables, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoSimulationParameters,
                                   DynawoVersion currentVersion, ReportNode reportNode) {

        ReportNode contextReportNode = DynawoSimulationReports.createDynawoSimulationContextReportNode(reportNode);
        DynawoVersion dynawoVersion = Objects.requireNonNull(currentVersion);
        this.network = Objects.requireNonNull(network);
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynawoSimulationParameters = Objects.requireNonNull(dynawoSimulationParameters);

        Stream<BlackBoxModel> uniqueIdsDynamicModels = Objects.requireNonNull(dynamicModels).stream()
                .filter(distinctByDynamicId(contextReportNode)
                        .and(distinctByStaticId(contextReportNode)
                        .and(supportedVersion(dynawoVersion, contextReportNode))));
        this.dynamicModels = dynawoSimulationParameters.isUseModelSimplifiers()
                ? simplifyModels(uniqueIdsDynamicModels, contextReportNode).toList()
                : uniqueIdsDynamicModels.toList();

        this.eventModels = Objects.requireNonNull(eventModels).stream()
                .filter(distinctByDynamicId(contextReportNode)
                        .and(supportedVersion(dynawoVersion, contextReportNode)))
                .toList();
        this.staticIdBlackBoxModelMap = getInputBlackBoxDynamicModelStream()
                .filter(EquipmentBlackBoxModel.class::isInstance)
                .map(EquipmentBlackBoxModel.class::cast)
                .collect(Collectors.toMap(EquipmentBlackBoxModel::getStaticId, Function.identity()));

        // Late init on ContextDependentEvents
        this.eventModels.stream()
                .filter(ContextDependentEvent.class::isInstance)
                .map(ContextDependentEvent.class::cast)
                .forEach(e -> e.setEquipmentHasDynamicModel(this));

        this.outputVariables = Objects.requireNonNull(outputVariables).stream()
                .collect(Collectors.groupingBy(OutputVariable::getOutputType));
        this.frequencySynchronizer = setupFrequencySynchronizer();
        this.macroConnectionsAdder = new MacroConnectionsAdder(this::getDynamicModel,
                this::getPureDynamicModel,
                macroConnectList::add,
                macroConnectorsMap::computeIfAbsent,
                contextReportNode);

        for (BlackBoxModel bbm : getBlackBoxDynamicModelStream().toList()) {
            macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));
            bbm.createMacroConnections(macroConnectionsAdder);
            bbm.createDynamicModelParameters(this, dynamicModelsParameters::add);
        }

        ParametersSet networkParameters = getDynawoSimulationParameters().getNetworkParameters();
        for (BlackBoxModel bbem : eventModels) {
            bbem.createMacroConnections(macroConnectionsAdder);
            bbem.createDynamicModelParameters(this, dynamicModelsParameters::add);
            bbem.createNetworkParameter(networkParameters);
        }
    }

    private Stream<BlackBoxModel> simplifyModels(Stream<BlackBoxModel> inputBbm, ReportNode reportNode) {
        Stream<BlackBoxModel> outputBbm = inputBbm;
        for (ModelsRemovalSimplifier modelsSimplifier : ServiceLoader.load(ModelsRemovalSimplifier.class)) {
            outputBbm = outputBbm.filter(modelsSimplifier.getModelRemovalPredicate(reportNode));
        }
        for (ModelsSubstitutionSimplifier modelsSimplifier : ServiceLoader.load(ModelsSubstitutionSimplifier.class)) {
            outputBbm = outputBbm.map(modelsSimplifier.getModelSubstitutionFunction(network, dynawoSimulationParameters, reportNode))
                    .filter(Objects::nonNull);
        }
        return outputBbm;
    }

    private FrequencySynchronizerModel setupFrequencySynchronizer() {
        List<SignalNModel> signalNModels = filterDynamicModels(SignalNModel.class);
        List<FrequencySynchronizedModel> frequencySynchronizedModels = filterDynamicModels(FrequencySynchronizedModel.class);
        boolean hasSpecificBuses = dynamicModels.stream().anyMatch(AbstractBus.class::isInstance);
        boolean hasSignalNModel = !signalNModels.isEmpty();
        if (!frequencySynchronizedModels.isEmpty() && hasSignalNModel) {
            throw new PowsyblException("Signal N and frequency synchronized generators cannot be used with one another");
        }
        if (hasSignalNModel) {
            return new SignalN(signalNModels);
        }
        return hasSpecificBuses ? new SetPoint(frequencySynchronizedModels) : new OmegaRef(frequencySynchronizedModels);
    }

    private <T extends Model> List<T> filterDynamicModels(Class<T> modelClass) {
        return dynamicModels.stream()
                .filter(modelClass::isInstance)
                .map(modelClass::cast)
                .toList();
    }

    public Network getNetwork() {
        return network;
    }

    public String getWorkingVariantId() {
        return workingVariantId;
    }

    public DynamicSimulationParameters getParameters() {
        return parameters;
    }

    public DynawoSimulationParameters getDynawoSimulationParameters() {
        return dynawoSimulationParameters;
    }

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

    public boolean hasDynamicModel(Identifiable<?> equipment) {
        return staticIdBlackBoxModelMap.containsKey(equipment.getId());
    }

    public List<MacroConnect> getMacroConnectList() {
        return macroConnectList;
    }

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

    public List<BlackBoxModel> getBlackBoxDynamicModels() {
        return getBlackBoxDynamicModelStream().toList();
    }

    public Stream<BlackBoxModel> getBlackBoxEventModelStream() {
        return eventModels.stream();
    }

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

    public String getSimulationParFile() {
        return getNetwork().getId() + ".par";
    }
}
