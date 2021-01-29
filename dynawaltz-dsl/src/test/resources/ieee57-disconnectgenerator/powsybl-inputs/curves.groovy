/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Generator

for (Bus bus : network.busBreakerView.buses) {
    if (bus.id == "_KANA___1_TN" || bus.id == "_TURN___2_TN" || bus.id == "_LOGA___3_TN" || bus.id == "_BEAV___6_TN" 
        || bus.id == "_CLIN___8_TN" || bus.id == "_SALT___9_TN" || bus.id == "_GLEN__12_TN") {
        curve {
            staticId bus.id
            variable "Upu_value"
        }
    }
}

for (Generator gen : network.generators) {
    curves {
        dynamicModelId gen.id
        variables "generator_omegaPu", "generator_PGen", "generator_QGen", "generator_UStatorPu"
    }
}
