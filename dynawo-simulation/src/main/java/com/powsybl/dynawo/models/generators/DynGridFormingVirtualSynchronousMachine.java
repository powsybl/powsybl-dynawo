/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynGridFormingVirtualSynchronousMachine extends AbstractEquipmentBlackBoxModel<Generator> {

    protected static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("GFM_Measurements_PFilterPu", "p"),
            new VarMapping("GFM_Measurements_QFilterPu", "q"),
            new VarMapping("GFM_state", "state"));

    protected DynGridFormingVirtualSynchronousMachine(Generator generator, String parameterSetId, ModelConfig modelConfig) {
        super(generator, parameterSetId, modelConfig);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWith);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(new VarConnection(getTerminalVarName(), connected.getTerminalVarName()));
//        connected.getSwitchOffSignalVarName()
//                .map(switchOff -> new VarConnection(getSwitchOffSignalNodeVarName(), switchOff))
//                .ifPresent(varConnections::add);
        return varConnections;
    }

    public String getTerminalVarName() {
        return "GFM_terminal";
    }

//    public String getSwitchOffSignalNodeVarName() {
//        return "generator_switchOffSignal1";
//    }

}
