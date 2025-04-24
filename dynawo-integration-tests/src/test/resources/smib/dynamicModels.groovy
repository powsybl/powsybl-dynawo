/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package smib

InfiniteBus {
    staticId "VL1_BUS1"
    parameterSetId "1"
}

Bus {
    staticId "VL2_BUS1"
    parameterSetId "1"
}

Bus {
    staticId "VL3_BUS1"
    parameterSetId "1"
}

Line {
    staticId "line1"
    parameterSetId "2"
}

Line {
    staticId "line2"
    parameterSetId "2"
}

TransformerFixedRatio {
    staticId "TR"
    parameterSetId "4"
}

GeneratorSynchronousFourWindingsProportionalRegulations {
    staticId "SM"
    parameterSetId "5"
}
