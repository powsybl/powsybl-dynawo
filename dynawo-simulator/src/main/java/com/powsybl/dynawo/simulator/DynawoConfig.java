/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.dynamic.simulation.DynamicSimulationParameters;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoConfig extends AbstractExtension<DynamicSimulationParameters> {

    private static final String DEFAULT_DYNAWO_CMD_NAME = "myEnvDynawo.sh";

    public DynawoConfig() {
        debug = false;
        dynawoCptCommandName = DEFAULT_DYNAWO_CMD_NAME;
    }

    @Override
    public String getName() {
        return "DynawoConfig";
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getDynawoCptCommandName() {
        return dynawoCptCommandName;
    }

    public void setDynawoCptCommandName(String dynawoCptCommandName) {
        this.dynawoCptCommandName = dynawoCptCommandName;
    }

    private boolean debug;
    private String dynawoCptCommandName;
}
