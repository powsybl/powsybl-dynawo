/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoContext {

    public DynawoContext(Network network, List<DynamicModel> dynamicModels, List<Curve> curves, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoParameters) {
        this.network = Objects.requireNonNull(network);
        this.dynamicModels = Objects.requireNonNull(dynamicModels);
        this.curves = Objects.requireNonNull(curves);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynawoParameters = Objects.requireNonNull(dynawoParameters);

        this.macroConnectors.put("LoadToNode",
            "<dyn:macroConnector id=\"LoadToNode\">" +
            "  <dyn:connect var1=\"load_terminal\" var2=\"@STATIC_ID@@NODE@_ACPIN\"/>" +
            "  <dyn:connect var1=\"load_switchOffSignal1\" var2=\"@STATIC_ID@@NODE@_switchOff\"/>" +
            "</dyn:macroConnector>");

        this.macroStaticReferences.put("Load",
            "<dyn:macroStaticReference id=\"Load\">" +
            "    <dyn:staticRef var=\"load_PPu\" staticVar=\"p\"/>" +
            "    <dyn:staticRef var=\"load_QPu\" staticVar=\"q\"/>" +
            "    <dyn:staticRef var=\"load_state\" staticVar=\"state\"/>" +
            "  </dyn:macroStaticReference>");
    }

    public Network getNetwork() {
        return network;
    }

    public DynamicSimulationParameters getParameters() {
        return parameters;
    }

    public DynawoSimulationParameters getDynawoParameters() {
        return dynawoParameters;
    }

    public List<DynamicModel> getDynamicModels() {
        return Collections.unmodifiableList(dynamicModels);
    }

    public List<Curve> getCurves() {
        return Collections.unmodifiableList(curves);
    }

    public boolean withCurves() {
        return !curves.isEmpty();
    }

    public String getMacroConnector(String id) {
        return macroConnectors.get(id);
    }

    public String getMacroStaticReference(String id) {
        return macroStaticReferences.get(id);
    }

    private final Network network;
    private final DynamicSimulationParameters parameters;
    private final DynawoSimulationParameters dynawoParameters;
    private final List<DynamicModel> dynamicModels;
    private final List<Curve> curves;

    private final Map<String, String> macroConnectors = new HashMap<>();
    private final Map<String, String> macroStaticReferences = new HashMap<>();
}
