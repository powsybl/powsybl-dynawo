/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.dynawo.models.BlackBoxModel;

import java.util.function.Predicate;

/**
 * Configures the dynamic simulation final step
 * @param stopTime Simulation final step stop time, start time will be equal to the first step stop time
 * @param modelsPredicate Discriminate models used only during final step
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record FinalStepConfig(double stopTime, Predicate<BlackBoxModel> modelsPredicate) {
}
