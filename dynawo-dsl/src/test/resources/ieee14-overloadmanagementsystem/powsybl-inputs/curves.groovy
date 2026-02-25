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
        id bus.id
        variable "Upu_value"
    }
}

for (Generator gen : network.generators) {
    curve {
        id gen.id
        variables "generator_omegaPu", "generator_PGen", "generator_QGen", "generator_UStatorPu", "voltageRegulator_EfdPu"
    }
}

curve {
    id "_LOAD___2_EC"
    variables "load_PPu", "load_QPu"
}

curve {
    id "_BUS____2-BUS____4-1_AC"
    variables "iSide2", "state"
}

curve {
    id "CLA_2_4"
    variables "currentLimitAutomaton_order", "currentLimitAutomaton_IMax"
}

curve {
    id "_BUS____1-BUS____5-1_AC"
    variables "iSide2", "state"
}

curve {
    id "_BUS____2-BUS____5-1_AC"
    variables "iSide2", "state"
}

curve {
    id "CLA_2_5"
    variables "currentLimitAutomaton_order", "currentLimitAutomaton_IMax"
}
