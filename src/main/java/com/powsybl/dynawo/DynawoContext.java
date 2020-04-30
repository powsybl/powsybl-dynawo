/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.util.Objects;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoContext {

    public DynawoContext(Network network, DynamicSimulationParameters parameters) {
        this.network = Objects.requireNonNull(network);
        this.parameters = Objects.requireNonNull(parameters);
    }

    public Network getNetwork() {
        return network;
    }

    public DynamicSimulationParameters getParameters() {
        return parameters;
    }

    private final Network network;
    private final DynamicSimulationParameters parameters;
}
