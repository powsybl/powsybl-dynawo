/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package itools

import com.powsybl.iidm.network.TwoSides

Disconnect {
    staticId "_BUS____1-BUS____5-1_AC"
    startTime 1
    disconnectOnly TwoSides.TWO
}

Disconnect {
    staticId "_BUS____2-BUS____3-1_AC"
    startTime 1
    disconnectOnly TwoSides.TWO
}