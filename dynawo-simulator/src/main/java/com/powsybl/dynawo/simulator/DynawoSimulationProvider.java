/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.util.concurrent.CompletableFuture;

import com.google.auto.service.AutoService;
import com.powsybl.computation.ComputationManager;
import com.powsybl.dynamic.simulation.DynamicSimulationParameters;
import com.powsybl.dynamic.simulation.DynamicSimulationProvider;
import com.powsybl.dynamic.simulation.DynamicSimulationResult;
import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.iidm.network.Network;
import com.powsybl.tools.PowsyblCoreVersion;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
@AutoService(DynamicSimulationProvider.class)
public class DynawoSimulationProvider implements DynamicSimulationProvider {

    @Override
    public String getName() {
        return "DynawoSimulation";
    }

    @Override
    public String getVersion() {
        return new PowsyblCoreVersion().getMavenProjectVersion();
    }

    @Override
    public CompletableFuture<DynamicSimulationResult> run(Network network, ComputationManager computationManager, String workingVariantId,
        DynamicSimulationParameters parameters) {
        DynawoSimulation dynawoSimulation = new DynawoSimulation(network, computationManager, 0, dynawoInputProvider);
        DynawoConfig dynawoConfig = parameters.getExtensionByName("DynawoConfig");
        if (dynawoConfig == null) {
            dynawoConfig = new DynawoConfig();
        }
        return dynawoSimulation.run(workingVariantId, dynawoConfig);
    }

    public void setDynawoInputProvider(DynawoInputProvider dynawoInputProvider) {
        this.dynawoInputProvider = dynawoInputProvider;
    }

    private DynawoInputProvider dynawoInputProvider;
}
