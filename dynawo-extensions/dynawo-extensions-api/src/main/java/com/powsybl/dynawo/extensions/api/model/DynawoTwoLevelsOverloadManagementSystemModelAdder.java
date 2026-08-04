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
public interface DynawoTwoLevelsOverloadManagementSystemModelAdder<B extends Branch<B>>
        extends DynawoAutomationSystemModelAdder<B, DynawoTwoLevelsOverloadManagementSystemModel<B>, DynawoTwoLevelsOverloadManagementSystemModelAdder<B>> {

    @Override
    default Class<DynawoTwoLevelsOverloadManagementSystemModel> getExtensionClass() {
        return DynawoTwoLevelsOverloadManagementSystemModel.class;
    }

    DynawoTwoLevelsOverloadManagementSystemModelAdder<B> withIMeasurement1(String iMeasurement);

    DynawoTwoLevelsOverloadManagementSystemModelAdder<B> withIMeasurement1Side(TwoSides iMeasurementSide);

    DynawoTwoLevelsOverloadManagementSystemModelAdder<B> withIMeasurement2(String iMeasurement);

    DynawoTwoLevelsOverloadManagementSystemModelAdder<B> withIMeasurement2Side(TwoSides iMeasurementSide);
}
