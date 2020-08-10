/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicEventModel;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoContext {

    public DynawoContext(Network network, List<DynamicModel> dynamicModels, List<DynamicEventModel> dynamicEventModels, List<Curve> curves, DynamicSimulationParameters parameters, DynawoSimulationParameters dynawoParameters) {
        this.network = Objects.requireNonNull(network);
        this.dynamicModels = Objects.requireNonNull(dynamicModels);
        this.dynamicEventModels = Objects.requireNonNull(dynamicEventModels);
        this.curves = Objects.requireNonNull(curves);
        this.parameters = Objects.requireNonNull(parameters);
        this.dynawoParameters = Objects.requireNonNull(dynawoParameters);
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

    public List<DynamicEventModel> getDynamicEventModels() {
        return Collections.unmodifiableList(dynamicEventModels);
    }

    public List<Curve> getCurves() {
        return Collections.unmodifiableList(curves);
    }

    public boolean withCurves() {
        return !curves.isEmpty();
    }

    private final Network network;
    private final DynamicSimulationParameters parameters;
    private final DynawoSimulationParameters dynawoParameters;
    private final List<DynamicModel> dynamicModels;
    private final List<DynamicEventModel> dynamicEventModels;
    private final List<Curve> curves;
}
