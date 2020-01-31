/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.json;

import java.io.IOException;

import org.junit.Test;

import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynamicsimulation.json.JsonDynamicSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverIDAParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoSimulationParametersSerializerTest extends AbstractConverterTest {

    @Test
    public void roundTripSIMParameters() throws IOException {
        DynamicSimulationParameters dynamicSimulationParameters = new DynamicSimulationParameters()
            .setStartTime(0)
            .setStopTime(1);
        DynawoSimulationParameters dynawoSimulationParameters = new DynawoSimulationParameters()
            .setSolverParameters(new SolverParameters(SolverType.SIM))
            .setDslFilename(null);
        dynamicSimulationParameters.addExtension(DynawoSimulationParameters.class, dynawoSimulationParameters);
        roundTripTest(dynamicSimulationParameters, JsonDynamicSimulationParameters::write,
            JsonDynamicSimulationParameters::read, "/DynawoSimulationSIMParameters.json");
    }

    @Test
    public void roundTripIDAParameters() throws IOException {
        DynamicSimulationParameters dynamicSimulationParameters = new DynamicSimulationParameters()
            .setStartTime(0)
            .setStopTime(1);
        DynawoSimulationParameters dynawoSimulationParameters = new DynawoSimulationParameters()
            .setSolverParameters(new SolverIDAParameters(2))
            .setDslFilename(null);
        dynamicSimulationParameters.addExtension(DynawoSimulationParameters.class, dynawoSimulationParameters);
        roundTripTest(dynamicSimulationParameters, JsonDynamicSimulationParameters::write,
            JsonDynamicSimulationParameters::read, "/DynawoSimulationIDAParameters.json");
    }
}
