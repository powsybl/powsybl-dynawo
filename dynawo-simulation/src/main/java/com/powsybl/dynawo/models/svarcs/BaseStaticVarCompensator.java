/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.svarcs;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.InjectionModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.iidm.network.StaticVarCompensator;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseStaticVarCompensator extends AbstractEquipmentBlackBoxModel<StaticVarCompensator> implements InjectionModel {

    private final SvarcVarMappingHandler mappingHandler;

    protected BaseStaticVarCompensator(StaticVarCompensator svarc, String parameterSetId, ModelConfig modelConfig, SvarcVarMappingHandler mappingHandler) {
        super(svarc, parameterSetId, modelConfig);
        this.mappingHandler = mappingHandler;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    protected List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        return List.of(new VarConnection("SVarC_terminal", connected.getTerminalVarName()));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return mappingHandler.getVarsMapping();
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "SVarC_switchOffSignal2";
    }
}
