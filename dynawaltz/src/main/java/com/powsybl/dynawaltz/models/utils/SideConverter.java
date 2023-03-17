/*
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.utils;

import com.powsybl.dynawaltz.models.Side;
import com.powsybl.iidm.network.Branch;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class SideConverter {

    private SideConverter() {
    }

    public static Side convert(Branch.Side side) {
        switch (side) {
            case ONE:
                return Side.ONE;
            case TWO:
                return Side.TWO;
            default:
                throw new AssertionError("Unexpected Side value: " + side);
        }
    }
}
