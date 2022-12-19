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
import com.powsybl.dynawaltz.models.*;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousModel;
import com.powsybl.dynawaltz.models.utils.Couple;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import com.powsybl.iidm.network.Network;

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
    private final Map<Couple<String>, MacroConnector> connectorsMap = new LinkedHashMap<>();
    private final Map<Couple<String>, MacroConnector> eventConnectorsMap = new LinkedHashMap<>();
    private final Map<BlackBoxModel, List<Model>> modelsConnections = new LinkedHashMap<>();
    private final Map<BlackBoxModel, List<Model>> eventModelsConnections = new LinkedHashMap<>();
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

        List<GeneratorSynchronousModel> synchronousGenerators = dynamicModels.stream()
                .filter(GeneratorSynchronousModel.class::isInstance)
                .map(GeneratorSynchronousModel.class::cast)
                .collect(Collectors.toList());
        this.omegaRef = new OmegaRef(synchronousGenerators);

        for (BlackBoxModel bbm : Stream.concat(dynamicModels.stream(), Stream.of(omegaRef)).collect(Collectors.toList())) {
            macroStaticReferences.computeIfAbsent(bbm.getName(), k -> new MacroStaticReference(k, bbm.getVarsMapping()));

            List<Model> modelsConnected = bbm.getModelsConnectedTo(this);
            modelsConnections.put(bbm, modelsConnected);

            for (Model connectedBbm : modelsConnected) {
                var key = new Couple<>(bbm.getName(), connectedBbm.getName());
                connectorsMap.computeIfAbsent(key, k -> createMacroConnector(bbm, connectedBbm));
            }
        }

        for (BlackBoxModel bbem : eventModels) {
            List<Model> modelsConnected = bbem.getModelsConnectedTo(this);
            eventModelsConnections.put(bbem, modelsConnected);

            for (Model connectedBbm : modelsConnected) {
                var key = new Couple<>(bbem.getName(), connectedBbm.getName());
                eventConnectorsMap.computeIfAbsent(key, k -> createMacroConnector(bbem, connectedBbm));
            }
        }
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

    public Collection<MacroStaticReference> getMacroStaticReferences() {
        return macroStaticReferences.values();
    }

    public Map<String, BlackBoxModel> getStaticIdBlackBoxModelMap() {
        return getInputBlackBoxModelStream()
                .filter(blackBoxModel -> blackBoxModel.getStaticId().isPresent())
                .collect(Collectors.toMap(bbm -> bbm.getStaticId().get(), Function.identity(), this::mergeDuplicateStaticId, LinkedHashMap::new));
    }

    private BlackBoxModel mergeDuplicateStaticId(BlackBoxModel bbm1, BlackBoxModel bbm2) {
        throw new AssertionError("Duplicate staticId " + bbm1.getStaticId());
    }

    public Collection<MacroConnector> getMacroConnectors() {
        return connectorsMap.values();
    }

    public MacroConnector getMacroConnector(Model model1, Model model2) {
        return connectorsMap.get(new Couple<>(model1.getName(), model2.getName()));
    }

    public Collection<MacroConnector> getEventMacroConnectors() {
        return eventConnectorsMap.values();
    }

    public MacroConnector getEventMacroConnector(BlackBoxModel event, Model model) {
        return eventConnectorsMap.get(new Couple<>(event.getName(), model.getName()));
    }

    private MacroConnector createMacroConnector(BlackBoxModel bbm, Model model) {
        return new MacroConnector(bbm.getName(), model.getName(), bbm.getVarConnectionsWith(model));
    }

    public Map<BlackBoxModel, List<Model>> getModelsConnections() {
        return modelsConnections;
    }

    public Map<BlackBoxModel, List<Model>> getEventModelsConnections() {
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

    public List<BlackBoxModel> getBlackBoxModels() {
        return getBlackBoxModelStream().collect(Collectors.toList());
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
