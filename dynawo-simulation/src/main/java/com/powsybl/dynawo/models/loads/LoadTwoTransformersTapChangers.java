/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.loads;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.transformers.TapChangerModel;
import com.powsybl.dynawo.models.versionablevariable.VersionableVariables;
import com.powsybl.iidm.network.Load;

import java.util.List;

import static com.powsybl.dynawo.models.TransformerSide.HIGH_VOLTAGE;
import static com.powsybl.dynawo.models.TransformerSide.LOW_VOLTAGE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadTwoTransformersTapChangers extends LoadTwoTransformers implements TapChangerModel {

    protected LoadTwoTransformersTapChangers(Load load, String parameterSetId, ModelConfig modelConfig) {
        super(load, parameterSetId, modelConfig);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWithResolver);
    }

    protected List<VarConnection> getVarConnectionsWithResolver(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = getVarConnectionsWith(connected);
        connected.getSwitchOffSignalVarName()
                .ifPresent(switchOff -> {
                    String switchOffVar = VersionableVariables.getCurrentValue("TC_SWITCH_OFF");
                    varConnections.add(new VarConnection(String.format(switchOffVar, HIGH_VOLTAGE.getSideSuffix()), switchOff));
                    varConnections.add(new VarConnection(String.format(switchOffVar, LOW_VOLTAGE.getSideSuffix()), switchOff));
                });
        return varConnections;
    }

    @Override
    public List<VarConnection> getTapChangerVarConnections(TransformerSide side) {
        throw new PowsyblException("LoadTwoTransformersTapChangers already have a tap changer");
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        String lockedVar = VersionableVariables.getCurrentValue("TC_LOCKED");
        return List.of(getTapChangerBlockerVarConnection(LOW_VOLTAGE, lockedVar),
                getTapChangerBlockerVarConnection(HIGH_VOLTAGE, lockedVar));
    }

    private VarConnection getTapChangerBlockerVarConnection(TransformerSide side, String lockedVar) {
        return new VarConnection(getTapChangerBlockingVarName(side), String.format(lockedVar, side.getSideSuffix()));
    }
}
