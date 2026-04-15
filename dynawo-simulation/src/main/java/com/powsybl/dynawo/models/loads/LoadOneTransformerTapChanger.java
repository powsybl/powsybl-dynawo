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
import com.powsybl.dynawo.models.utils.ImmutableLateInit;
import com.powsybl.dynawo.models.versionableVariable.VariableResolver;
import com.powsybl.dynawo.models.versionableVariable.VariableResolverModel;
import com.powsybl.iidm.network.Load;

import java.util.List;

import static com.powsybl.dynawo.models.TransformerSide.NONE;
import static com.powsybl.dynawo.models.versionableVariable.VersionVariableUtils.TC_LOCKED;
import static com.powsybl.dynawo.models.versionableVariable.VersionVariableUtils.TC_SWITCH_OFF;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class LoadOneTransformerTapChanger extends LoadOneTransformer implements TapChangerModel, VariableResolverModel {

    protected final ImmutableLateInit<VariableResolver> variableResolver = new ImmutableLateInit<>();

    protected LoadOneTransformerTapChanger(Load load, String parameterSetId, ModelConfig modelConfig) {
        super(load, parameterSetId, modelConfig);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment.getTerminal(), this::getVarConnectionsWithResolver);
    }

    protected List<VarConnection> getVarConnectionsWithResolver(EquipmentConnectionPoint connected) {
        List<VarConnection> varConnections = super.getVarConnectionsWith(connected);
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection(String.format(variableResolver.getValue().resolve(TC_SWITCH_OFF), NONE.getSideSuffix()), switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    @Override
    public List<VarConnection> getTapChangerVarConnections(TransformerSide side) {
        throw new PowsyblException("LoadOneTransformerTapChanger already have a tap changer");
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return List.of(new VarConnection(getTapChangerBlockingVarName(NONE), String.format(variableResolver.getValue().resolve(TC_LOCKED), NONE.getSideSuffix())));
    }

    @Override
    public void setVariableResolver(VariableResolver variableResolver) {
        this.variableResolver.setValue(variableResolver);
    }
}
