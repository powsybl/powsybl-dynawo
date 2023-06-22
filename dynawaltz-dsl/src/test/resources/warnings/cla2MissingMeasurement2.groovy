/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package warnings

import com.powsybl.iidm.network.Branch

CurrentLimitAutomatonTwoLevels {
    dynamicModelId "CLA_NGEN"
    parameterSetId "CLA"
    controlledQuadripole "NHV1_NHV2_2"
    iMeasurement1 "NHV1_NHV2_1"
    iMeasurement1Side Branch.Side.TWO
    iMeasurement2 "NGEN"
    iMeasurement2Side Branch.Side.ONE
}
