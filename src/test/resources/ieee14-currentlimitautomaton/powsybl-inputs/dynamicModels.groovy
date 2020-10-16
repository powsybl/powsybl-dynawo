/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


import com.powsybl.iidm.network.Branch
import com.powsybl.iidm.network.Line
import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Generator


for (Load load : network.loads) {
    if (load.id != "_LOAD___8_EC") {
        LoadAlphaBeta {
            staticId load.id
            dynamicModelId "BBM" + load.id
            parameterSetId "LAB"
        }
    }
}

for (Generator gen : network.generators) {
    if (gen.id != "_GEN____6_SM" && gen.id != "_GEN____8_SM") {
        GeneratorSynchronousFourWindingsProportionalRegulations {
            staticId gen.id
            dynamicModelId "BBM" + gen.id
            parameterSetId "GSFWPR" + gen.id
        }
    } else {
        GeneratorSynchronousThreeWindingsProportionalRegulations {
            staticId gen.id
            dynamicModelId "BBM" + gen.id
            parameterSetId "GSTWPR" + gen.id
        }
    }
    OmegaRef {
        generatorDynamicModelId "BBM" + gen.id
    }
}

for (Line line : network.lines) {
    if (line.id == "_BUS____2-BUS____4-1_AC" || line.id == "_BUS____2-BUS____5-1_AC") {
        CurrentLimitAutomaton {
            staticId line.id
            dynamicModelId "AM" + line.id
            parameterSetId "CLA" + line.id
            side Branch.Side.TWO
        }
    }
}
