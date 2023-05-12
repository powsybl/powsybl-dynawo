/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.hvdc;

import com.powsybl.dynawaltz.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.VarMapping;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.iidm.network.HvdcLine;

import java.util.ArrayList;
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
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, BusUtils.getConnectableBusStaticId(equipment.getConverterStation1().getTerminal()), BusModel.class, this::getVarConnectionsWithBus, Side.ONE);
        adder.createMacroConnections(this, BusUtils.getConnectableBusStaticId(equipment.getConverterStation2().getTerminal()), BusModel.class, this::getVarConnectionsWithBus, Side.TWO);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected, Side side) {
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(new VarConnection("hvdc_terminal" + side.getSideNumber(), connected.getTerminalVarName()));
        connected.getSwitchOffSignalVarName()
                .map(switchOff -> new VarConnection("hvdc_switchOffSignal1" + side.getSideSuffix(), switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }
}
