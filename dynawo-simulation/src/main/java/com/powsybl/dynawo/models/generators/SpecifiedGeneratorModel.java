/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.models.InjectionModel;

/**
 * Implemented by user specified generator model
 * Default generator model does not implement this interface
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public interface SpecifiedGeneratorModel extends InjectionModel {

    String getSwitchOffSignalAutomatonVarName();

    String getUPuVarName();
}
