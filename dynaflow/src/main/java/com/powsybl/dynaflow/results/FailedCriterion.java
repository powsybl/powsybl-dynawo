/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow.results;

/**
 * scenarioResults or loadIncreaseResults failed criterion
 * @param message Failed criterion message
 * @param time Failure time (in seconds)
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record FailedCriterion(String message, double time) {
}