/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package ieee14.mc

LoadAlphaBeta {
    staticId "_LOAD___3_EC"
    parameterSetId "LAB"
}

LoadAlphaBeta {
    staticId "_LOAD___6_EC"
    parameterSetId "LAB"
}

LoadAlphaBeta {
    staticId "_LOAD__10_EC"
    parameterSetId "LAB"
}

for (gen in ["_GEN____1_SM", "_GEN____2_SM", "_GEN____3_SM"]) {
    GeneratorSynchronousFourWindingsProportionalRegulations {
        staticId gen
        parameterSetId "GSFWPR" + gen
    }
}

for (gen in ["_GEN____6_SM", "_GEN____8_SM"]) {
    GeneratorSynchronousThreeWindingsProportionalRegulations {
        staticId gen
        parameterSetId "GSTWPR" + gen
    }
}