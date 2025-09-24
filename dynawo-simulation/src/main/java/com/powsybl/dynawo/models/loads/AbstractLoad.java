/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.InjectionModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.iidm.network.Load;

import java.util.List;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractLoad extends AbstractEquipmentBlackBoxModel<Load> implements InjectionModel {

    protected final String terminalVarName;

    protected AbstractLoad(Load load, String parameterSetId, ModelConfig modelConfig, String terminalVarName) {
        super(load, parameterSetId, modelConfig);
        this.terminalVarName = terminalVarName;
    }

    protected String getTerminalVarName() {
        return terminalVarName;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    abstract List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected);

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "load_switchOffSignal2";
    }
}
