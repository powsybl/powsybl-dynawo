/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomaton;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterIAutomatonBuilder extends AbstractPhaseShifterModelBuilder<PhaseShifterIAutomatonBuilder> {

    public static final String LIB = "PhaseShifterI";

    public PhaseShifterIAutomatonBuilder(Network network, Reporter reporter) {
        super(network, LIB, reporter);
    }

    @Override
    public PhaseShifterIAutomaton build() {
        return isInstantiable() ? new PhaseShifterIAutomaton(dynamicModelId, dslTransformer.getEquipment(), parameterSetId) : null;
    }

    @Override
    protected PhaseShifterIAutomatonBuilder self() {
        return this;
    }
}
