/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.iidm.network.Load;

import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public record LoadsVariation(List<Load> loads, double variationValue, VariationMode variationMode) {

    //TODO complete
    public enum VariationMode {
        PROPORTIONAL
    }
}