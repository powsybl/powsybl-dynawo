/**
 *
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package error

/**
 * @author Riad Benradi
 * @literal <riad.benradi at rte -france.com>
 */

import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Generator

for (Bus bus : network.busBreakerView.buses) {
    fsv {
        id bus.id
        variable "wrong value"
    }
}

for (Generator gen : network.generators) {
    curve {
        id gen.id
        variables "wrong value", "wrong value 2", "wrong value 3"
    }
}



