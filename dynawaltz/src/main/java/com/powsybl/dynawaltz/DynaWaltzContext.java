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
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.MacroConnector;
import com.powsybl.dynawaltz.models.NetworkModel;
import com.powsybl.dynawaltz.models.OmegaRef;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousModel;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import com.powsybl.iidm.network.Network;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    private final List<BlackBoxModel> dynamicModels;
    private final List<BlackBoxModel> eventModels;
    private final List<Curve> curves;
    private final Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
    private final Map<Pair<String, String>, MacroConnector> connectorsMap = new LinkedHashMap<>();
    private final Map<Pair<String, String>, MacroConnector> eventConnectorsMap = new LinkedHashMap<>();
    private final Map<BlackBoxModel, List<BlackBoxModel>> modelsConnections = new LinkedHashMap<>();
    private final Map<BlackBoxModel, List<BlackBoxModel>> eventModelsConnections = new LinkedHashMap<>();
    private final NetworkModel networkModel = new NetworkModel();

    private final OmegaRef omegaRef;

    public DynaWaltzContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                            List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters) {
        this.network = Objects.requireNonNull(network);
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        this.dynamicModels = Objects.requireNonNull(dynamicModels);
        this.eventModels = Objects.requireNonNull(eventModels);
        this.curves = Objects.requireNonNull(curves);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynaWaltzParameters = Objects.requireNonNull(dynaWaltzParameters);
        this.parametersDatabase = loadDatabase(dynaWaltzParameters.getParametersFile());
        this.omegaRef = new OmegaRef(dynamicModels.stream()
                .filter(GeneratorSynchronousModel.class::isInstance)
                .map(GeneratorSynchronousModel.class::cast)
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

    public DynaWaltzParametersDatabase getParametersDatabase() {
        return parametersDatabase;
    }

    public List<BlackBoxModel> getBlackBoxModels() {
        return Collections.unmodifiableList(dynamicModels);
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

    public MacroConnector getEventMacroConnector(BlackBoxModel event, BlackBoxModel bbm) {
        initEventConnectorsMap();
        return eventConnectorsMap.get(Pair.of(event.getLib(), bbm.getLib()));
    }

    private void initEventConnectorsMap() {
        if (eventConnectorsMap.isEmpty()) {
            getBlackBoxEventModelStream().forEach(this::computeEventMacroConnector);
        }
    }

    private void computeEventMacroConnector(BlackBoxModel event) {
        getEventModelsConnections().get(event).forEach(connectedBbm -> {
            var connectorKey = Pair.of(event.getLib(), connectedBbm.getLib());
            eventConnectorsMap.computeIfAbsent(connectorKey, k -> createMacroConnector(event, connectedBbm));
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

    public Map<BlackBoxModel, List<BlackBoxModel>> getEventModelsConnections() {
        if (eventModelsConnections.isEmpty()) {
            getBlackBoxEventModelStream().forEach(bbem -> eventModelsConnections.put(bbem, bbem.getModelsConnectedTo(this)));
        }
        return eventModelsConnections;
    }

    private Stream<BlackBoxModel> getInputBlackBoxModelStream() {
        //Doesn't include the OmegaRef, it only concerns the DynamicModels provided by the user
        return dynamicModels.stream();
    }

    public Stream<BlackBoxModel> getBlackBoxModelStream() {
        if (omegaRef.getSynchronousGenerators().isEmpty()) {
            return getInputBlackBoxModelStream();
        }
        return Stream.concat(getInputBlackBoxModelStream(), Stream.of(omegaRef));
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

    private static DynaWaltzParametersDatabase loadDatabase(String filename) {
        FileSystem fs = PlatformConfig.defaultConfig().getConfigDir()
                .map(Path::getFileSystem)
                .orElseThrow(() -> new PowsyblException("A configuration directory should be defined"));
        return DynaWaltzParametersDatabase.load(fs.getPath(filename));
    }

    public NetworkModel getNetworkModel() {
        return networkModel;
    }

    public String getParFile() {
        return Paths.get(getDynaWaltzParameters().getParametersFile()).getFileName().toString();
    }

    public String getSimulationParFile() {
        return getNetwork().getId() + ".par";
    }
}
