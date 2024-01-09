/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons.phaseshifters;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.models.automatons.AbstractAutomatonModelBuilder;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
abstract class AbstractPhaseShifterModelBuilder<T extends AbstractAutomatonModelBuilder<T>> extends AbstractAutomatonModelBuilder<T> {

    protected final DslEquipment<TwoWindingsTransformer> dslTransformer;

    AbstractPhaseShifterModelBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
        dslTransformer = new DslEquipment<>(IdentifiableType.TWO_WINDINGS_TRANSFORMER, "transformer");
    }

    public T transformer(String staticId) {
        dslTransformer.addEquipment(staticId, network::getTwoWindingsTransformer);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= dslTransformer.checkEquipmentData(reporter);
    }
}
