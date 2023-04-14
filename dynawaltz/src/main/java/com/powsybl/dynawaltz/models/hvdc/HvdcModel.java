/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.iidm.network.HvdcLine;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class HvdcModel extends AbstractEquipmentBlackBoxModel<HvdcLine> {
    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("hvdc_PInj1Pu", "p1"),
            new VarMapping("hvdc_QInj1Pu", "q1"),
            new VarMapping("hvdc_state", "state1"),
            new VarMapping("hvdc_PInj2Pu", "p2"),
            new VarMapping("hvdc_QInj2Pu", "q2"),
            new VarMapping("hvdc_state", "state2"));

    private final String hvdcLib;

    public HvdcModel(String dynamicModelId, HvdcLine hvdc, String parameterSetId, String hvdcLib) {
        super(dynamicModelId, parameterSetId, hvdc);
        this.hvdcLib = Objects.requireNonNull(hvdcLib);
    }

    public String getLib() {
        return hvdcLib;
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(getVarConnectionsWithConverters(), context);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    public List<VarConnection> getVarConnectionsWithConverters() {
        return Arrays.asList(
                new VarConnection("hvdc_terminal1", "@STATIC_ID@@NODE1@_ACPIN"),
                new VarConnection("hvdc_switchOffSignal1Side1", "@STATIC_ID@@NODE1@_switchOff"),
                new VarConnection("hvdc_terminal2", "@STATIC_ID@@NODE2@_ACPIN"),
                new VarConnection("hvdc_switchOffSignal1Side2", "@STATIC_ID@@NODE2@_switchOff")
        );
    }
}
