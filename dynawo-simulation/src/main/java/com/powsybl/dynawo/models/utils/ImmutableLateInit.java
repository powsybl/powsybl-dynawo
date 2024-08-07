/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.utils;

import com.powsybl.commons.PowsyblException;

import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ImmutableLateInit<T> {

    private T value = null;

    public final T getValue() {
        if (value == null) {
            throw new PowsyblException("Field has not been initialized");
        }
        return value;
    }

    public final void setValue(T value) {
        if (this.value != null) {
            throw new PowsyblException("Field has already been initialized");
        }
        this.value = Objects.requireNonNull(value);
    }
}
