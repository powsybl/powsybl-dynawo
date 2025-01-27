/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.loadsvariation;

import com.powsybl.iidm.modification.scalable.ProportionalScalable;
import com.powsybl.iidm.network.Load;

import java.util.List;

import static com.powsybl.iidm.modification.scalable.ProportionalScalable.DistributionMode.PROPORTIONAL_TO_P0;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadsProportionalScalable extends ProportionalScalable {

    public LoadsProportionalScalable(List<Load> loads) {
        super(loads, l ->
                new CalculatedLoadScalable((Load) l), PROPORTIONAL_TO_P0, -Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public List<CalculatedPowerDelta> getLoadScalable() {
        return getScalables().stream().map(CalculatedPowerDelta.class::cast).toList();
    }
}
