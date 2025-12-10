/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.api.voltage;

import com.powsybl.commons.extensions.ExtensionAdder;
import com.powsybl.iidm.network.VoltageLevel;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public interface VoltageLevelLoadCharacteristicsAdder
        extends ExtensionAdder<VoltageLevel, VoltageLevelLoadCharacteristics> {

    @Override
    default Class<VoltageLevelLoadCharacteristics> getExtensionClass() {
        return VoltageLevelLoadCharacteristics.class;
    }

    VoltageLevelLoadCharacteristicsAdder withCharacteristic(VoltageLevelLoadCharacteristics.Type characteristic);
}
