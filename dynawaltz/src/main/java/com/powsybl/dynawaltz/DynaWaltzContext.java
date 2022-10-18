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
import com.powsybl.dynawaltz.dynamicmodels.*;
import com.powsybl.dynawaltz.events.BlackBoxEventModel;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import com.powsybl.iidm.network.Network;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzContext {

    private final Network network;
    private final String workingVariantId;
    private final DynamicSimulationParameters parameters;
    private final DynaWaltzParameters dynaWaltzParameters;
    private final DynaWaltzParametersDatabase parametersDatabase;
    private final List<DynamicModel> dynamicModels;
    private final List<EventModel> eventModels;
    private final List<Curve> curves;
    private final Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
    private final Map<Pair<String, String>, MacroConnector> connectorsMap = new LinkedHashMap<>();
    private final Map<Pair<String, String>, MacroConnector> eventConnectorsMap = new LinkedHashMap<>();
    private final Map<BlackBoxModel, List<BlackBoxModel>> modelsConnections = new LinkedHashMap<>();
    private final Map<BlackBoxEventModel, List<BlackBoxModel>> eventModelsConnections = new LinkedHashMap<>();
    private final NetworkModel networkModel = new NetworkModel();
    private final Map<String, AtomicInteger> counters = new HashMap<>();
    private final Map<BlackBoxModel, Integer> libIndexMap = new HashMap<>();

    private final OmegaRef omegaRef;

    public DynaWaltzContext(Network network, String workingVariantId, List<DynamicModel> dynamicModels, List<EventModel> eventModels, List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters) {
        this.network = Objects.requireNonNull(network);
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        this.dynamicModels = Objects.requireNonNull(dynamicModels);
        this.eventModels = Objects.requireNonNull(eventModels);
        this.curves = Objects.requireNonNull(curves);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynaWaltzParameters = Objects.requireNonNull(dynaWaltzParameters);
        this.parametersDatabase = loadDatabase(dynaWaltzParameters.getParametersFile());
        this.omegaRef = new OmegaRef(dynamicModels);
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

    public List<BlackBoxModel> getUserBlackBoxModels() {
        return getUserBlackBoxModelStream().collect(Collectors.toList());
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

    public Map<String, BlackBoxModel> getStaticIdBlackBoxModelMap() {
        return getBlackBoxModelStream()
                .filter(blackBoxModel -> !blackBoxModel.getStaticId().isEmpty())
                .collect(Collectors.toMap(BlackBoxModel::getStaticId, Function.identity(), this::mergeDuplicateStaticId, LinkedHashMap::new));
    }

    private BlackBoxModel mergeDuplicateStaticId(BlackBoxModel bbm1, BlackBoxModel bbm2) {
        throw new AssertionError("Duplicate staticId " + bbm1.getStaticId());
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
            getBlackBoxModelStream().forEach(this::computeMacroConnectors);
        }
    }

    private void computeMacroConnectors(BlackBoxModel bbm) {
        getModelsConnections().get(bbm).forEach(connectedBbm -> {
            var key = Pair.of(bbm.getLib(), connectedBbm.getLib());
            connectorsMap.computeIfAbsent(key, k -> createMacroConnector(bbm, connectedBbm));
        });
    }

    public Collection<MacroConnector> getEventMacroConnectors() {
        initEventConnectorsMap();
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
        getEventModelsConnections().get(bbem).forEach(connectedBbm -> {
            var connectorKey = Pair.of(bbem.getLib(), connectedBbm.getLib());
            eventConnectorsMap.computeIfAbsent(connectorKey, k -> createMacroConnector(bbem, connectedBbm));
        });
    }

    private MacroConnector createMacroConnector(BlackBoxModel bbm0, BlackBoxModel bbm1) {
        return new MacroConnector(bbm0.getLib(), bbm1.getLib(), bbm0.getVarsConnect(bbm1));
    }

    public Map<BlackBoxModel, List<BlackBoxModel>> getModelsConnections() {
        if (modelsConnections.isEmpty()) {
            getBlackBoxModelStream().forEach(bbm -> modelsConnections.put(bbm, bbm.getModelsConnectedTo(this)));
        }
        return modelsConnections;
    }

    public Map<BlackBoxEventModel, List<BlackBoxModel>> getEventModelsConnections() {
        if (eventModelsConnections.isEmpty()) {
            getBlackBoxEventModelStream().forEach(bbem -> eventModelsConnections.put(bbem, bbem.getModelsConnectedTo(this)));
        }
        return eventModelsConnections;
    }

    public Stream<BlackBoxModel> getUserBlackBoxModelStream() {
        return dynamicModels.stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast);
    }

    public Stream<BlackBoxModel> getBlackBoxModelStream() {
        if (omegaRef.hasToBeExported()) {
            return Stream.concat(getUserBlackBoxModelStream(), Stream.of(omegaRef));
        }
        return getUserBlackBoxModelStream();
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

    public NetworkModel getNetworkModel() {
        return networkModel;
    }

    public int getLibIndex(BlackBoxModel bbm) {
        return getLibIndexMap().get(bbm);
    }

    private Map<BlackBoxModel, Integer> getLibIndexMap() {
        if (libIndexMap.isEmpty()) {
            getBlackBoxModelStream().forEach(bbm -> libIndexMap.put(bbm,
                    counters.computeIfAbsent(bbm.getLib(), k -> new AtomicInteger()).getAndIncrement()));
        }
        return libIndexMap;
    }

    public String getParFile() {
        return Paths.get(getDynaWaltzParameters().getParametersFile()).getFileName().toString();
    }

    public String getSimulationParFile() {
        return getNetwork().getId() + ".par";
    }
}
