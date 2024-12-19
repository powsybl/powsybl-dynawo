/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package ieee14.signal_n

GeneratorPVSignalN {
    staticId "_GEN____1_SM"
    parameterSetId "Generator1"
}

GeneratorPVSignalN {
    staticId "_GEN____2_SM"
    parameterSetId "Generator2"
}

GeneratorPVSignalN {
    staticId "_GEN____3_SM"
    parameterSetId "Generator3"
}

GeneratorPVSignalN {
    staticId "_GEN____6_SM"
    parameterSetId "Generator6"
}

GeneratorPVSignalN {
    staticId "_GEN____8_SM"
    parameterSetId "Generator8"
}

for (load in network.loads) {
    LoadAlphaBetaRestorative {
        staticId load.id
        parameterSetId "GenericRestorativeLoad"
    }
}