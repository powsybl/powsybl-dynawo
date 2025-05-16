/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.generators;

import com.powsybl.dynawo.models.defaultmodels.AbstractInjectionDefaultModel;
import com.powsybl.dynawo.models.events.ControllableEquipmentModel;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DefaultGenerator extends AbstractInjectionDefaultModel implements GeneratorModel, ControllableEquipmentModel {

    public DefaultGenerator(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "DefaultGenerator";
    }

    @Override
    public String getTerminalVarName() {
        return "@NAME@_terminal";
    }

    @Override
    public String getSwitchOffSignalNodeVarName() {
        return "@NAME@_switchOffSignal1";
    }

    //TODO handle the 2 others switch cases

    @Override
    public String getSwitchOffSignalAutomatonVarName() {
        return "@NAME@_switchOffSignal3";
    }

    @Override
    public String getRunningVarName() {
        return "@NAME@_running";
    }

    @Override
    public String getQStatorPuVarName() {
        return "@NAME@_QStatorPu";
    }

    @Override
    public String getUPuVarName() {
        return "@NAME@_UPu";
    }

    @Override
    public String getDeltaPVarName() {
        return "@NAME@_Pc";
    }
}
