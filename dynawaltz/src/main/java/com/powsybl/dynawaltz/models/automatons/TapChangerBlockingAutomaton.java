/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynawaltzReports;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.MeasurementPointSuffix;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.ActionConnectionPoint;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.models.transformers.TapChangerModel;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class TapChangerBlockingAutomaton extends AbstractPureDynamicBlackBoxModel {

    private static final Set<IdentifiableType> COMPATIBLE_EQUIPMENTS = EnumSet.of(IdentifiableType.LOAD, IdentifiableType.TWO_WINDINGS_TRANSFORMER);
    private static final int MAX_MEASUREMENTS = 5;

    private final List<TwoWindingsTransformer> transformers;
    private final List<Load> loadsWithTransformer;
    private final List<String> tapChangerAutomatonIds;
    private final List<Identifiable<?>> uMeasurements;
    private boolean isConnected = true;

    protected TapChangerBlockingAutomaton(String dynamicModelId, String parameterSetId, List<TwoWindingsTransformer> transformers, List<Load> loadsWithTransformer, List<String> tapChangerAutomatonIds, List<Identifiable<?>> uMeasurements, String lib) {
        super(dynamicModelId, parameterSetId, lib);
        this.transformers = Objects.requireNonNull(transformers);
        this.loadsWithTransformer = Objects.requireNonNull(loadsWithTransformer);
        this.tapChangerAutomatonIds = Objects.requireNonNull(tapChangerAutomatonIds);
        this.uMeasurements = Objects.requireNonNull(uMeasurements);
        if (transformers.isEmpty() && loadsWithTransformer.isEmpty() && tapChangerAutomatonIds.isEmpty()) {
            throw new PowsyblException("No Tap changers to monitor");
        }
        if (uMeasurements.isEmpty()) {
            throw new PowsyblException("No measurement points");
        }
        if (uMeasurements.size() > MAX_MEASUREMENTS) {
            throw new PowsyblException("Tap changer blocking automaton can only handle " + MAX_MEASUREMENTS + " measurement points at the same time");
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
    public String getLib() {
        return super.getLib() + uMeasurements.size();
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        for (TwoWindingsTransformer transformer : transformers) {
            adder.createMacroConnections(this, transformer, TapChangerModel.class, this::getVarConnectionsWith);
        }
        int skippedTapChangers = 0;
        for (Load load : loadsWithTransformer) {
            boolean isSkipped = adder.createMacroConnectionsOrSkip(this, load, TapChangerModel.class, this::getVarConnectionsWith);
            if (isSkipped) {
                skippedTapChangers++;
            }
        }
        for (String id : tapChangerAutomatonIds) {
            if (adder.createTcaMacroConnectionsOrSkip(this, id, this::getVarConnectionsWith)) {
                skippedTapChangers++;
            }
        }
        if (!transformers.isEmpty() || skippedTapChangers < (loadsWithTransformer.size() + tapChangerAutomatonIds.size())) {
            int i = 1;
            for (Identifiable<?> measurement : uMeasurements) {
                adder.createMacroConnections(this, measurement, ActionConnectionPoint.class, this::getVarConnectionsWith, MeasurementPointSuffix.of(i));
                i++;
            }
        } else {
            isConnected = false;
            DynawaltzReports.reportEmptyListAutomaton(adder.getReporter(), this.getName(), getDynamicModelId(), TapChangerModel.class.getSimpleName());
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
