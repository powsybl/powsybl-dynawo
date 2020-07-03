/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.powsybl.iidm.network.Load
import com.powsybl.iidm.network.Generator

for (Load load : network.loads) {
    LoadAlphaBeta {
        staticId load.id
        // modelId "BBM_" + load.id (modelId could be optional and equal to staticId)
        parameterSetId "default"
       // the parameterSetId is a string that points to the requested entry in the aggregated par file defined in config.yml
    }
}

for (Generator gen : network.generators) {
    GeneratorSynchronousFourWindingsProportionalRegulations {
        staticId gen.id
        modelId "BBM_" + gen.id
        parameterSetId "default"
    }
    OmegaRef {
        generatorDynamicModelId "BBM_" + gen.id
    }
}
