/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers;

import com.powsybl.dynawaltz.suppliers.dynamicmodels.DynamicModelConfigsJsonDeserializer;
import com.powsybl.dynawaltz.suppliers.events.EventModelConfigsJsonDeserializer;

import java.util.Collection;
import java.util.List;

/**
 * Builds {@link Property} in configuration Json deserializers
 * @see DynamicModelConfigsJsonDeserializer
 * @see EventModelConfigsJsonDeserializer
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PropertyBuilder {

    private String name;
    private String value;
    private List<String> values;
    private List<List<String>> arrays;
    private PropertyType type;
    private CollectionType collectionType = CollectionType.SINGLE;

    private enum CollectionType {
        SINGLE,
        LIST,
        LIST_ARRAY
    }

    public Property build() {
        return switch (collectionType) {
            case SINGLE -> new Property(name, type.isConversionFree() ? value : type.convertValue(value), type.getPropertyClass());
            case LIST -> new Property(name, type.isConversionFree() ? values : values.stream().map(v -> type.convertValue(v)).toList(), Collection.class);
            case LIST_ARRAY -> new Property(name, arrays.toArray(new List[0]), Collection[].class);
        };
    }

    public PropertyBuilder name(String name) {
        this.name = name;
        return this;
    }

    public PropertyBuilder value(String value) {
        this.value = value;
        return this;
    }

    //TODO add values... ?
    public PropertyBuilder values(List<String> values) {
        collectionType = CollectionType.LIST;
        this.values = values;
        return this;
    }

    public PropertyBuilder arrays(List<List<String>> arrays) {
        collectionType = CollectionType.LIST_ARRAY;
        this.arrays = arrays;
        return this;
    }

    public PropertyBuilder type(PropertyType type) {
        this.type = type;
        return this;
    }
}
