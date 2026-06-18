/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems;

import com.powsybl.dynawo.DynawoSimulationReports;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.TransformerSide;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.loads.LoadWithTransformerModel;
import com.powsybl.dynawo.models.loads.LoadWithTransformersModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.transformers.TapChangerModel;
import com.powsybl.iidm.network.Load;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.models.TransformerSide.HIGH_VOLTAGE;
import static com.powsybl.dynawo.models.TransformerSide.NONE;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerAutomationSystem extends AbstractPureDynamicBlackBoxModel implements TapChangerModel, ConnectionStatefulModel {

    private final Load load;
    private final TransformerSide side;
    private ConnectionState connection = null;

    protected TapChangerAutomationSystem(String dynamicModelId, String parameterSetId, Load load, TransformerSide side, ModelConfig modelConfig) {
        super(dynamicModelId, parameterSetId, modelConfig);
        this.load = Objects.requireNonNull(load);
        this.side = Objects.requireNonNull(side);
    }

    @Override
    public String getMacroConnectName() {
        return getLib() + side.getSideSuffix();
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        if (connection == null) {
            boolean isSkipped = switch (side) {
                case HIGH_VOLTAGE -> adder.createMacroConnectionsOrSkip(this, load, LoadWithTransformersModel.class, this::getHighVoltageVarConnectionsWith);
                case LOW_VOLTAGE -> adder.createMacroConnectionsOrSkip(this, load, LoadWithTransformersModel.class, this::getLowVoltageVarConnectionsWith);
                case NONE -> adder.createMacroConnectionsOrSkip(this, load, LoadWithTransformerModel.class, this::getVarConnectionsWith);
            };

            if (isSkipped) {
                connection = ConnectionState.CANNOT_CONNECT;
                DynawoSimulationReports.reportEmptyModel(adder.getReportNode(), getName(), getDynamicModelId());
            } else {
                connection = ConnectionState.CONNECTED;
            }
        }
    }

    private List<VarConnection> getVarConnectionsWith(LoadWithTransformerModel connected) {
        return connected.getTapChangerVarConnections();
    }

    private List<VarConnection> getHighVoltageVarConnectionsWith(LoadWithTransformersModel connected) {
        return connected.getHighVoltageTapChangerVarConnections();
    }

    private List<VarConnection> getLowVoltageVarConnectionsWith(LoadWithTransformersModel connected) {
        return connected.getLowVoltageTapChangerVarConnections();
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return List.of(new VarConnection(getTapChangerBlockingVarName(NONE == side ? HIGH_VOLTAGE : side), "tapChanger_locked"));
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        if (ConnectionState.CONNECTED == connection) {
            super.write(writer, parFileName);
        }
    }

    @Override
    public ConnectionState getConnectionState() {
        return connection;
    }

    @Override
    public boolean connect(MacroConnectionsAdder adder) {
        createMacroConnections(adder);
        return ConnectionState.CONNECTED == getConnectionState();
    }

    @Override
    public boolean isConnected() {
        return ConnectionState.CONNECTED == getConnectionState();
    }
}
