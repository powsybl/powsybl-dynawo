/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import com.powsybl.commons.extensions.AbstractExtension;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoInputProvider;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParameters extends AbstractExtension<DynamicSimulationParameters> {

    private static final String DEFAULT_DYNAWO_CMD_NAME = "myEnvDynawo.sh";

    public DynawoParameters() {
        this(false, DEFAULT_DYNAWO_CMD_NAME, null);
    }

    public DynawoParameters(boolean debug, String dynawoCommandName, DynawoInputProvider dynawoInputProvider) {
        this.debug = debug;
        this.dynawoCommandName = dynawoCommandName;
        this.dynawoInputProvider = dynawoInputProvider;
    }

    @Override
    public String getName() {
        return "DynawoConfig";
    }

    public boolean isDebug() {
        return debug;
    }

    public DynawoParameters setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public String getDynawoCommandName() {
        return dynawoCommandName;
    }

    public DynawoParameters setDynawoCommandName(String dynawoCommandName) {
        this.dynawoCommandName = dynawoCommandName;
        return this;
    }

    public DynawoInputProvider getDynawoInputProvider() {
        return dynawoInputProvider;
    }

    public DynawoParameters setDynawoInputProvider(DynawoInputProvider dynawoInputProvider) {
        this.dynawoInputProvider = dynawoInputProvider;
        return this;
    }

    private boolean debug;
    private String dynawoCommandName;
    private DynawoInputProvider dynawoInputProvider;

}
