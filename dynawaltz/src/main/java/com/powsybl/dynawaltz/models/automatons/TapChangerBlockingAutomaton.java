/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.MeasurementPoint;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.transformers.TapChangerModel;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.TwoWindingsTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class TapChangerBlockingAutomaton extends AbstractPureDynamicBlackBoxModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(TapChangerBlockingAutomaton.class);
    private static final Set<IdentifiableType> COMPATIBLE_EQUIPMENTS = EnumSet.of(IdentifiableType.LOAD, IdentifiableType.TWO_WINDINGS_TRANSFORMER);
    private static final int MAX_MEASUREMENTS = 5;

    private final List<TwoWindingsTransformer> transformers;
    private final List<Load> loadsWithTransformer;
    private final List<String> tapChangerAutomatonIds;
    private final List<Bus> uMeasurements;
    private boolean isConnected = true;

    public TapChangerBlockingAutomaton(String dynamicModelId, String parameterSetId, List<TwoWindingsTransformer> transformers, List<Load> loadsWithTransformer, List<String> tapChangerAutomatonIds, List<Bus> uMeasurements) {
        super(dynamicModelId, parameterSetId);
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

    public TapChangerBlockingAutomaton(String dynamicModelId, String parameterSetId, List<TwoWindingsTransformer> transformers, List<Load> loadsWithTransformer, List<Bus> uMeasurements) {
        this(dynamicModelId, parameterSetId, transformers, loadsWithTransformer, Collections.emptyList(), uMeasurements);
    }

    public TapChangerBlockingAutomaton(String dynamicModelId, String parameterSetId, List<TwoWindingsTransformer> transformers, List<Bus> uMeasurements) {
        this(dynamicModelId, parameterSetId, transformers, Collections.emptyList(), Collections.emptyList(), uMeasurements);
    }

    public static boolean isCompatibleEquipment(IdentifiableType type) {
        return COMPATIBLE_EQUIPMENTS.contains(type);
    }

    @Override
    public String getName() {
        return "TapChangerBlockingAutomaton";
    }

    @Override
    public String getLib() {
        return getName() + uMeasurements.size();
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        for (TwoWindingsTransformer transformer : transformers) {
            createMacroConnections(transformer, TapChangerModel.class, this::getVarConnectionsWith, context);
        }
        int skippedTapChangers = 0;
        for (Load load : loadsWithTransformer) {
            boolean isSkipped = createMacroConnectionsOrSkip(load, TapChangerModel.class, this::getVarConnectionsWith, context);
            if (isSkipped) {
                skippedTapChangers++;
            }
        }
        for (String id : tapChangerAutomatonIds) {
            if (createTcaMacroConnectionsOrSkip(id, context)) {
                skippedTapChangers++;
            }
        }
        if (!transformers.isEmpty() || skippedTapChangers < (loadsWithTransformer.size() + tapChangerAutomatonIds.size())) {
            int i = 1;
            for (Bus bus : uMeasurements) {
                createMacroConnections(bus, BusModel.class, this::getVarConnectionsWith, context, MeasurementPoint.of(i));
                i++;
            }
        } else {
            isConnected = false;
            LOGGER.warn("None of TapChangerBlockingAutomaton {} equipments are TapChangerModel, the automaton will be skipped", getDynamicModelId());
        }
    }

    private List<VarConnection> getVarConnectionsWith(TapChangerModel connected) {
        return connected.getTapChangerBlockerVarConnections();
    }

    private List<VarConnection> getVarConnectionsWith(BusModel connected, String suffix) {
        return connected.getUImpinVarName()
                .map(uImpinVarName -> List.of(new VarConnection("tapChangerBlocking_UMonitored" + suffix, uImpinVarName)))
                .orElse(Collections.emptyList());
    }

    private boolean createTcaMacroConnectionsOrSkip(String dynamicId, DynaWaltzContext context) {
        TapChangerAutomaton connectedModel = context.getPureDynamicModel(dynamicId, TapChangerAutomaton.class, false);
        if (connectedModel != null && connectedModel.isConnected(context)) {
            String macroConnectorId = context.addMacroConnector(getName(), connectedModel.getName(), getVarConnectionsWith(connectedModel));
            context.addMacroConnect(macroConnectorId, getMacroConnectFromAttributes(), connectedModel.getMacroConnectToAttributes());
            return false;
        }
        return true;
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        if (isConnected) {
            super.write(writer, context);
        }
    }
}
