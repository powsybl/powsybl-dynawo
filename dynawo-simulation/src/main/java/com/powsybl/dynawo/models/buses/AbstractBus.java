/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.iidm.network.Bus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractBus extends AbstractEquipmentBlackBoxModel<Bus> implements EquipmentConnectionPoint, ActionConnectionPoint {

    protected AbstractBus(Bus bus, String parameterSetId, ModelConfig modelConfig) {
        super(bus, parameterSetId, modelConfig);
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        List<MacroConnectAttribute> attributesConnectTo = new ArrayList<>(super.getMacroConnectToAttributes());
        attributesConnectTo.add(MacroConnectAttribute.of("name2", getDynamicModelId()));
        return attributesConnectTo;
    }

    public void createMacroConnections(MacroConnectionsAdder adder) {
        equipment.getConnectedTerminalStream().forEach(t -> {
            if (!adder.checkMacroConnections(t.getConnectable(), EquipmentBlackBoxModel.class)) {
                throw new PowsyblException(String.format("The equipment %s linked to the %s %s does not possess a dynamic model",
                        t.getConnectable().getId(), this.getClass().getSimpleName(), getDynamicModelId()));
            }
        });
    }

    @Override
    public String getName() {
        return getLib();
    }

    @Override
    public Optional<String> getSwitchOffSignalVarName() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getUImpinVarName() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getUpuImpinVarName() {
        return Optional.empty();
    }

    @Override
    public Optional<String> getStateValueVarName() {
        return Optional.empty();
    }
}
