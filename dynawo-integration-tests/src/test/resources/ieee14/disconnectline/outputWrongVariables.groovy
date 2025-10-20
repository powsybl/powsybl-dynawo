/**
 * Copyright (c) 2025,
 * RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 *
 */
package ieee14.disconnectline

/**
 * @author Riad Benradi
 * @literal <riad.benradi at rte -france.com>
 */

import com.powsybl.iidm.network.Bus
import com.powsybl.iidm.network.Generator

for (Bus bus : network.busBreakerView.buses) {
    fsv {
        staticId bus.id
        variable "wrong value"
    }
}






