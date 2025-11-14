/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationParametersJsonModule extends SimpleModule {
    public CriticalTimeCalculationParametersJsonModule() {
        addDeserializer(CriticalTimeCalculationParameters.class, new CriticalTimeCalculationParametersDeserializer());
        addSerializer(CriticalTimeCalculationParameters.class, new CriticalTimeCalculationParametersSerializer());
    }
}
