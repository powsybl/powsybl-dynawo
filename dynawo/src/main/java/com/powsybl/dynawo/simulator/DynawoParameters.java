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

    public DynawoParameters() {
        this(null);
    }

    public DynawoParameters(DynawoInputProvider dynawoInputProvider) {
        this.dynawoInputProvider = dynawoInputProvider;
    }

    @Override
    public String getName() {
        return "DynawoParameters";
    }

    public DynawoInputProvider getDynawoInputProvider() {
        return dynawoInputProvider;
    }

    public DynawoParameters setDynawoInputProvider(DynawoInputProvider dynawoInputProvider) {
        this.dynawoInputProvider = dynawoInputProvider;
        return this;
    }

    private DynawoInputProvider dynawoInputProvider;

}
