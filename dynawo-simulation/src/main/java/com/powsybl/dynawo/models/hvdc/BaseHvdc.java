/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.hvdc;

import com.powsybl.dynawo.models.buses.EquipmentConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.AbstractEquipmentBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.VarMapping;
import com.powsybl.dynawo.models.utils.SideUtils;
import com.powsybl.iidm.network.HvdcConverterStation;
import com.powsybl.iidm.network.HvdcLine;
import com.powsybl.iidm.network.TwoSides;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class BaseHvdc extends AbstractEquipmentBlackBoxModel<HvdcLine> implements HvdcModel {

    private static final List<VarMapping> VAR_MAPPING = Arrays.asList(
            new VarMapping("hvdc_PInj1Pu", "p1"),
            new VarMapping("hvdc_QInj1Pu", "q1"),
            new VarMapping("hvdc_state", "state1"),
            new VarMapping("hvdc_PInj2Pu", "p2"),
            new VarMapping("hvdc_QInj2Pu", "q2"),
            new VarMapping("hvdc_state", "state2"));

    protected static final String TERMINAL_PREFIX = "hvdc_terminal";

    private final Function<TwoSides, String> eventVarNameSupplier;

    protected BaseHvdc(String dynamicModelId, HvdcLine hvdc, String parameterSetId, String lib, Function<TwoSides, String> eventVarNameSupplier) {
        super(dynamicModelId, parameterSetId, hvdc, lib);
        this.eventVarNameSupplier = eventVarNameSupplier;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment, this::getVarConnectionsWith, TwoSides.ONE);
        adder.createTerminalMacroConnections(this, equipment, this::getVarConnectionsWith, TwoSides.TWO);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return VAR_MAPPING;
    }

    protected List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected, TwoSides side) {
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(getSimpleVarConnectionWithBus(connected, side));
        connected.getSwitchOffSignalVarName(side)
                .map(switchOff -> new VarConnection("hvdc_switchOffSignal1" + SideUtils.getSideSuffix(side), switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    protected final VarConnection getSimpleVarConnectionWithBus(EquipmentConnectionPoint connected, TwoSides side) {
        return new VarConnection(TERMINAL_PREFIX + side.getNum(), connected.getTerminalVarName(side));
    }

    public List<HvdcConverterStation<?>> getConnectedStations() {
        return List.of(equipment.getConverterStation1(), equipment.getConverterStation2());
    }

    @Override
    public String getSwitchOffSignalEventVarName(TwoSides side) {
        return eventVarNameSupplier.apply(side);
    }
}
