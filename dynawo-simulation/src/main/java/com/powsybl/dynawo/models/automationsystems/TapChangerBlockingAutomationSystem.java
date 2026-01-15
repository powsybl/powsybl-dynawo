/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.DynawoSimulationReports;
import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.MeasurementPointSuffix;
import com.powsybl.dynawo.models.buses.ActionConnectionPoint;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.transformers.TapChangerModel;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerBlockingAutomationSystem extends AbstractPureDynamicBlackBoxModel {

    private static final Set<IdentifiableType> COMPATIBLE_EQUIPMENTS = EnumSet.of(IdentifiableType.LOAD, IdentifiableType.TWO_WINDINGS_TRANSFORMER);
    private static final int MAX_MEASUREMENTS = 5;

    private final List<Identifiable<?>> tapChangerEquipments;
    private final List<String> tapChangerAutomationSystemIds;
    private final List<Identifiable<?>> uMeasurements;
    private boolean isConnected = true;

    protected TapChangerBlockingAutomationSystem(String dynamicModelId, String parameterSetId, List<Identifiable<?>> tapChangerEquipments, List<String> tapChangerAutomationSystemIds, List<Identifiable<?>> uMeasurements, ModelConfig modelConfig) {
        super(dynamicModelId, parameterSetId, modelConfig);
        this.tapChangerEquipments = Objects.requireNonNull(tapChangerEquipments);
        this.tapChangerAutomationSystemIds = Objects.requireNonNull(tapChangerAutomationSystemIds);
        this.uMeasurements = Objects.requireNonNull(uMeasurements);
        if (tapChangerEquipments.isEmpty() && tapChangerAutomationSystemIds.isEmpty()) {
            throw new PowsyblException("No Tap changers to monitor");
        }
        if (uMeasurements.isEmpty()) {
            throw new PowsyblException("No measurement points");
        }
        if (uMeasurements.size() > MAX_MEASUREMENTS) {
            throw new PowsyblException("Tap changer blocking automation system can only handle " + MAX_MEASUREMENTS + " measurement points at the same time");
        }
    }

    public static boolean isCompatibleEquipment(IdentifiableType type) {
        return COMPATIBLE_EQUIPMENTS.contains(type);
    }

    @Override
    public String getName() {
        return super.getLib();
    }

    @Override
    public String getMacroConnectName() {
        return super.getLib();
    }

    @Override
    public String getLib() {
        return super.getLib() + uMeasurements.size();
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        int skippedTapChangers = 0;
        for (Identifiable<?> tc : tapChangerEquipments) {
            if (adder.createMacroConnectionsOrSkip(this, tc, TapChangerModel.class, this::getVarConnectionsWith)) {
                skippedTapChangers++;
            }
        }
        for (String id : tapChangerAutomationSystemIds) {
            if (adder.createMacroConnectionsOrSkip(this, id, TapChangerAutomationSystem.class, this::getVarConnectionsWith)) {
                skippedTapChangers++;
            }
        }
        if (skippedTapChangers < (tapChangerEquipments.size() + tapChangerAutomationSystemIds.size())) {
            if (uMeasurements.size() == 1) {
                adder.createMacroConnections(this, uMeasurements.getFirst(), ActionConnectionPoint.class, this::getVarConnectionsWith, MeasurementPointSuffix.of());
            } else {
                int i = 1;
                for (Identifiable<?> measurement : uMeasurements) {
                    adder.createMacroConnections(this, measurement, ActionConnectionPoint.class, this::getVarConnectionsWith, MeasurementPointSuffix.of(i));
                    i++;
                }
            }
        } else {
            isConnected = false;
            DynawoSimulationReports.reportEmptyListAutomationSystem(adder.getReportNode(), this.getName(), getDynamicModelId(), TapChangerModel.class.getSimpleName());
        }
    }

    private List<VarConnection> getVarConnectionsWith(TapChangerModel connected) {
        return connected.getTapChangerBlockerVarConnections();
    }

    private List<VarConnection> getVarConnectionsWith(ActionConnectionPoint connected, String suffix) {
        return connected.getUImpinVarName()
                .map(uImpinVarName -> List.of(new VarConnection("tapChangerBlocking_UMonitored" + suffix, uImpinVarName)))
                .orElse(Collections.emptyList());
    }

    @Override
    public void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException {
        if (isConnected) {
            super.write(writer, parFileName);
        }
    }
}
