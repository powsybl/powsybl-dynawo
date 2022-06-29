/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public class DefaultBusModel extends AbstractNetworkBlackBoxModel implements BusModel {
    private final String id;

    public DefaultBusModel(String staticId) {
        super(staticId);
        id = "defaultNodeModel";
    }

    @Override
    public String getLib() {
        return "NetworkBus";
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getTerminalVarName() {
        return "@STATIC_ID@@NODE@_ACPIN";
    }

    @Override
    public String getSwitchOffSignalVarName() {
        return "@STATIC_ID@@NODE@_switchOff";
    }

    @Override
    public String getNumCCVarName() {
        return "@@NAME@@@NODE@_numcc";
    }
}
