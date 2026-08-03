/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.model;

import com.powsybl.iidm.network.Branch;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface DynawoOverloadManagementSystemModel<B extends Branch<B>>
        extends DynawoAutomationSystemModelExtension<B, DynawoOverloadManagementSystemModel<B>> {

    String NAME = "dynawoOverloadManagementSystemModel";

    @Override
    default String getName() {
        return NAME;
    }

    String getIMeasurement();

    DynawoOverloadManagementSystemModel<B> setIMeasurement(String iMeasurement);

    String getIMeasurementSide();

    DynawoOverloadManagementSystemModel<B> setIMeasurementSide(String iMeasurementSide);
}
