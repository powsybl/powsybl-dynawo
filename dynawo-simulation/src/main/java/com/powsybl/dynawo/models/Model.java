/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;

import java.util.List;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public interface Model extends DynamicModel {

    String getName();

    List<MacroConnectAttribute> getMacroConnectToAttributes();
}
