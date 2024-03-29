/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package dynamicModels

List[] measurements = [["OldId", "NGEN", "NHV1"], ["NHV1", "OldId"], ["NHV2"]]

TapChangerBlockingAutomaton {
    dynamicModelId "ZAB"
    parameterSetId "ZAB"
    uMeasurements measurements
    transformers "NGEN_NHV1", "NHV2_NLOAD", "LOAD"
}
