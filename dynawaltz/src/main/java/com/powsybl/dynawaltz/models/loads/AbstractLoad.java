/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.loads;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.events.DisconnectableEquipment;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.iidm.network.Load;

import java.util.List;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractLoad extends AbstractEquipmentBlackBoxModel<Load> implements DisconnectableEquipment {

    protected final String terminalVarName;

    protected AbstractLoad(String dynamicModelId, Load load, String parameterSetId, String terminalVarName) {
        super(dynamicModelId, parameterSetId, Objects.requireNonNull(load));
        this.terminalVarName = terminalVarName;
    }

    protected String getTerminalVarName() {
        return terminalVarName;
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(BusUtils.getConnectableBusStaticId(equipment), BusModel.class, this::getVarConnectionsWithBus, context);
    }

    abstract List<VarConnection> getVarConnectionsWithBus(BusModel connected);

    @Override
    public String getDisconnectableVarName() {
        return "load_switchOffSignal2";
    }
}
