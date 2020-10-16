/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Line
import com.powsybl.iidm.network.Load

for (Bus bus : network.busBreakerView.buses) {
    curve {
        staticId bus.id
        variable "Upu_value"
    }
}

for (Generator gen : network.generators) {
    curves {
        dynamicModelId "BBM" + gen.id
        variables "generator_omegaPu", "generator_PGen", "generator_QGen", "generator_UStatorPu", "voltageRegulator_EfdPu"
    }
}

for (Load load : network.loads) {
    if (load.id == "_LOAD___2_EC") {
        curve {
            dynamicModelId "BBM" + load.id
            variables "load_PPu", "load_QPu"
        }
    }
}

for (Line line : network.lines) {
    if (line.id == "_BUS____2-BUS____4-1_AC" || line.id == "_BUS____2-BUS____5-1_AC") {
        curve {
            staticId line.id
            variables "iSide2", "state"
        }
        curve {
            dynamicModelId "AM" + line.id
            variables "currentLimitAutomaton_order", "currentLimitAutomaton_IMax"
        }
    }
}
