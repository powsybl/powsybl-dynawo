/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.generators.GeneratorModel;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Generator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class UnderVoltageAutomaton extends AbstractPureDynamicBlackBoxModel {

    protected final Generator generator;

    UnderVoltageAutomaton(String dynamicModelId, String parameterSetId, Generator generator, String lib) {
        super(dynamicModelId, parameterSetId, lib);
        this.generator = Objects.requireNonNull(generator);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, generator, GeneratorModel.class, this::getVarConnectionsWith);
    }

    protected List<VarConnection> getVarConnectionsWith(GeneratorModel connected) {
        return Arrays.asList(
                new VarConnection("underVoltageAutomaton_UMonitoredPu", connected.getUPuVarName()),
                new VarConnection("underVoltageAutomaton_switchOffSignal", connected.getSwitchOffSignalAutomatonVarName())
        );
    }
}
