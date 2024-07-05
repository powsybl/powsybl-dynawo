/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package dynamicModels

def measurements = ["S1VL2_BBS1", "OldId", "S3VL1_BBS"]

TapChangerBlockingAutomaton {
    dynamicModelId "ZAB"
    parameterSetId "ZAB"
    uMeasurements measurements
    transformers "TWT", "LD1"
}
