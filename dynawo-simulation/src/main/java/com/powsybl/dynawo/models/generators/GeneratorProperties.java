/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
final class GeneratorProperties {

    static final String DEFAULT_SWITCH_OFF_SIGNAL = "generator_switchOffSignal";
    static final String DEFAULT_OMEGA_PU = "generator_omegaPu";
    static final String DEFAULT_OMEGA_REF_PU = "generator_omegaRefPu";
    static final String DEFAULT_RUNNING = "generator_running";
    static final String GENERATOR_STATE = "generator_state";
    static final String STATE = "state";

    private GeneratorProperties() {
    }
}
