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
import com.powsybl.dynawaltz.models.automatons.UnderVoltageAutomaton;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class UnderVoltageAutomatonBuilder extends AbstractAutomatonModelBuilder<UnderVoltageAutomatonBuilder> {

    protected final DslEquipment<Generator> dslGenerator;

    //TODO forcer l'usage d'un reporter dans les contr des builder , gere le no op dans utils
    public UnderVoltageAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
        dslGenerator = new DslEquipment<>(IdentifiableType.GENERATOR, "generator");
    }

    public UnderVoltageAutomatonBuilder generator(String staticId) {
        dslGenerator.addEquipment(staticId, network::getGenerator);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= dslGenerator.checkEquipmentData(reporter);
    }

    @Override
    public DynamicModel build() {
        return isInstantiable() ? new UnderVoltageAutomaton(dynamicModelId, parameterSetId, dslGenerator.getEquipment(), getLib()) : null;
    }

    @Override
    protected UnderVoltageAutomatonBuilder self() {
        return this;
    }
}
