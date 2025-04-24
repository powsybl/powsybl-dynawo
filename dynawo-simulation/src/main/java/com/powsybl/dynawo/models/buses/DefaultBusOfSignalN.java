/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.buses;

import java.util.List;
import com.powsybl.dynawo.models.defaultmodels.AbstractDefaultModel;
import com.powsybl.dynawo.models.frequencysynchronizers.SignalNModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawo.xml.DynawoSimulationXmlConstants;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DefaultBusOfSignalN extends AbstractDefaultModel implements BusOfSignalNModel {

    private final String signalNEquipmentStaticId;

    public static DefaultBusOfSignalN of(SignalNModel model) {
        return new DefaultBusOfSignalN(model.getConnectableBus().getId(), model.getEquipment().getId());
    }

    private DefaultBusOfSignalN(String staticId, String signalNEquipmentStaticId) {
        super(staticId);
        this.signalNEquipmentStaticId = signalNEquipmentStaticId;
    }

    @Override
    public String getName() {
        return "DefaultBusOfSignalN";
    }

    public String getPhiVarName() {
        return "@@NAME@@@NODE@_phi_value";
    }

    @Override
    public List<MacroConnectAttribute> getMacroConnectToAttributes() {
        return List.of(
                MacroConnectAttribute.of("id2", DynawoSimulationXmlConstants.NETWORK),
                MacroConnectAttribute.of("name2", signalNEquipmentStaticId)
        );
    }
}
