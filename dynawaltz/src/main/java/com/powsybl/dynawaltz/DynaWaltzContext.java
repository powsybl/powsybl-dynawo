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
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.generators.GeneratorSynchronousModel;
import com.powsybl.dynawaltz.models.lines.LineModel;
import com.powsybl.dynawaltz.models.utils.ConnectedModelTypes;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.Terminal;

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
    private final Map<String, BlackBoxModel> staticIdBlackBoxModelMap;
    private final List<Curve> curves;
    private final Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();
    private final Map<ConnectedModelTypes, MacroConnector> connectorsMap = new LinkedHashMap<>();
    private final Map<ConnectedModelTypes, MacroConnector> eventConnectorsMap = new LinkedHashMap<>();
    private final Map<BlackBoxModel, List<Model>> modelsConnections = new LinkedHashMap<>();
    private final Map<BlackBoxModel, List<Model>> eventModelsConnections = new LinkedHashMap<>();
    private final NetworkModel networkModel = new NetworkModel();

    private final OmegaRef omegaRef;
    private final PlatformConfig platformConfig;

    public DynaWaltzContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                            List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters) {
        this(network, workingVariantId, dynamicModels, eventModels, curves, parameters, dynaWaltzParameters, PlatformConfig.defaultConfig());
    }

    public DynaWaltzContext(Network network, String workingVariantId, List<BlackBoxModel> dynamicModels, List<BlackBoxModel> eventModels,
                            List<Curve> curves, DynamicSimulationParameters parameters, DynaWaltzParameters dynaWaltzParameters,
                            PlatformConfig platformConfig) {
        this.network = Objects.requireNonNull(network);
        this.workingVariantId = Objects.requireNonNull(workingVariantId);
        this.dynamicModels = Objects.requireNonNull(dynamicModels);
        this.eventModels = checkEventModelIdUniqueness(Objects.requireNonNull(eventModels));
        this.staticIdBlackBoxModelMap = getInputBlackBoxDynamicModelStream()
                .filter(blackBoxModel -> blackBoxModel.getStaticId().isPresent())
                .collect(Collectors.toMap(bbm -> bbm.getStaticId().get(), Function.identity(), this::mergeDuplicateStaticId, LinkedHashMap::new));
        this.curves = Objects.requireNonNull(curves);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynaWaltzParameters = Objects.requireNonNull(dynaWaltzParameters);
        this.platformConfig = Objects.requireNonNull(platformConfig);
        this.parametersDatabase = loadDatabase(dynaWaltzParameters.getParametersFile(), platformConfig);
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
                var key = ConnectedModelTypes.of(bbm.getName(), connectedBbm.getName());
                connectorsMap.computeIfAbsent(key, k -> createMacroConnector(bbm, connectedBbm));
            }
        }

        for (BlackBoxModel bbem : eventModels) {
            List<Model> modelsConnected = bbem.getModelsConnectedTo(this);
            eventModelsConnections.put(bbem, modelsConnected);

            for (Model connectedBbm : modelsConnected) {
                var key = ConnectedModelTypes.of(bbem.getName(), connectedBbm.getName());
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

    public Model getDynamicModelOrThrows(String staticId) {
        BlackBoxModel bbm = staticIdBlackBoxModelMap.get(staticId);
        if (bbm == null) {
            throw new PowsyblException("Cannot find the equipment '" + staticId + "' among the dynamic models provided");
        }
        return bbm;
    }

    public LineModel getDynamicModelOrDefaultLine(String staticId, Branch.Side side) {
        BlackBoxModel bbm = staticIdBlackBoxModelMap.get(staticId);
        if (bbm == null) {
            return networkModel.getDefaultLineModel(staticId, side);
        }
        if (bbm instanceof LineModel) {
            return (LineModel) bbm;
        }
        throw new PowsyblException("The model identified by the static id " + staticId + " is not a line model");
    }

    public BusModel getDynamicModelOrDefaultBus(Terminal terminal) {
        return getDynamicModelOrDefaultBus(terminal.getBusBreakerView().getConnectableBus().getId());
    }

    public BusModel getDynamicModelOrDefaultBus(String staticId) {
        BlackBoxModel bbm = staticIdBlackBoxModelMap.get(staticId);
        if (bbm == null) {
            return networkModel.getDefaultBusModel(staticId);
        }
        if (bbm instanceof BusModel) {
            return (BusModel) bbm;
        }
        throw new PowsyblException("The model identified by the static id " + staticId + " is not a bus model");
    }

    private BlackBoxModel mergeDuplicateStaticId(BlackBoxModel bbm1, BlackBoxModel bbm2) {
        throw new PowsyblException("Duplicate staticId: " + bbm1.getStaticId().orElseThrow());
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

    public Collection<MacroConnector> getMacroConnectors() {
        return connectorsMap.values();
    }

    public MacroConnector getMacroConnector(Model model1, Model model2) {
        return connectorsMap.get(ConnectedModelTypes.of(model1.getName(), model2.getName()));
    }

    public Collection<MacroConnector> getEventMacroConnectors() {
        return eventConnectorsMap.values();
    }

    public MacroConnector getEventMacroConnector(BlackBoxModel event, Model model) {
        return eventConnectorsMap.get(ConnectedModelTypes.of(event.getName(), model.getName()));
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

    private Stream<BlackBoxModel> getInputBlackBoxDynamicModelStream() {
        //Doesn't include the OmegaRef, it only concerns the DynamicModels provided by the user
        return dynamicModels.stream();
    }

    public Stream<BlackBoxModel> getBlackBoxDynamicModelStream() {
        if (omegaRef.isEmpty()) {
            return getInputBlackBoxDynamicModelStream();
        }
        return Stream.concat(getInputBlackBoxDynamicModelStream(), Stream.of(omegaRef));
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

    private static FileSystem getFileSystem(PlatformConfig platformConfig) {
        return platformConfig.getConfigDir()
                .map(Path::getFileSystem)
                .orElseThrow(() -> new PowsyblException("A configuration directory should be defined"));
    }

    private static DynaWaltzParametersDatabase loadDatabase(String filename, PlatformConfig platformConfig) {
        FileSystem fs = getFileSystem(platformConfig);
        return DynaWaltzParametersDatabase.load(fs.getPath(filename));
    }

    public String getParFile() {
        return Paths.get(getDynaWaltzParameters().getParametersFile()).getFileName().toString();
    }

    public String getSimulationParFile() {
        return getNetwork().getId() + ".par";
    }

    public PlatformConfig getPlatformConfig() {
        return platformConfig;
    }
}
