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
 */
public class Reference {

    private final String name;
    private final ParameterType type;
    private final String origData;
    private final String origName;
    private final String componentId;

    public Reference(@JsonProperty("name") String name,
                     @JsonProperty("type") ParameterType type,
                     @JsonProperty("origData") String origData,
                     @JsonProperty("origName") String origName,
                     @JsonProperty("componentId") String componentId) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.origData = Objects.requireNonNull(origData);
        this.origName = Objects.requireNonNull(origName);
        this.componentId = componentId;
    }

    public String getName() {
        return name;
    }

    public ParameterType getType() {
        return type;
    }

    public String getOrigData() {
        return origData;
    }

    public String getOrigName() {
        return origName;
    }

    public String getComponentId() {
        return componentId;
    }
}
