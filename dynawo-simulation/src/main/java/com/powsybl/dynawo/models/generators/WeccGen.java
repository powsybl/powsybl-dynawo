/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.iidm.network.Generator;

import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class WeccGen extends AbstractEquipmentBlackBoxModel<Generator> {

    private final List<VarMapping> varsMapping;
    protected final String weccPrefix;

    protected WeccGen(Generator generator, String parameterSetId, ModelConfig modelConfig, String weccPrefix) {
        super(generator, parameterSetId, modelConfig);
        this.weccPrefix = Objects.requireNonNull(weccPrefix);
        varsMapping = List.of(
                new VarMapping(weccPrefix + "_measurements_PPuSnRef", "p"),
                new VarMapping(weccPrefix + "_measurements_QPuSnRef", "q"),
                new VarMapping(weccPrefix + "_injector_state", "state"));
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        return List.of(new VarConnection(weccPrefix + "_terminal", connected.getTerminalVarName()));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return varsMapping;
    }
}
