/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.powsybl.iidm.network.Generator
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.TwoSides

for (Load load : network.loads) {
    if (load.id != "_LOAD___8_EC") {
        LoadAlphaBeta {
            staticId load.id
            parameterSetId "LAB"
        }
    }
}

for (Generator gen : network.generators) {
    if (gen.id != "_GEN____6_SM" && gen.id != "_GEN____8_SM") {
        GeneratorSynchronousFourWindingsProportionalRegulations {
            staticId gen.id
            parameterSetId "GSFWPR" + gen.id
        }
    } else {
        GeneratorSynchronousThreeWindingsProportionalRegulations {
            staticId gen.id
            parameterSetId "GSTWPR" + gen.id
        }
    }
}

CurrentLimitAutomaton {
    dynamicModelId "CLA_2_4"
    parameterSetId "CLA_2_4"
    controlledBranch "_BUS____2-BUS____4-1_AC"
    iMeasurement "_BUS____2-BUS____4-1_AC"
    iMeasurementSide TwoSides.TWO
}

CurrentLimitAutomaton {
    dynamicModelId "CLA_2_5"
    parameterSetId "CLA_2_5"
    controlledBranch "_BUS____2-BUS____5-1_AC"
    iMeasurement "_BUS____2-BUS____5-1_AC"
    iMeasurementSide TwoSides.TWO
}

