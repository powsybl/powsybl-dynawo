/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationParametersJsonModule extends SimpleModule {

    public MarginCalculationParametersJsonModule() {
        addDeserializer(MarginCalculationParameters.class, new MarginCalculationParametersDeserializer());
        addSerializer(MarginCalculationParameters.class, new MarginCalculationParametersSerializer());
    }
}
