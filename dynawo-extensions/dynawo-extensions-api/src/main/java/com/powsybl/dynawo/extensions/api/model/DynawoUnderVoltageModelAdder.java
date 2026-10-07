/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.model;

import com.powsybl.iidm.network.Generator;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface DynawoUnderVoltageModelAdder extends DynawoAutomationSystemModelAdder<Generator, DynawoUnderVoltageModel, DynawoUnderVoltageModelAdder> {

    @Override
    default Class<DynawoUnderVoltageModel> getExtensionClass() {
        return DynawoUnderVoltageModel.class;
    }
}
