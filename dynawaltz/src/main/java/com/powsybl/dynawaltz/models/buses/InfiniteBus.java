/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.buses;

import com.powsybl.iidm.network.Bus;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class InfiniteBus extends AbstractBus {

    private final String lib;

    public InfiniteBus(String dynamicModelId, Bus bus, String parameterSetId, String lib) {
        super(dynamicModelId, bus, parameterSetId);
        this.lib = lib;
    }

    @Override
    public String getLib() {
        return lib;
    }

    @Override
    public String getTerminalVarName() {
        return "infiniteBus_terminal";
    }
}
