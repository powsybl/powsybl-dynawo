/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.powsybl.computation.ComputationManager;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.computation.local.LocalComputationManager;
import com.powsybl.dynamicsimulation.CurvesSupplier;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.groovy.CurveGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyCurvesSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
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
        if (args.length < 2 || args.length > 4) {
            LOGGER.error("Usage: {} networkFile.xiidm dynamicModels.dyd [curves.crv] [parametersFile.json]", Main.class.getName());
            System.exit(1);
        }

        String networkFile = args[0];
        String dydFile = args[1];
        CurvesSupplier curvesSupplier = getCurvesSupplier(args);
        String parametersFile = getParametersFile(args);

        Network network = Importers.loadNetwork(networkFile);

        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        parameters.addExtension(DynawoSimulationParameters.class, DynawoSimulationParameters.load());
        if (parametersFile != null) {
            JsonDynamicSimulationParameters.update(parameters, Paths.get(parametersFile));
        }

        DynawoSimulationProvider provider = new DynawoSimulationProvider();
        DynamicModelsSupplier dynamicModelsSupplier = new GroovyDynamicModelsSupplier(Paths.get(dydFile));
        try (ComputationManager computationManager = new LocalComputationManager(LocalComputationConfig.load())) {
            provider.run(network, dynamicModelsSupplier, curvesSupplier, network.getVariantManager().getWorkingVariantId(), computationManager, parameters).join();
        } catch (IOException e) {
            LOGGER.error(e.toString(), e);
            System.exit(1);
        }
    }

    private static String getParametersFile(String[] args) {
        String parametersFile = args.length == 4 ? args[3] : null;
        if (parametersFile == null) {
            parametersFile = args.length == 3 && args[2].endsWith(".json") ? args[2] : null;
        }
        return parametersFile;
    }

    private static CurvesSupplier getCurvesSupplier(String[] args) {
        if (args.length >= 3 && args[2].endsWith(".crv")) {
            List<CurveGroovyExtension> extensions = GroovyExtension.find(CurveGroovyExtension.class, "dynawo");
            return new GroovyCurvesSupplier(Paths.get(args[2]), extensions);
        }
        return CurvesSupplier.empty();
    }
}
