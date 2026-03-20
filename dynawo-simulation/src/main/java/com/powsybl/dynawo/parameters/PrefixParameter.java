/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * Used to create a proper parameter with {@link ParametersSet#generateParametersFromPrefix(String, List) (String) generateParametersFromPrefix} method
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record PrefixParameter(String name, String componentId, ParameterType type, String value) {

    public PrefixParameter(@JsonProperty("name") String name,
                           @JsonProperty("componentId") String componentId,
                           @JsonProperty("type") ParameterType type,
                           @JsonProperty("value") String value) {
        this.name = Objects.requireNonNull(name);
        this.componentId = Objects.requireNonNull(componentId);
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
    }
}
