/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.loadsvariation;

import com.powsybl.iidm.modification.scalable.LoadScalable;
import com.powsybl.iidm.network.Load;

/**
 * The scaled P and Q are not set in the given load but kept in properties, contrary to {@link LoadScalable}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class CalculatedLoadScalable extends LoadScalable implements CalculatedPower {

    private Double calculatedP0 = Double.NaN;
    private Double calculatedQ0 = Double.NaN;

    CalculatedLoadScalable(String id) {
        super(id);
    }

    @Override
    protected void setP0(Load l, double value) {
        calculatedP0 = value;
    }

    @Override
    protected double getP0(Load l) {
        return calculatedP0.isNaN() ? l.getP0() : calculatedP0;
    }

    @Override
    protected void setQ0(Load l, double value) {
        calculatedQ0 = value;
    }

    @Override
    protected double getQ0(Load l) {
        return calculatedQ0.isNaN() ? l.getQ0() : calculatedQ0;
    }

    @Override
    public Double getCalculatedP0() {
        return calculatedP0;
    }

    @Override
    public Double getCalculatedQ0() {
        return calculatedQ0;
    }
}
