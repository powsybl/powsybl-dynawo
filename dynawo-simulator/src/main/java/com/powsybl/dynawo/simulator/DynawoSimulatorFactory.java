/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import com.powsybl.computation.ComputationManager;
import com.powsybl.dynawo.DynawoExporter;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.simulation.ImpactAnalysis;
import com.powsybl.simulation.Stabilization;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public interface DynawoSimulatorFactory {

    public Stabilization createStabilization(Network network, ComputationManager computationManager, int priority);

    public ImpactAnalysis createImpactAnalysis(Network network, ComputationManager computationManager, int priority,
        DynawoProvider dynawoProvider, DynawoExporter exporter);
}
