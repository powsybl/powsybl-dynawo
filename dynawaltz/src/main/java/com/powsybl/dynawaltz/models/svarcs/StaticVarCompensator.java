/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.svarcs;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.EquipmentConnectionPoint;

import java.util.Arrays;
import java.util.List;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class StaticVarCompensator extends AbstractEquipmentBlackBoxModel<com.powsybl.iidm.network.StaticVarCompensator> implements StaticVarCompensatorModel {

    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("SVarC_injector_PInjPu", "p"),
            new VarMapping("SVarC_injector_QInjPu", "q"),
            new VarMapping("SVarC_injector_state", "state"),
            new VarMapping("SVarC_modeHandling_mode_value", "regulatingMode"));

    public StaticVarCompensator(String dynamicModelId, com.powsybl.iidm.network.StaticVarCompensator svarc, String parameterSetId, String lib) {
        super(dynamicModelId, parameterSetId, svarc, lib);
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createTerminalMacroConnections(equipment, this::getVarConnectionsWith, context);
    }

    private List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected) {
        return List.of(new VarConnection("SVarC_terminal", connected.getTerminalVarName()));
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    @Override
    public String getSwitchOffSignalEventVarName() {
        return "SVarC_switchOffSignal2";
    }
}
