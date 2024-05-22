/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers;

import java.util.List;

/**
 * Builds {@link Property} in configuration Json deserializers
 * @see com.powsybl.dynawaltz.suppliers.dynamicmodels.DynamicModelConfigsJsonDeserializer
 * @see com.powsybl.dynawaltz.suppliers.events.EventModelConfigsJsonDeserializer
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PropertyBuilder {

    private String name;
    private Object value;
    private PropertyType type;

    public Property build() {
        return new Property(name, type.isConversionFree() ? value : type.convertValue((String) value), type.getPropertyClass());
    }

    public PropertyBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PropertyBuilder value(String value) {
        this.value = value;
        return this;
    }

    public PropertyBuilder values(List<String> values) {
        this.value = values;
        return this;
    }

    public PropertyBuilder arrays(List<List<String>> arrays) {
        this.value = arrays.toArray(new List[0]);
        return this;
    }

    public PropertyBuilder type(PropertyType type) {
        this.type = type;
        return this;
    }
}
