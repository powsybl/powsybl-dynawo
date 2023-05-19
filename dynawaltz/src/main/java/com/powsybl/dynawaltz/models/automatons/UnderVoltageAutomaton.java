/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.generators.GeneratorModel;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class UnderVoltageAutomaton extends AbstractPureDynamicBlackBoxModel {

    protected final Generator generator;

    public UnderVoltageAutomaton(String dynamicModelId, String parameterSetId, Generator generator) {
        super(dynamicModelId, parameterSetId);
        this.generator = Objects.requireNonNull(generator);
    }

    @Override
    public String getLib() {
        return "UnderVoltageAutomaton";
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(generator, GeneratorModel.class, this::getVarConnectionsWith, context);
    }

    protected List<VarConnection> getVarConnectionsWith(GeneratorModel connected) {
        return Arrays.asList(
                new VarConnection("underVoltageAutomaton_UMonitoredPu", connected.getUPuVarName()),
                new VarConnection("underVoltageAutomaton_switchOffSignal", connected.getSwitchOffSignalAutomatonVarName())
        );
    }
}
