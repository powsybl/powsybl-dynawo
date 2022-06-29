/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.MacroConnector;
import com.powsybl.dynawaltz.dynamicmodels.NetworkModel;
import com.powsybl.dynawaltz.dynamicmodels.OmegaRef;
import com.powsybl.dynawaltz.events.BlackBoxEventModel;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import com.powsybl.iidm.network.Network;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzContext {

    private final Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
    private final Map<Pair<String, String>, MacroConnector> connectorsMap = new LinkedHashMap<>();
    private final Map<Pair<String, String>, MacroConnector> eventConnectorsMap = new LinkedHashMap<>();
    private final Map<BlackBoxModel, BlackBoxModel> modelsConnections = new LinkedHashMap<>();
    private final Map<BlackBoxEventModel, BlackBoxModel> eventModelsConnections = new LinkedHashMap<>();
    private NetworkModel networkModel = new NetworkModel();

    public DynaWaltzContext(Network network, String workingVariantId, List<DynamicModel> dynamicModels, List<EventModel> eventModels, List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters) {
        this.network = Objects.requireNonNull(network);
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        this.dynamicModels = Objects.requireNonNull(dynamicModels);
        this.eventModels = Objects.requireNonNull(eventModels);
        this.curves = Objects.requireNonNull(curves);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynaWaltzParameters = Objects.requireNonNull(dynaWaltzParameters);
        this.parametersDatabase = loadDatabase(dynaWaltzParameters.getParametersFile());
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

    public DynaWaltzParametersDatabase getParametersDatabase() {
        return parametersDatabase;
    }

    public List<DynamicModel> getDynamicModels() {
        return Collections.unmodifiableList(dynamicModels);
    }

    public List<BlackBoxModel> getBlackBoxModels() {
        return getBlackBoxModelStream().collect(Collectors.toList());
    }

    public Collection<MacroStaticReference> getMacroStaticReferences() {
        initMacroStaticReferences();
        return macroStaticReferences.values();
    }

    private void initMacroStaticReferences() {
        if (macroStaticReferences.isEmpty()) {
            getBlackBoxModelStream().forEach(bbm ->
                macroStaticReferences.computeIfAbsent(bbm.getLib(), k -> new MacroStaticReference(k, bbm.getVarsMapping()))
            );
        }
    }

    public Map<String, BlackBoxModel> getDynamicIdBlackBoxModelMap() {
        return getBlackBoxModelStream()
                .collect(Collectors.toMap(BlackBoxModel::getDynamicModelId, Function.identity(), this::mergeModelsDynamicId, LinkedHashMap::new));
    }

    public Map<String, BlackBoxModel> getStaticIdBlackBoxModelMap() {
        return getBlackBoxModelStream()
                .filter(blackBoxModel -> !(blackBoxModel instanceof OmegaRef))
                .collect(Collectors.toMap(BlackBoxModel::getStaticId, Function.identity(), this::mergeModelsStaticId, LinkedHashMap::new));
    }

    private BlackBoxModel mergeModelsStaticId(BlackBoxModel o1, BlackBoxModel o2) {
        throw new AssertionError("Duplicate staticId " + o1.getStaticId()
                + " with two models: " + o1.getClass().getSimpleName() + " and " + o2.getClass().getSimpleName());
    }

    private BlackBoxModel mergeModelsDynamicId(BlackBoxModel o1, BlackBoxModel o2) {
        throw new AssertionError("Duplicate dynamicModelId " + o1.getDynamicModelId()
                + " with two models: " + o1.getClass().getSimpleName() + " and " + o2.getClass().getSimpleName());
    }

    public Collection<MacroConnector> getMacroConnectors() {
        initConnectorsMap();
        return connectorsMap.values();
    }

    public MacroConnector getMacroConnector(BlackBoxModel bbm0, BlackBoxModel bbm1) {
        initConnectorsMap();
        return connectorsMap.get(Pair.of(bbm0.getLib(), bbm1.getLib()));
    }

    private void initConnectorsMap() {
        if (connectorsMap.isEmpty()) {
            getBlackBoxModelStream().forEach(this::computeMacroConnector);
        }
    }

    private void computeMacroConnector(BlackBoxModel bbm) {
        BlackBoxModel connectedBbm = getModelsConnections().get(bbm);
        connectorsMap.computeIfAbsent(Pair.of(bbm.getLib(), connectedBbm.getLib()),
                k -> new MacroConnector(bbm.getLib(), connectedBbm.getLib(), bbm.getVarsConnect(connectedBbm)));
    }

    public Collection<MacroConnector> getEventMacroConnectors() {
        return eventConnectorsMap.values();
    }

    public MacroConnector getEventMacroConnector(BlackBoxEventModel bbem, BlackBoxModel bbm) {
        initEventConnectorsMap();
        return eventConnectorsMap.get(Pair.of(bbem.getLib(), bbm.getLib()));
    }

    private void initEventConnectorsMap() {
        if (eventConnectorsMap.isEmpty()) {
            getBlackBoxEventModelStream().forEach(this::computeEventMacroConnector);
        }
    }

    private void computeEventMacroConnector(BlackBoxEventModel bbem) {
        BlackBoxModel connectedBbm = getEventModelsConnections().get(bbem);
        eventConnectorsMap.computeIfAbsent(Pair.of(bbem.getLib(), connectedBbm.getLib()),
                k -> new MacroConnector(bbem.getLib(), connectedBbm.getLib(), bbem.getVarsConnect(connectedBbm)));
    }

    public Map<BlackBoxModel, BlackBoxModel> getModelsConnections() {
        if (modelsConnections.isEmpty()) {
            getBlackBoxModelStream().forEach(bbm -> modelsConnections.put(bbm, bbm.getModelConnectedTo(this)));
        }
        return modelsConnections;
    }

   public Map<BlackBoxEventModel, BlackBoxModel> getEventModelsConnections() {
        if (eventModelsConnections.isEmpty()) {
            getBlackBoxEventModelStream().forEach(bbem -> eventModelsConnections.put(bbem, bbem.getModelConnectedTo(this)));
        }
        return eventModelsConnections;
    }

    public Stream<BlackBoxModel> getBlackBoxModelStream() {
        return dynamicModels.stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast);
    }

    public List<EventModel> getEventModels() {
        return Collections.unmodifiableList(eventModels);
    }

    public Stream<BlackBoxEventModel> getBlackBoxEventModelStream() {
        return eventModels.stream()
                .filter(BlackBoxEventModel.class::isInstance)
                .map(BlackBoxEventModel.class::cast);
    }

    public List<BlackBoxEventModel> getBlackBoxEventModels() {
        return getBlackBoxEventModelStream().collect(Collectors.toList());
    }

    public List<Curve> getCurves() {
        return Collections.unmodifiableList(curves);
    }

    public boolean withCurves() {
        return !curves.isEmpty();
    }

    private static DynaWaltzParametersDatabase loadDatabase(String filename) {
        FileSystem fs = PlatformConfig.defaultConfig().getConfigDir()
                .map(Path::getFileSystem)
                .orElseThrow(() -> new PowsyblException("A configuration directory should be defined"));
        return DynaWaltzParametersDatabase.load(fs.getPath(filename));
    }

    private final Network network;
    private final String workingVariantId;
    private final DynamicSimulationParameters parameters;
    private final DynaWaltzParameters dynaWaltzParameters;
    private final DynaWaltzParametersDatabase parametersDatabase;
    private final List<DynamicModel> dynamicModels;
    private final List<EventModel> eventModels;
    private final List<Curve> curves;

    public NetworkModel getNetworkModel() {
        return networkModel;
    }
}
