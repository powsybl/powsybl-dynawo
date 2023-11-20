/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.buses;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Terminal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractBus extends AbstractEquipmentBlackBoxModel<Bus> implements EquipmentConnectionPoint, ActionConnectionPoint {

    protected AbstractBus(String dynamicModelId, Bus bus, String parameterSetId, String lib) {
        super(dynamicModelId, parameterSetId, bus, lib);
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        List<MacroConnectAttribute> attributesConnectTo = new ArrayList<>(super.getMacroConnectToAttributes());
        attributesConnectTo.add(MacroConnectAttribute.of("name2", getStaticId()));
        return attributesConnectTo;
    }

    public void createMacroConnections(MacroConnectionsAdder adder) {
        checkLinkedDynamicModels(adder);
    }

    private void checkLinkedDynamicModels(MacroConnectionsAdder adder) {
        equipment.getConnectedTerminalStream()
                .map(Terminal::getConnectable)
                .filter(adder::isWithoutBlackBoxDynamicModel)
                .findAny()
                .ifPresent(id -> {
                    throw new PowsyblException(String.format("The equipment %s linked to the %s %s does not possess a dynamic model",
                            id, this.getClass().getSimpleName(), getStaticId()));
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
}
