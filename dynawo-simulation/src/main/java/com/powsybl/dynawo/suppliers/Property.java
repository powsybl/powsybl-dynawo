/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers;

import com.powsybl.dynawo.builders.ModelBuilder;
import com.powsybl.dynawo.suppliers.dynamicmodels.DynamicModelConfig;
import com.powsybl.dynawo.suppliers.events.EventModelConfig;

/**
 * {@link DynamicModelConfig} and {@link EventModelConfig} property
 * @param name property name corresponding to {@link ModelBuilder} method name
 * @param value property value
 * @param propertyClass {@link #value} class
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record Property(String name, Object value, Class<?> propertyClass) {
}
