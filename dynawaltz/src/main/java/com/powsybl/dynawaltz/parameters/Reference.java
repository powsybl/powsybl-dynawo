/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.parameters;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public record Reference(String name, ParameterType type, String origData, String origName) {

    public Reference(@JsonProperty("name") String name,
                     @JsonProperty("type") ParameterType type,
                     @JsonProperty("origData") String origData,
                     @JsonProperty("origName") String origName) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.origData = Objects.requireNonNull(origData);
        this.origName = Objects.requireNonNull(origName);
    }
}
