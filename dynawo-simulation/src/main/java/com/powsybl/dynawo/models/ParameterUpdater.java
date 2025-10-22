/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models;

import com.powsybl.dynawo.parameters.ParameterType;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@FunctionalInterface
public interface ParameterUpdater {

    void addParameter(String parameterSetId, String name, ParameterType type, String value);
}
