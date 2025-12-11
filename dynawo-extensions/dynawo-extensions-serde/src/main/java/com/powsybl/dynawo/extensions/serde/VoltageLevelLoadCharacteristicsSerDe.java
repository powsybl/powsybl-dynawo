/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawo.extensions.serde;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.AbstractExtensionSerDe;
import com.powsybl.commons.extensions.ExtensionSerDe;
import com.powsybl.commons.io.DeserializerContext;
import com.powsybl.commons.io.SerializerContext;
import com.powsybl.dynawo.extensions.api.voltage.VoltageLevelLoadCharacteristics;
import com.powsybl.dynawo.extensions.api.voltage.VoltageLevelLoadCharacteristicsAdder;
import com.powsybl.iidm.network.VoltageLevel;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class VoltageLevelLoadCharacteristicsSerDe extends AbstractExtensionSerDe<VoltageLevel, VoltageLevelLoadCharacteristics> {

    public VoltageLevelLoadCharacteristicsSerDe() {
        super(VoltageLevelLoadCharacteristics.NAME, "network", VoltageLevelLoadCharacteristics.class, "voltageLevelLoadCharacteristics.xsd",
                "http://www.powsybl.org/schema/iidm/ext/voltageLevel_load_characteristics/1_0", "vllc");
    }

    @Override
    public void write(VoltageLevelLoadCharacteristics voltageLevelLoadCharacteristics, SerializerContext context) {
        context.getWriter().writeEnumAttribute("characteristic", voltageLevelLoadCharacteristics.getCharacteristic());
    }

    @Override
    public VoltageLevelLoadCharacteristics read(VoltageLevel voltageLevel, DeserializerContext context) {
        VoltageLevelLoadCharacteristics.Type characteristic = context.getReader().readEnumAttribute("characteristic", VoltageLevelLoadCharacteristics.Type.class);
        context.getReader().readEndNode();
        return voltageLevel.newExtension(VoltageLevelLoadCharacteristicsAdder.class)
                .withCharacteristic(characteristic)
                .add();
    }
}
