/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.hvdc;

import com.powsybl.dynawo.builders.ModelConfig;
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

    private static final List<VarMapping> INVERTED_VAR_MAPPING = Arrays.asList(
            new VarMapping("hvdc_PInj1Pu", "p2"),
            new VarMapping("hvdc_QInj1Pu", "q2"),
            new VarMapping("hvdc_state", "state2"),
            new VarMapping("hvdc_PInj2Pu", "p1"),
            new VarMapping("hvdc_QInj2Pu", "q1"),
            new VarMapping("hvdc_state", "state1"));

    protected static final String TERMINAL_PREFIX = "hvdc_terminal";

    protected final boolean isInverted;
    private final HvdcVarNameHandler varNameHandler;

    protected BaseHvdc(HvdcLine hvdc, String parameterSetId, ModelConfig modelConfig, HvdcVarNameHandler varNameHandler) {
        super(hvdc, parameterSetId, modelConfig);
        this.varNameHandler = varNameHandler;
        this.isInverted = HvdcLine.ConvertersMode.SIDE_1_INVERTER_SIDE_2_RECTIFIER == equipment.getConvertersMode();
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createTerminalMacroConnections(this, equipment, this::getVarConnectionsWith, TwoSides.ONE, isInverted);
        adder.createTerminalMacroConnections(this, equipment, this::getVarConnectionsWith, TwoSides.TWO, isInverted);
    }

    @Override
    public List<VarMapping> getVarsMapping() {
        return isInverted ? INVERTED_VAR_MAPPING : VAR_MAPPING;
    }

    @Override
    public String getName() {
        return isInverted ? getLib() + "Inverted" : getLib();
    }

    public TwoSides getConnectionSide(TwoSides side) {
        return isInverted ? SideUtils.getOppositeSide(side) : side;
    }

    protected List<VarConnection> getVarConnectionsWith(EquipmentConnectionPoint connected, TwoSides side) {
        TwoSides connectionSide = getConnectionSide(side);
        List<VarConnection> varConnections = new ArrayList<>(2);
        varConnections.add(getSimpleVarConnectionWithBus(connected, side, connectionSide));
        connected.getSwitchOffSignalVarName(connectionSide)
                .map(switchOff -> new VarConnection(varNameHandler.getConnectionPointVarName(side), switchOff))
                .ifPresent(varConnections::add);
        return varConnections;
    }

    protected final VarConnection getSimpleVarConnectionWithBus(EquipmentConnectionPoint connected, TwoSides side, TwoSides connectionSide) {
        return new VarConnection(TERMINAL_PREFIX + side.getNum(), connected.getTerminalVarName(connectionSide));
    }

    public List<HvdcConverterStation<?>> getConnectedStations() {
        return List.of(equipment.getConverterStation1(), equipment.getConverterStation2());
    }

    @Override
    public String getSwitchOffSignalEventVarName(TwoSides side) {
        return varNameHandler.getEventVarName(side);
    }
}
