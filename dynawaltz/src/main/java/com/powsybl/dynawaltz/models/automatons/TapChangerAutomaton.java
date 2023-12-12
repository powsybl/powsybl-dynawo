/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynawaltzReports;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.loads.LoadWithTransformers;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.transformers.TapChangerModel;
import com.powsybl.iidm.network.Load;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerAutomaton extends AbstractPureDynamicBlackBoxModel implements TapChangerModel {

    private final Load load;
    private final TransformerSide side;

    private ConnectionState connection = ConnectionState.NOT_SET;

    private enum ConnectionState {
        CONNECTED,
        NOT_CONNECTED,
        NOT_SET
    }

    public TapChangerAutomaton(String dynamicModelId, String parameterSetId, Load load, TransformerSide side) {
        super(dynamicModelId, parameterSetId);
        this.load = Objects.requireNonNull(load);
        this.side = Objects.requireNonNull(side);
    }

    public TapChangerAutomaton(String dynamicModelId, String parameterSetId, Load load) {
        this(dynamicModelId, parameterSetId, load, TransformerSide.NONE);
    }

    @Override
    public String getName() {
        return getLib() + side.getSideSuffix();
    }

    @Override
    public String getLib() {
        return "TapChangerAutomaton";
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        if (ConnectionState.NOT_SET == connection) {
            boolean isSkipped = adder.createMacroConnectionsOrSkip(this, load, LoadWithTransformers.class, this::getVarConnectionsWith);
            if (isSkipped) {
                connection = ConnectionState.NOT_CONNECTED;
                DynawaltzReports.reportEmptyAutomaton(adder.getReporter(), this.getName(), getDynamicModelId(), LoadWithTransformers.class.getSimpleName());
            } else {
                connection = ConnectionState.CONNECTED;
            }
        }
    }

    private List<VarConnection> getVarConnectionsWith(LoadWithTransformers connected) {
        return connected.getTapChangerVarConnections(side);
    }

    @Override
    public List<VarConnection> getTapChangerBlockerVarConnections() {
        return List.of(new VarConnection(getTapChangerBlockingVarName(side), "tapChanger_locked"));
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        if (ConnectionState.CONNECTED == connection) {
            super.write(writer, context);
        }
    }

    public boolean isConnected(MacroConnectionsAdder adder) {
        if (ConnectionState.NOT_SET == connection) {
            createMacroConnections(adder);
        }
        return ConnectionState.CONNECTED == connection;
    }
}
