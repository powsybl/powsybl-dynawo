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
import com.powsybl.dynawaltz.models.*;
import com.powsybl.dynawaltz.models.buses.AbstractBus;
import com.powsybl.dynawaltz.models.buses.DefaultEquipmentConnectionPoint;
import com.powsybl.dynawaltz.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawaltz.models.defaultmodels.DefaultModelsHandler;
import com.powsybl.dynawaltz.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.dynawaltz.models.frequencysynchronizers.FrequencySynchronizerModel;
import com.powsybl.dynawaltz.models.frequencysynchronizers.OmegaRef;
import com.powsybl.dynawaltz.models.frequencysynchronizers.SetPoint;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnect;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectAttribute;
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

    private final Reporter reporter;
    private final Network network;
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

    public DynaWaltzContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                            List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters) {
        this(network, workingVariantId, dynamicModels, eventModels, curves, parameters, dynaWaltzParameters, Reporter.NO_OP);
    }

    public DynaWaltzContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                            List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters, Reporter reporter) {

        this.reporter = DynawaltzReports.createDynaWaltzContextReporter(reporter);
        this.network = Objects.requireNonNull(network);
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynaWaltzParameters = Objects.requireNonNull(dynaWaltzParameters);

        Iterator<ModelOptimizer> dataOptimizers = dynaWaltzParameters.isUseModelOptimizers() ? ServiceLoader.load(ModelOptimizer.class).iterator() : Collections.emptyIterator();
        this.dynamicModels = runOptimizers(dataOptimizers,
                Objects.requireNonNull(dynamicModels)
                .stream()
                .filter(distinctByDynamicId(reporter).and(distinctByStaticId(reporter))),
               reporter)
                .toList();

        this.eventModels = Objects.requireNonNull(eventModels).stream()
                .filter(distinctByDynamicId(reporter))
                .toList();
        this.staticIdBlackBoxModelMap = getInputBlackBoxDynamicModelStream()
                .filter(EquipmentBlackBoxModel.class::isInstance)
                .map(EquipmentBlackBoxModel.class::cast)
                .collect(Collectors.toMap(EquipmentBlackBoxModel::getStaticId, Function.identity()));
        this.curves = Objects.requireNonNull(curves);
        this.frequencySynchronizer = setupFrequencySynchronizer(dynamicModels.stream().anyMatch(AbstractBus.class::isInstance) ? SetPoint::new : OmegaRef::new);

        for (BlackBoxModel bbm : getBlackBoxDynamicModelStream().toList()) {
            macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));
            bbm.createMacroConnections(this);
            bbm.createDynamicModelParameters(this, dynamicModelsParameters::add);
        }

        ParametersSet networkParameters = getDynaWaltzParameters().getNetworkParameters();
        for (BlackBoxModel bbem : eventModels) {
            bbem.createMacroConnections(this);
            bbem.createDynamicModelParameters(this, dynamicModelsParameters::add);
            bbem.createNetworkParameter(networkParameters);
        }
    }

    private FrequencySynchronizerModel setupFrequencySynchronizer(Function<List<FrequencySynchronizedModel>, FrequencySynchronizerModel> fsConstructor) {
        return fsConstructor.apply(dynamicModels.stream()
                .filter(FrequencySynchronizedModel.class::isInstance)
                .map(FrequencySynchronizedModel.class::cast)
                .toList());
    }

    private Stream<BlackBoxModel> runOptimizers(Iterator<ModelOptimizer> dataOptimizers, Stream<BlackBoxModel> inputData, Reporter reporter) {
        return dataOptimizers.hasNext() ? runOptimizers(dataOptimizers, dataOptimizers.next().optimizeModels(inputData, dynaWaltzParameters, reporter), reporter) : inputData;
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

    public <T extends Model> T getDynamicModel(String staticId, Class<T> clazz) {
        BlackBoxModel bbm = staticIdBlackBoxModelMap.get(staticId);
        if (bbm == null) {
            return defaultModelsHandler.getDefaultModel(staticId, clazz);
        }
        if (clazz.isInstance(bbm)) {
            return clazz.cast(bbm);
        }
        throw new PowsyblException(String.format(MODEL_ID_EXCEPTION, staticId, clazz.getSimpleName()));
    }

    public <T extends Model> T getDynamicModel(Identifiable<?> equipment, Class<T> connectableClass) {
        return getDynamicModel(equipment, connectableClass, true);
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

    public EquipmentConnectionPoint getConnectionPointDynamicModel(String staticId) {
        BlackBoxModel bbm = staticIdBlackBoxModelMap.get(staticId);
        if (bbm == null) {
            return DefaultEquipmentConnectionPoint.getInstance();
        }
        if (bbm instanceof EquipmentConnectionPoint cp) {
            return cp;
        }
        throw new PowsyblException(String.format(MODEL_ID_EXCEPTION, staticId, "ConnectionPoint"));
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

    public void addMacroConnect(String macroConnectorId, List<MacroConnectAttribute> attributesFrom, List<MacroConnectAttribute> attributesTo) {
        macroConnectList.add(new MacroConnect(macroConnectorId, attributesFrom, attributesTo));
    }

    public List<MacroConnect> getMacroConnectList() {
        return macroConnectList;
    }

    public String addMacroConnector(String name1, String name2, List<VarConnection> varConnections) {
        String macroConnectorId = MacroConnector.createMacroConnectorId(name1, name2);
        macroConnectorsMap.computeIfAbsent(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnections));
        return macroConnectorId;
    }

    public String addMacroConnector(String name1, String name2, Side side, List<VarConnection> varConnections) {
        String macroConnectorId = MacroConnector.createMacroConnectorId(name1, name2, side);
        macroConnectorsMap.computeIfAbsent(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnections));
        return macroConnectorId;
    }

    public String addMacroConnector(String name1, String name2, String name1Suffix, List<VarConnection> varConnections) {
        String macroConnectorId = MacroConnector.createMacroConnectorId(name1, name2, name1Suffix);
        macroConnectorsMap.computeIfAbsent(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnections));
        return macroConnectorId;
    }

    public Collection<MacroConnector> getMacroConnectors() {
        return macroConnectorsMap.values();
    }

    public boolean isWithoutBlackBoxDynamicModel(String staticId) {
        return !staticIdBlackBoxModelMap.containsKey(staticId);
    }

    public boolean isWithoutBlackBoxDynamicModel(Identifiable<?> equipment) {
        return !staticIdBlackBoxModelMap.containsKey(equipment.getId());
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

    public Reporter getReporter() {
        return reporter;
    }
}
