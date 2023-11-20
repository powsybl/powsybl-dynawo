/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons.loadmerge;

/**
 * @author Laurent Isertial {@literal <laurent.issertial at rte-france.com>}
 */
public enum LoadPowersSigns {
    P_POS_Q_POS(".pppq"),
    P_POS_Q_NEG(".ppnq"),
    P_NEG_Q_POS(".nppq"),
    P_NEG_Q_NEG(".npnq");

    private final String mergeLoadSuffixId;

    LoadPowersSigns(String suffix) {
        mergeLoadSuffixId = suffix;
    }

    public String getMergeLoadSuffixId() {
        return mergeLoadSuffixId;
    }
}
