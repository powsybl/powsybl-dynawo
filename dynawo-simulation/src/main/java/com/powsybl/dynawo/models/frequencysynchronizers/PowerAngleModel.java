/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.frequencysynchronizers;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
//TODO rename
public interface PowerAngleModel extends FrequencySynchronizedModel {

    //TODO remove default - check the proper value for every synchronous gen
    default String getThetaVarName() {
        return "generator_theta";
    }
}
