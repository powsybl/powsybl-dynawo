/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomaton;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerAutomatonBuilder extends AbstractAutomatonModelBuilder<TapChangerAutomatonBuilder> {

    protected final DslEquipment<Load> dslLoad;
    protected TransformerSide side = TransformerSide.NONE;

    public TapChangerAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
        dslLoad = new DslEquipment<>(IdentifiableType.LOAD);
    }

    public TapChangerAutomatonBuilder staticId(String staticId) {
        dslLoad.addEquipment(staticId, network::getLoad);
        return self();
    }

    public TapChangerAutomatonBuilder side(TransformerSide side) {
        this.side = side;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= dslLoad.checkEquipmentData(reporter);
    }

    @Override
    public DynamicModel build() {
        return isInstantiable() ? new TapChangerAutomaton(dynamicModelId, parameterSetId, dslLoad.getEquipment(), side, getLib()) : null;
    }

    @Override
    protected TapChangerAutomatonBuilder self() {
        return this;
    }
}
