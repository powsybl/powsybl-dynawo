/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.models.Model;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public interface GeneratorModel extends Model {
    String getTerminalVarName();

    String getSwitchOffSignalNodeVarName();

    String getSwitchOffSignalEventVarName();

    String getSwitchOffSignalAutomatonVarName();

    String getRunningVarName();

    String getQStatorPuVarName();

    String getUpuVarName();
}
