/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.voltage;

import com.powsybl.commons.extensions.AbstractExtensionAdder;
import com.powsybl.commons.extensions.ExtensionAdder;
import com.powsybl.dynawo.extensions.api.voltage.VoltageLevelLoadCharacteristics;
import com.powsybl.dynawo.extensions.api.voltage.VoltageLevelLoadCharacteristicsAdder;
import com.powsybl.iidm.network.VoltageLevel;

import java.util.Objects;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public class VoltageLevelLoadCharacteristicsAdderImpl extends AbstractExtensionAdder<VoltageLevel, VoltageLevelLoadCharacteristics> implements VoltageLevelLoadCharacteristicsAdder, ExtensionAdder<VoltageLevel, VoltageLevelLoadCharacteristics> {

    private VoltageLevelLoadCharacteristics.Type characteristic;

    public VoltageLevelLoadCharacteristicsAdderImpl(VoltageLevel voltageLevel) {
        super(voltageLevel);
    }

    @Override
    protected VoltageLevelLoadCharacteristics createExtension(VoltageLevel voltageLevel) {
        return new VoltageLevelLoadCharacteristicsImpl(voltageLevel, characteristic);
    }

    @Override
    public VoltageLevelLoadCharacteristicsAdder withCharacteristic(VoltageLevelLoadCharacteristics.Type characteristic) {
        this.characteristic = Objects.requireNonNull(characteristic);
        return this;
    }
}
