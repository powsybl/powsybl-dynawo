/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers.dynamicmodels;

import org.gridsuite.filter.wip.ExpertFilter;

import java.util.Objects;

/**
 * Dynamic multiple models configuration deserialized by {@link DynamicModelConfigsJsonDeserializer}
 *
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record DynamicMultipleModel(String model, String group, ExpertFilter rule) {

    public DynamicMultipleModel(String model, String group, ExpertFilter rule) {
        this.model = Objects.requireNonNull(model);
        this.group = Objects.requireNonNull(group);
        this.rule = Objects.requireNonNull(rule);
    }
}
