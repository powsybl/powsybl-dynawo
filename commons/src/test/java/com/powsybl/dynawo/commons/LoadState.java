/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

/**
 * @author Laurent Isertial <laurent.issertial at rte-france.com>
 */
public final class LoadState {

    private final double p;
    private final double q;
    private final double p0;
    private final double q0;

    public LoadState(double p, double q, double p0, double q0) {
        this.p = p;
        this.q = q;
        this.p0 = p0;
        this.q0 = q0;
    }

    public double getP0() {
        return p0;
    }

    public double getQ0() {
        return q0;
    }

    public double getP() {
        return p;
    }

    public double getQ() {
        return q;
    }
}
