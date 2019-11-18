/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import com.powsybl.computation.ComputationManager;
import com.powsybl.iidm.network.Network;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.Stabilization;

public class DynawoSimulatorFactory {

    public Stabilization createStabilization(Network network, ComputationManager computationManager, int priority) {
        return new DynawoStabilization(network, computationManager, priority);
    }

    public ImpactAnalysis createImpactAnalysis(Network network, ComputationManager computationManager, int priority) {
        return new DynawoImpactAnalysis(network, computationManager, priority);
    }

}
