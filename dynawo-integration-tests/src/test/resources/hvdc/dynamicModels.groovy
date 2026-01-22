/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package hvdc

import com.powsybl.iidm.network.Load

GeneratorSynchronousFourWindings {
    staticId "_Generator___1"
    parameterSetId "1"
}

for (Load load : network.loads) {
    LoadAlphaBeta {
        staticId load.id
        parameterSetId "3"
    }
}

HvdcPV {
    staticId "HVDC1"
    parameterSetId "4"
}

HvdcPV {
    staticId "HVDC2"
    parameterSetId "5"
}

PhaseShifterI {
    dynamicModelId "PS_NGEN_NHV1"
    parameterSetId "PS"
    transformer "_BUS____5-BUS____6-1_PS"
}

PhaseShifterP {
    dynamicModelId "PS_NGEN_NHV1"
    parameterSetId "PS"
    transformer "_BUS____5-BUS____6-1_PS"
}




