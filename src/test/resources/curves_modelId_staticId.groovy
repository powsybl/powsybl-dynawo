/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

import com.powsybl.iidm.network.Bus

for (Bus bus : network.busBreakerView.buses) {
    curve {
        modelId "NETWORK"
        staticId bus.id
        variable "Upu_value"
    }
}
