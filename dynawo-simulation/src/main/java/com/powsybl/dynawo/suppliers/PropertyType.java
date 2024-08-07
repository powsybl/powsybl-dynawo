/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers;

import com.powsybl.iidm.network.TwoSides;

import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public enum PropertyType {

    TWO_SIDES(TwoSides.class, TwoSides::valueOf),
    BOOLEAN(boolean.class, Boolean::parseBoolean),
    INTEGER(int.class, Integer::parseInt),
    DOUBLE(double.class, Double::parseDouble),
    STRING(String.class, value -> value);

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
        return this == STRING;
    }
}
