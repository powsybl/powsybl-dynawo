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
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import com.powsybl.iidm.network.Network;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzContext {

    private final Map<String, MacroStaticReference> macroStaticReferences = new LinkedHashMap<>();

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

    public Stream<BlackBoxModel> getBlackBoxModelStream() {
        return dynamicModels.stream()
                .filter(BlackBoxModel.class::isInstance)
                .map(BlackBoxModel.class::cast);
    }

    public List<EventModel> getEventModels() {
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

    private final Network network;
    private final String workingVariantId;
    private final DynamicSimulationParameters parameters;
    private final DynaWaltzParameters dynaWaltzParameters;
    private final DynaWaltzParametersDatabase parametersDatabase;
    private final List<DynamicModel> dynamicModels;
    private final List<EventModel> eventModels;
    private final List<Curve> curves;
}
