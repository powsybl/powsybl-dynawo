/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */

package com.powsybl.dynawaltz.dsl.automatons

import com.powsybl.dsl.DslException
import com.powsybl.dynawaltz.dsl.models.builders.AbstractPureDynamicModelBuilder
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.TwoWindingsTransformer

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractPhaseShifterModelBuilder extends AbstractPureDynamicModelBuilder {

    Network network
    TwoWindingsTransformer transformer

    AbstractPhaseShifterModelBuilder(Network network) {
        this.network = network
    }

    void transformer(String staticId) {
        this.transformer = network.getTwoWindingsTransformer(staticId)
        /*if (transformer == null) {
            throw new DslException("Transformer static id unknown: " + staticId)
        }*/
    }

}
