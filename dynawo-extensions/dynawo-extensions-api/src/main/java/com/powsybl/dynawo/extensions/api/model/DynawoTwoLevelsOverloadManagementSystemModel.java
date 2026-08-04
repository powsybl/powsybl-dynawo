/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.model;

import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface DynawoTwoLevelsOverloadManagementSystemModel<B extends Branch<B>>
        extends DynawoAutomationSystemModelExtension<B, DynawoTwoLevelsOverloadManagementSystemModel<B>> {

    String NAME = "dynawoTwoLevelsOverloadManagementSystemModel";

    @Override
    default String getName() {
        return NAME;
    }

    String getIMeasurement1();

    DynawoTwoLevelsOverloadManagementSystemModel<B> setIMeasurement1(String iMeasurement);

    TwoSides getIMeasurement1Side();

    DynawoTwoLevelsOverloadManagementSystemModel<B> setIMeasurement1Side(TwoSides iMeasurementSide);

    String getIMeasurement2();

    DynawoTwoLevelsOverloadManagementSystemModel<B> setIMeasurement2(String iMeasurement);

    TwoSides getIMeasurement2Side();

    DynawoTwoLevelsOverloadManagementSystemModel<B> setIMeasurement2Side(TwoSides iMeasurementSide);
}
