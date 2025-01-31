/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems.phaseshifters;

import com.powsybl.dynawo.DynawoSimulationReports;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.transformers.TransformerModel;
import com.powsybl.dynawo.models.utils.ImmutableLateInit;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class PhaseShifterBlockingIAutomationSystem extends AbstractPureDynamicBlackBoxModel {

    private final String phaseShifterIDynamicId;
    private final ImmutableLateInit<TwoWindingsTransformer> transformer = new ImmutableLateInit<>();
    private boolean isConnected = true;

    protected PhaseShifterBlockingIAutomationSystem(String dynamicModelId, String phaseShifterIDynamicId, String parameterSetId, ModelConfig modelConfig) {
        super(dynamicModelId, parameterSetId, modelConfig);
        this.phaseShifterIDynamicId = phaseShifterIDynamicId;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        isConnected = !adder.createMacroConnectionsOrSkip(this, phaseShifterIDynamicId, PhaseShifterIAutomationSystem.class, this::getVarConnectionsWith);
        if (isConnected) {
            adder.createMacroConnections(this, transformer.getValue(), TransformerModel.class, this::getVarConnectionsWith);
        } else {
            DynawoSimulationReports.reportEmptyAutomaton(adder.getReportNode(), getName(), getDynamicModelId(), phaseShifterIDynamicId, PhaseShifterIModel.class.getSimpleName());
        }
    }

    protected List<VarConnection> getVarConnectionsWith(PhaseShifterIModel connected) {
        transformer.setValue(connected.getConnectedTransformer());
        return List.of(new VarConnection("phaseShifterBlockingI_locked", connected.getLockedVarName()));
    }

    protected List<VarConnection> getVarConnectionsWith(TransformerModel connected) {
        return List.of(new VarConnection("phaseShifterBlockingI_IMonitored", connected.getIMonitoredVarName()));
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        if (isConnected) {
            super.write(writer, parFileName);
        }
    }
}
