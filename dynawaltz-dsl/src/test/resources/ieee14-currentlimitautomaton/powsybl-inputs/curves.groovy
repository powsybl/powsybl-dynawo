/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Generator

for (Bus bus : network.busBreakerView.buses) {
    curve {
        staticId bus.id
        variable "Upu_value"
    }
}

for (Generator gen : network.generators) {
    curves {
        dynamicModelId gen.id
        variables "generator_omegaPu", "generator_PGen", "generator_QGen", "generator_UStatorPu", "voltageRegulator_EfdPu"
    }
}

curves {
    dynamicModelId "_LOAD___2_EC"
    variables "load_PPu", "load_QPu"
}

curves {
    staticId "_BUS____2-BUS____4-1_AC"
    variables "iSide2", "state"
}

curves {
    dynamicModelId "CLA_2_4"
    variables "currentLimitAutomaton_order", "currentLimitAutomaton_IMax"
}

curves {
    staticId "_BUS____1-BUS____5-1_AC"
    variables "iSide2", "state"
}

curves {
    staticId "_BUS____2-BUS____5-1_AC"
    variables "iSide2", "state"
}

curves {
    dynamicModelId "CLA_2_5"
    variables "currentLimitAutomaton_order", "currentLimitAutomaton_IMax"
}
