/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons.phaseshifters;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.ModelConfig;
import com.powsybl.dynawaltz.builders.ModelConfigs;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Network;

import java.util.Map;
import java.util.Set;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterPAutomatonBuilder extends AbstractPhaseShifterModelBuilder<PhaseShifterPAutomatonBuilder> {

    public static final String CATEGORY = "PHASE_SHIFTER_P";
    private static final Map<String, ModelConfig> LIBS = ModelConfigs.getInstance().getModelConfigs(CATEGORY);

    public static PhaseShifterPAutomatonBuilder of(Network network) {
        return of(network, Reporter.NO_OP);
    }

    public static PhaseShifterPAutomatonBuilder of(Network network, Reporter reporter) {
        return new PhaseShifterPAutomatonBuilder(network, LIBS.values().iterator().next(), reporter);
    }

    public static PhaseShifterPAutomatonBuilder of(Network network, String lib) {
        return of(network, lib, Reporter.NO_OP);
    }

    public static PhaseShifterPAutomatonBuilder of(Network network, String lib, Reporter reporter) {
        ModelConfig modelConfig = LIBS.get(lib);
        if (modelConfig == null) {
            Reporters.reportLibNotFound(reporter, PhaseShifterPAutomatonBuilder.class.getSimpleName(), lib);
            return null;
        }
        return new PhaseShifterPAutomatonBuilder(network, LIBS.get(lib), reporter);
    }

    public static Set<String> getSupportedLibs() {
        return LIBS.keySet();
    }

    protected PhaseShifterPAutomatonBuilder(Network network, ModelConfig modelConfig, Reporter reporter) {
        super(network, modelConfig, reporter);
    }

    @Override
    public PhaseShifterPAutomaton build() {
        return isInstantiable() ? new PhaseShifterPAutomaton(dynamicModelId, transformer.getEquipment(), parameterSetId, getLib()) : null;
    }

    @Override
    protected PhaseShifterPAutomatonBuilder self() {
        return this;
    }
}
