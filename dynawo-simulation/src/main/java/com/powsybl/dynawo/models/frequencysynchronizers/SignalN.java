/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.frequencysynchronizers;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.buses.BusOfSignalNModel;
import com.powsybl.dynawo.models.buses.DefaultBusOfSignalN;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class SignalN extends AbstractPureDynamicBlackBoxModel implements FrequencySynchronizerModel {

    private static final String SIGNAL_N_ID = "Signal_N";
    private static final ModelConfig MODEL_CONFIG = new ModelConfig("SignalN");
    private final List<SignalNModel> signalNEquipments;
    private final String defaultParFile;

    public SignalN(List<SignalNModel> signalNEquipments, String defaultParFile) {
        super(SIGNAL_N_ID, "", MODEL_CONFIG);
        this.signalNEquipments = signalNEquipments;
        this.defaultParFile = defaultParFile;
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        for (SignalNModel eq : signalNEquipments) {
            adder.createMacroConnections(this, eq, getVarConnectionsWith(eq));
        }
        SignalNModel firstModel = signalNEquipments.get(0);
        BusOfSignalNModel busOf = new DefaultBusOfSignalN(firstModel.getConnectableBus().getId(), firstModel.getStaticId());
        adder.createMacroConnections(this, busOf, getVarConnectionsWithBus(busOf));
    }

    private List<VarConnection> getVarConnectionsWith(SignalNModel connected) {
        return List.of(new VarConnection("signalN_N", connected.getNVarName()));
    }

    private List<VarConnection> getVarConnectionsWithBus(BusOfSignalNModel connected) {
        return List.of(new VarConnection("signalN_thetaRef", connected.getPhiVarName()));
    }

    @Override
    public boolean isEmpty() {
        return signalNEquipments.isEmpty();
    }

    @Override
    protected void writeDynamicAttributes(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
    }

    @Override
    public String getDefaultParFile() {
        return defaultParFile;
    }
}
