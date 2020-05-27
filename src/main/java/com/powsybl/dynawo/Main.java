/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.io.IOException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.CurvesProvider;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationProvider;
import com.powsybl.iidm.import_.Importers;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length < 2 || args.length > 3) {
            LOGGER.error("Usage: {} networkFile.xiidm dynamicModels.dyd [parametersFile.json]", Main.class.getName());
            System.exit(1);
        }

        String networkFile = args[0];
        String dydFile = args[1];
        String parametersFile = args.length == 3 ? args[2] : null;

        Network network = Importers.loadNetwork(networkFile);

        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        parameters.addExtension(DynawoSimulationParameters.class, DynawoSimulationParameters.load());
        if (parametersFile != null) {
            JsonDynamicSimulationParameters.update(parameters, Paths.get(parametersFile));
        }

        DynawoSimulationProvider provider = new DynawoSimulationProvider();
        provider.setDydFilename(dydFile);
        try (ComputationManager computationManager = new LocalComputationManager(LocalComputationConfig.load())) {
            provider.run(network, CurvesProvider.empty(), computationManager, network.getVariantManager().getWorkingVariantId(), parameters).join();
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
            System.exit(1);
        }
    }
}
