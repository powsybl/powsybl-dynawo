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
import com.powsybl.iidm.network.TwoSides

for (Load load : network.loads) {
    if (load.id == "LOAD2") {
        LoadOneTransformer {
            staticId load.id
            // dynamicModelId "BBM_" + load.id (dynamicModelId could be optional and equal to staticId)
            parameterSetId "LOT"
           // the parameterSetId is a string that points to the requested entry in the aggregated par file defined in config.yml
        }
    } else {
        LoadAlphaBeta {
            staticId load.id
            // dynamicModelId "BBM_" + load.id (dynamicModelId could be optional and equal to staticId)
            parameterSetId "LAB"
           // the parameterSetId is a string that points to the requested entry in the aggregated par file defined in config.yml
        }
    }
}

for (Generator gen : network.generators) {
    if (gen.id == "GEN2") {
        GeneratorSynchronousFourWindingsProportionalRegulations {
            staticId gen.id
            dynamicModelId "BBM_" + gen.id
            parameterSetId "GSFWPR"
        }
    } else if (gen.id == "GEN3") {
        GeneratorSynchronousFourWindings {
            staticId gen.id
            dynamicModelId "BBM_" + gen.id
            parameterSetId "GSFW"
        }
    } else if (gen.id == "GEN4") {
        GeneratorSynchronousThreeWindings {
            staticId gen.id
            dynamicModelId "BBM_" + gen.id
            parameterSetId "GSTW"
        }
    } else if (gen.id == "GEN6") {
        GeneratorFictitious {
            staticId gen.id
            dynamicModelId "BBM_" + gen.id
            parameterSetId "GF"
        }
    } else if (gen.id == "GEN7") {
        GeneratorPQ {
            staticId gen.id
            dynamicModelId "BBM_" + gen.id
            parameterSetId "GPQ"
        }
    } else {
        GeneratorSynchronousThreeWindingsProportionalRegulations {
            staticId gen.id
            dynamicModelId "BBM_" + gen.id
            parameterSetId "GSTWPR"
        }
    }
}

for (Line line : network.lines) {
    OverloadManagementSystem {
        dynamicModelId "BBM_" + line.id
        parameterSetId "CLA"
        iMeasurement line.id
        iMeasurementSide TwoSides.TWO
    }

    Line {
        staticId line.id
        dynamicModelId "BBM_" + line.id
        parameterSetId "SL"
    }
}

for (Bus bus : network.busBreakerView.buses) {
    Bus {
        staticId bus.id
        dynamicModelId "BBM_" + bus.id
        parameterSetId "SB"
    }
}
