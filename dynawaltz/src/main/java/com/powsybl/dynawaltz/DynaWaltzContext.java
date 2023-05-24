/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.models.*;
import com.powsybl.dynawaltz.models.buses.InfiniteBus;
import com.powsybl.dynawaltz.models.frequencysynchronizers.FrequencySynchronizerModel;
import com.powsybl.dynawaltz.models.frequencysynchronizers.OmegaRef;
import com.powsybl.dynawaltz.models.frequencysynchronizers.SetPoint;
import com.powsybl.dynawaltz.models.generators.OmegaRefGeneratorModel;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Network;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DynaWaltzContext {

    private static final String MODEL_ID_EXCEPTION = "The model identified by the static id %s does not match the expected model (%s)";

    private final Network network;
    private final String workingVariantId;
    private final DynamicSimulationParameters parameters;
    private final DynaWaltzParameters dynaWaltzParameters;
    private final List<BlackBoxModel> dynamicModels;
    private final List<BlackBoxModel> eventModels;
    private final Map<String, EquipmentBlackBoxModelModel> staticIdBlackBoxModelMap;
    private final List<Curve> curves;
    private final Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
    private final List<MacroConnect> macroConnectList = new ArrayList<>();
    private final Map<String, MacroConnector> macroConnectorsMap = new LinkedHashMap<>();
    private final NetworkModel networkModel = new NetworkModel();
    private final FrequencySynchronizerModel frequencySynchronizer;

    public DynaWaltzContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                            List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters) {
        this.network = Objects.requireNonNull(network);
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        this.dynamicModels = Objects.requireNonNull(dynamicModels);
        this.eventModels = checkEventModelIdUniqueness(Objects.requireNonNull(eventModels));
        this.staticIdBlackBoxModelMap = getInputBlackBoxDynamicModelStream()
                .filter(EquipmentBlackBoxModelModel.class::isInstance)
                .map(EquipmentBlackBoxModelModel.class::cast)
                .collect(Collectors.toMap(EquipmentBlackBoxModelModel::getStaticId, Function.identity(), this::mergeDuplicateStaticId, LinkedHashMap::new));
        this.curves = Objects.requireNonNull(curves);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynaWaltzParameters = Objects.requireNonNull(dynaWaltzParameters);
        this.frequencySynchronizer = setupFrequencySynchronizer(dynamicModels.stream().anyMatch(InfiniteBus.class::isInstance) ? SetPoint::new : OmegaRef::new);

        for (BlackBoxModel bbm : getBlackBoxDynamicModelStream().collect(Collectors.toList())) {
            macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));
            bbm.createMacroConnections(this);
        }

        for (BlackBoxModel bbem : eventModels) {
            bbem.createMacroConnections(this);
        }
    }

    private FrequencySynchronizerModel setupFrequencySynchronizer(Function<List<OmegaRefGeneratorModel>, FrequencySynchronizerModel> fsConstructor) {
        return fsConstructor.apply(dynamicModels.stream()
                .filter(OmegaRefGeneratorModel.class::isInstance)
                .map(OmegaRefGeneratorModel.class::cast)
                .collect(Collectors.toList()));
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
            return networkModel.getDefaultModel(staticId, clazz);
        }
        if (clazz.isInstance(bbm)) {
            return clazz.cast(bbm);
        }
        throw new PowsyblException(String.format(MODEL_ID_EXCEPTION, staticId, clazz.getSimpleName()));
    }

    public <T extends Model> T getDynamicModel(Identifiable<?> equipment, Class<T> connectableClass) {
        BlackBoxModel bbm = staticIdBlackBoxModelMap.get(equipment.getId());
        if (bbm == null) {
            return networkModel.getDefaultModel(equipment, connectableClass);
        }
        if (connectableClass.isInstance(bbm)) {
            return connectableClass.cast(bbm);
        }
        throw new PowsyblException(String.format(MODEL_ID_EXCEPTION, equipment.getId(), connectableClass.getSimpleName()));
    }

    public <T extends Model> T getPureDynamicModel(String dynamicId, Class<T> connectableClass) {
        BlackBoxModel bbm = dynamicModels.stream()
                .filter(dm -> dynamicId.equals(dm.getDynamicModelId()))
                .findFirst()
                .orElseThrow(() -> {
                    throw new PowsyblException("Pure dynamic model " + dynamicId + " not found");
                });
        if (connectableClass.isInstance(bbm)) {
            return connectableClass.cast(bbm);
        }
        throw new PowsyblException(String.format(MODEL_ID_EXCEPTION, dynamicId, connectableClass.getSimpleName()));
    }

    private EquipmentBlackBoxModelModel mergeDuplicateStaticId(EquipmentBlackBoxModelModel bbm1, EquipmentBlackBoxModelModel bbm2) {
        throw new PowsyblException("Duplicate staticId: " + bbm1.getStaticId());
    }

    private static List<BlackBoxModel> checkEventModelIdUniqueness(List<BlackBoxModel> eventModels) {
        Set<String> dynamicIds = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (BlackBoxModel bbm : eventModels) {
            if (!dynamicIds.add(bbm.getDynamicModelId())) {
                duplicates.add(bbm.getDynamicModelId());
            }
        }
        if (!duplicates.isEmpty()) {
            throw new PowsyblException("Duplicate dynamicId: " + duplicates);
        }
        return eventModels;
    }

    public void addMacroConnect(String macroConnectorId, List<MacroConnectAttribute> attributesFrom, List<MacroConnectAttribute> attributesTo) {
        macroConnectList.add(new MacroConnect(macroConnectorId, attributesFrom, attributesTo));
    }

    public void addMacroConnect(String macroConnectorId, List<MacroConnectAttribute> attributesFrom) {
        macroConnectList.add(new MacroConnect(macroConnectorId, attributesFrom));
    }

    public List<MacroConnect> getMacroConnectList() {
        return macroConnectList;
    }

    public String addMacroConnector(String name1, List<VarConnection> varConnections) {
        String macroConnectorId = MacroConnector.createMacroConnectorId(name1);
        macroConnectorsMap.computeIfAbsent(macroConnectorId, k -> new MacroConnector(macroConnectorId, varConnections));
        return macroConnectorId;
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
        //Doesn't include the OmegaRef, it only concerns the DynamicModels provided by the user
        return dynamicModels.stream();
    }

    public Stream<BlackBoxModel> getBlackBoxDynamicModelStream() {
        if (frequencySynchronizer.isEmpty()) {
            return getInputBlackBoxDynamicModelStream();
        }
        return Stream.concat(getInputBlackBoxDynamicModelStream(), Stream.of(frequencySynchronizer));
    }

    public List<BlackBoxModel> getBlackBoxDynamicModels() {
        return getBlackBoxDynamicModelStream().collect(Collectors.toList());
    }

    public List<BlackBoxModel> getBlackBoxModels() {
        return Stream.concat(getBlackBoxDynamicModelStream(), getBlackBoxEventModelStream())
                .collect(Collectors.toList());
    }

    public Stream<BlackBoxModel> getBlackBoxEventModelStream() {
        return eventModels.stream();
    }

    public List<BlackBoxModel> getBlackBoxEventModels() {
        return Collections.unmodifiableList(eventModels);
    }

    public List<Curve> getCurves() {
        return Collections.unmodifiableList(curves);
    }

    public boolean withCurves() {
        return !curves.isEmpty();
    }

    public String getSimulationParFile() {
        return getNetwork().getId() + ".par";
    }
}
