/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.EquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.Model;
import com.powsybl.dynawaltz.models.buses.AbstractBus;
import com.powsybl.dynawaltz.models.defaultmodels.DefaultModelsHandler;
import com.powsybl.dynawaltz.models.events.ContextDependentEvent;
import com.powsybl.dynawaltz.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawaltz.models.frequencysynchronizers.FrequencySynchronizerModel;
import com.powsybl.dynawaltz.models.frequencysynchronizers.OmegaRef;
import com.powsybl.dynawaltz.models.frequencysynchronizers.SetPoint;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnect;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnector;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
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
public class DynaWaltzContext {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DynaWaltzContext.class);
    private static final String MODEL_ID_EXCEPTION = "The model identified by the static id %s does not match the expected model (%s)";
    private static final String MODEL_ID_LOG = "The model identified by the static id {} does not match the expected model ({})";

    protected final Network network;
    private final String workingVariantId;
    private final DynamicSimulationParameters parameters;
    private final DynaWaltzParameters dynaWaltzParameters;
    private final List<BlackBoxModel> dynamicModels;
    private final List<BlackBoxModel> eventModels;
    private final Map<String, EquipmentBlackBoxModel> staticIdBlackBoxModelMap;
    private final List<Curve> curves;
    private final Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
    private final List<MacroConnect> macroConnectList = new ArrayList<>();
    private final Map<String, MacroConnector> macroConnectorsMap = new LinkedHashMap<>();
    private final DefaultModelsHandler defaultModelsHandler = new DefaultModelsHandler();
    private final FrequencySynchronizerModel frequencySynchronizer;
    private final List<ParametersSet> dynamicModelsParameters = new ArrayList<>();
    protected final MacroConnectionsAdder macroConnectionsAdder;

    public DynaWaltzContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                            List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters) {
        this(network, workingVariantId, dynamicModels, eventModels, curves, parameters, dynaWaltzParameters, Reporter.NO_OP);
    }

    public DynaWaltzContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                            List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters, Reporter reporter) {

        Reporter contextReporter = DynawaltzReports.createDynaWaltzContextReporter(reporter);
        this.network = Objects.requireNonNull(network);
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynaWaltzParameters = Objects.requireNonNull(dynaWaltzParameters);

        Stream<BlackBoxModel> uniqueIdsDynamicModels = Objects.requireNonNull(dynamicModels).stream()
                .filter(distinctByDynamicId(contextReporter).and(distinctByStaticId(contextReporter)));
        this.dynamicModels = dynaWaltzParameters.isUseModelSimplifiers()
                ? simplifyModels(uniqueIdsDynamicModels, contextReporter).toList()
                : uniqueIdsDynamicModels.toList();

        this.eventModels = Objects.requireNonNull(eventModels).stream()
                .filter(distinctByDynamicId(contextReporter))
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

        this.curves = Objects.requireNonNull(curves);
        this.frequencySynchronizer = setupFrequencySynchronizer(dynamicModels.stream().anyMatch(AbstractBus.class::isInstance) ? SetPoint::new : OmegaRef::new);
        this.macroConnectionsAdder = new MacroConnectionsAdder(this::getDynamicModel,
                this::getPureDynamicModel,
                macroConnectList::add,
                macroConnectorsMap::computeIfAbsent,
                contextReporter);

        for (BlackBoxModel bbm : getBlackBoxDynamicModelStream().toList()) {
            macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));
            bbm.createMacroConnections(macroConnectionsAdder);
            bbm.createDynamicModelParameters(this, dynamicModelsParameters::add);
        }

        ParametersSet networkParameters = getDynaWaltzParameters().getNetworkParameters();
        for (BlackBoxModel bbem : eventModels) {
            bbem.createMacroConnections(macroConnectionsAdder);
            bbem.createDynamicModelParameters(this, dynamicModelsParameters::add);
            bbem.createNetworkParameter(networkParameters);
        }
    }

    private Stream<BlackBoxModel> simplifyModels(Stream<BlackBoxModel> inputBbm, Reporter reporter) {
        Stream<BlackBoxModel> outputBbm = inputBbm;
        for (ModelsSimplifier modelsSimplifier : ServiceLoader.load(ModelsSimplifier.class)) {
            outputBbm = modelsSimplifier.simplifyModels(outputBbm, network, dynaWaltzParameters, reporter);
        }
        return outputBbm;
    }

    private FrequencySynchronizerModel setupFrequencySynchronizer(Function<List<FrequencySynchronizedModel>, FrequencySynchronizerModel> fsConstructor) {
        return fsConstructor.apply(dynamicModels.stream()
                .filter(FrequencySynchronizedModel.class::isInstance)
                .map(FrequencySynchronizedModel.class::cast)
                .toList());
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

    public DynaWaltzParameters getDynaWaltzParameters() {
        return dynaWaltzParameters;
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

    protected static Predicate<BlackBoxModel> distinctByStaticId(Reporter reporter) {
        Set<String> seen = new HashSet<>();
        return bbm -> {
            if (bbm instanceof EquipmentBlackBoxModel eBbm && !seen.add(eBbm.getStaticId())) {
                DynawaltzReports.reportDuplicateStaticId(reporter, eBbm.getStaticId(), eBbm.getLib(), eBbm.getDynamicModelId());
                return false;
            }
            return true;
        };
    }

    protected static Predicate<BlackBoxModel> distinctByDynamicId(Reporter reporter) {
        Set<String> seen = new HashSet<>();
        return bbm -> {
            if (!seen.add(bbm.getDynamicModelId())) {
                DynawaltzReports.reportDuplicateDynamicId(reporter, bbm.getDynamicModelId(), bbm.getName());
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

    public List<Curve> getCurves() {
        return Collections.unmodifiableList(curves);
    }

    public boolean withCurves() {
        return !curves.isEmpty();
    }

    public List<ParametersSet> getDynamicModelsParameters() {
        return dynamicModelsParameters;
    }

    public String getSimulationParFile() {
        return getNetwork().getId() + ".par";
    }
}
