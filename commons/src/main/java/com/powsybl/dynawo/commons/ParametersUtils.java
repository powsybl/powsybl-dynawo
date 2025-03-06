/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class ParametersUtils {

    public static final String PROPERTY_LIST_DELIMITER = ",";

    public static <E extends Enum<E>> List<Object> getEnumPossibleValues(Class<E> enumClass) {
        return EnumSet.allOf(enumClass).stream().map(Enum::name).collect(Collectors.toList());
    }

    public static void addNotNullEntry(String key, Object value, BiConsumer<String, String> adder) {
        if (value != null) {
            adder.accept(key, Objects.toString(value));
        }
    }

    private ParametersUtils() {
    }
}
