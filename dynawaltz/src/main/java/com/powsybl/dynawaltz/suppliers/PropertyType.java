/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers;

import com.powsybl.iidm.network.TwoSides;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public enum PropertyType {

    TWO_SIDES(TwoSides.class, TwoSides::valueOf),
    BOOLEAN(boolean.class, Boolean::parseBoolean),
    INTEGER(int.class, Integer::parseInt),
    DOUBLE(double.class, Double::parseDouble),
    STRING(String.class, value -> value),
    STRINGS(Collection.class, List::of),
    STRINGS_ARRAYS(Collection[].class, s -> new Collection[]{List.of(s)});

    private static final EnumSet<PropertyType> CONVERSION_FREE_TYPES = EnumSet.of(STRING, STRINGS, STRINGS_ARRAYS);

    private final Class<?> propertyClass;
    private final Function<String, Object> valueConvertor;

    PropertyType(Class<?> propertyClass, Function<String, Object> valueConvertor) {
        this.propertyClass = propertyClass;
        this.valueConvertor = valueConvertor;
    }

    public Class<?> getPropertyClass() {
        return propertyClass;
    }

    public Object convertValue(String value) {
        return valueConvertor.apply(value);
    }

    public boolean isConversionFree() {
        return CONVERSION_FREE_TYPES.contains(this);
    }
}
