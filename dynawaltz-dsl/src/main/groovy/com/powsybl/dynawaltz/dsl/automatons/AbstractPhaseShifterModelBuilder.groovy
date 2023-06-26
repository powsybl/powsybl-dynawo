/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl.automatons

import com.powsybl.dynawaltz.dsl.DslEquipment
import com.powsybl.dynawaltz.dsl.builders.AbstractPureDynamicModelBuilder
import com.powsybl.iidm.network.IdentifiableType
import com.powsybl.iidm.network.Network
import com.powsybl.iidm.network.TwoWindingsTransformer

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
abstract class AbstractPhaseShifterModelBuilder extends AbstractPureDynamicModelBuilder {

    protected final DslEquipment<TwoWindingsTransformer> dslTransformer

    AbstractPhaseShifterModelBuilder(Network network, String lib) {
        super(network, lib)
        dslTransformer = new DslEquipment<>(IdentifiableType.TWO_WINDINGS_TRANSFORMER, "transformer")
    }

    void transformer(String staticId) {
        dslTransformer.addEquipment(staticId, network::getTwoWindingsTransformer)
    }

    @Override
    protected void checkData() {
        super.checkData()
        isInstantiable &= dslTransformer.checkEquipmentData(LOGGER)
    }
}
