/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.versionablevariable;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractVersionable<T> implements Versionable<T> {

    private final String name;
    private final List<Versionable.Step<T>> steps;

    @SafeVarargs
    protected AbstractVersionable(String name, Versionable.Step<T>... steps) {
        this.name = name;
        this.steps = List.of(steps);
    }

    public String getName() {
        return name;
    }

    @Override
    public List<Step<T>> getSteps() {
        return steps;
    }
}
