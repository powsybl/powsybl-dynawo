/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DefaultModelFactory<T> {

    private final Map<String, T> defaultModelMap = new HashMap<>();

    private final Function<String, T> modelConstructor;

    DefaultModelFactory(Function<String, T> modelConstructor) {
        this.modelConstructor = modelConstructor;
    }

    public T getDefaultModel(String staticId) {
        return defaultModelMap.computeIfAbsent(staticId, key -> modelConstructor.apply(staticId));
    }
}
