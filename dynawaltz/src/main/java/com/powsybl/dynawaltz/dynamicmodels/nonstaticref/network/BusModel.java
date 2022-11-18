/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.nonstaticref.network;

import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public interface BusModel extends BlackBoxModel {
    String getId();

    String getTerminalVarName();

    String getSwitchOffSignalVarName();

    String getNumCCVarName();
}
