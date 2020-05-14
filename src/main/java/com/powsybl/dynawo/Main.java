/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.dynamicsimulation.DynamicSimulation;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
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

        if (args.length < 1 || args.length > 2) {
            LOGGER.info("Usage: {} networkFile.xiidm [parametersFile.json]", Main.class.getName());
            return;
        }
        Network network = Importers.loadNetwork(args[0]);
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        parameters.addExtension(DynawoSimulationParameters.class, DynawoSimulationParameters.load());
        if (args.length > 1) {
            JsonDynamicSimulationParameters.update(parameters, Paths.get(args[1]));
        }

        DynamicSimulation.Runner runner = DynamicSimulation.find();
        runner.run(network, parameters);
        System.exit(0);
    }
}
