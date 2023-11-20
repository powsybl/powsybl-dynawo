/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.svarcs;

import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.extensions.StandbyAutomaton;

import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class StaticVarCompensator extends AbstractEquipmentBlackBoxModel<com.powsybl.iidm.network.StaticVarCompensator> implements StaticVarCompensatorModel {

    private static final VarMapping P_MAPPING = new VarMapping("SVarC_injector_PInjPu", "p");
    private static final VarMapping Q_MAPPING = new VarMapping("SVarC_injector_QInjPu", "q");
    private static final VarMapping STATE_MAPPING = new VarMapping("SVarC_injector_state", "state");
    private static final VarMapping MODE_MAPPING = new VarMapping("SVarC_modeHandling_mode_value", "regulatingMode");

    private static final List<VarMapping> VAR_MAPPING_NO_STANDBY_AUTOMATON = List.of(P_MAPPING, Q_MAPPING, STATE_MAPPING);
    private static final List<VarMapping> VAR_MAPPING_WITH_STANDBY_AUTOMATON = List.of(P_MAPPING, Q_MAPPING, STATE_MAPPING, MODE_MAPPING);

    public StaticVarCompensator(String dynamicModelId, com.powsybl.iidm.network.StaticVarCompensator svarc, String parameterSetId, String lib) {
        super(dynamicModelId, parameterSetId, svarc, lib);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        return List.of(new VarConnection("SVarC_terminal", connected.getTerminalVarName()));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        StandbyAutomaton standbyAutomaton = equipment.getExtension(StandbyAutomaton.class);
        return standbyAutomaton == null ? VAR_MAPPING_NO_STANDBY_AUTOMATON : VAR_MAPPING_WITH_STANDBY_AUTOMATON;
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "SVarC_switchOffSignal2";
    }
}
