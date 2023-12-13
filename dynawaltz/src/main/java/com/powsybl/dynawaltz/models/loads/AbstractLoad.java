/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Load;

import java.util.List;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractLoad extends AbstractEquipmentBlackBoxModel<Load> implements LoadModel {

    protected final String terminalVarName;

    protected AbstractLoad(String dynamicModelId, Load load, String parameterSetId, String lib, String terminalVarName) {
        super(dynamicModelId, parameterSetId, load, lib);
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
