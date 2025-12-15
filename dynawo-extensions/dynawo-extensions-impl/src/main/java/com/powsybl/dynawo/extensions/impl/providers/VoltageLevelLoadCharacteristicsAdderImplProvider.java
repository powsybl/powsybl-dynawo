/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.providers;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.ExtensionAdderProvider;
import com.powsybl.dynawo.extensions.api.voltage.VoltageLevelLoadCharacteristics;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.dynawo.extensions.impl.voltage.VoltageLevelLoadCharacteristicsAdderImpl;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
@AutoService(ExtensionAdderProvider.class)
public class VoltageLevelLoadCharacteristicsAdderImplProvider implements
        ExtensionAdderProvider<VoltageLevel, VoltageLevelLoadCharacteristics, VoltageLevelLoadCharacteristicsAdderImpl> {

    @Override
    public String getImplementationName() {
        return "Default";
    }

    @Override
    public String getExtensionName() {
        return VoltageLevelLoadCharacteristics.NAME;
    }

    @Override
    public Class<VoltageLevelLoadCharacteristicsAdderImpl> getAdderClass() {
        return VoltageLevelLoadCharacteristicsAdderImpl.class;
    }

    @Override
    public VoltageLevelLoadCharacteristicsAdderImpl newAdder(VoltageLevel voltageLevel) {
        return new VoltageLevelLoadCharacteristicsAdderImpl(voltageLevel);
    }
}
