/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomaton;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterPAutomatonBuilder extends AbstractPhaseShifterModelBuilder<PhaseShifterPAutomatonBuilder> {

    public PhaseShifterPAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public PhaseShifterPAutomaton build() {
        return isInstantiable() ? new PhaseShifterPAutomaton(dynamicModelId, dslTransformer.getEquipment(), parameterSetId, getLib()) : null;
    }

    @Override
    protected PhaseShifterPAutomatonBuilder self() {
        return this;
    }
}
