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
 * Configures the dynamic simulation phase 2
 * @param phase2stopTime Simulation phase 2 stop time, start time will be equal to phase 1 stop time
 * @param phase2ModelsPredicate Discriminate models used only during phase 2
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record Phase2Config(double phase2stopTime, Predicate<BlackBoxModel> phase2ModelsPredicate) {
}
