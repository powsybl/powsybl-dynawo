/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.json;

import java.io.IOException;

import org.junit.Test;

import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzParameters;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzParametersSerializerTest extends AbstractConverterTest {

    @Test
    public void roundTripParameters() throws IOException {
        String parametersFile = "/home/user/parametersFile";
        String networkParametersFile = "/home/user/networkParametersFile";
        String solverParametersFile = "/home/user/solverParametersFile";
        InMemoryPlatformConfig platformConfig = new InMemoryPlatformConfig(fileSystem);
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynawaltz-default-parameters");
        moduleConfig.setStringProperty("parametersFile", parametersFile);
        moduleConfig.setStringProperty("network.parametersFile", networkParametersFile);
        moduleConfig.setStringProperty("solver.parametersFile", solverParametersFile);

        DynamicSimulationParameters dynamicSimulationParameters = new DynamicSimulationParameters()
            .setStartTime(0)
            .setStopTime(3600);
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load(platformConfig);
        dynamicSimulationParameters.addExtension(DynaWaltzParameters.class, dynawoParameters);
        roundTripTest(dynamicSimulationParameters, JsonDynamicSimulationParameters::write,
            JsonDynamicSimulationParameters::read, "/DynaWaltzParameters.json");
    }
}
