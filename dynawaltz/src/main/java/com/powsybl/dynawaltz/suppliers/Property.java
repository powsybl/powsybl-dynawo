/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers;


/**
 * {@link com.powsybl.dynawaltz.suppliers.dynamicmodels.DynamicModelConfig} and {@link com.powsybl.dynawaltz.suppliers.events.EventModelConfig} property
 * @param name property name corresponding to {@link com.powsybl.dynawaltz.builders.ModelBuilder} method name
 * @param value property value
 * @param propertyClass {@link #value} class
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record Property(String name, Object value, Class<?> propertyClass) {
}
