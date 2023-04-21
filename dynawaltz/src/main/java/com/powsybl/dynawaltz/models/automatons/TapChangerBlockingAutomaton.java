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
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.transformers.TapChangerModel;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class TapChangerBlockingAutomaton extends AbstractPureDynamicBlackBoxModel {

    private static final Set<IdentifiableType> COMPATIBLE_EQUIPMENTS = EnumSet.of(IdentifiableType.LOAD, IdentifiableType.TWO_WINDINGS_TRANSFORMER);
    private static final int MAX_MEASUREMENTS = 5;

    private final List<TwoWindingsTransformer> transformers;
    private final List<Load> loadsWithTransformer;
    private final List<String> tapChangerAutomatonIds;
    private final List<Bus> uMeasurements;

    public TapChangerBlockingAutomaton(String dynamicModelId, String parameterSetId, List<TwoWindingsTransformer> transformers, List<Load> loadsWithTransformer, List<String> tapChangerAutomatonIds, List<Bus> uMeasurements) {
        super(dynamicModelId, parameterSetId);
        this.transformers = Objects.requireNonNull(transformers);
        this.loadsWithTransformer = Objects.requireNonNull(loadsWithTransformer);
        this.tapChangerAutomatonIds = Objects.requireNonNull(tapChangerAutomatonIds);
        this.uMeasurements = Objects.requireNonNull(uMeasurements);
        if (transformers.isEmpty() && loadsWithTransformer.isEmpty() && tapChangerAutomatonIds.isEmpty()) {
            throw new PowsyblException("No Tap changers to monitor");
        }
        if (uMeasurements.size() == 0) {
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
    public String getLib() {
        return "TapChangerBlockingAutomaton" + uMeasurements.size();
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        for (TwoWindingsTransformer transformer : transformers) {
            createMacroConnections(transformer, TapChangerModel.class, this::getVarConnectionsWithTapChanger, context);
        }
        for (Load load : loadsWithTransformer) {
            createMacroConnections(load, TapChangerModel.class, this::getVarConnectionsWithTapChanger, context);
        }
        for (String id : tapChangerAutomatonIds) {
            createPureDynamicMacroConnections(id, TapChangerModel.class, this::getVarConnectionsWithTapChanger, context);
        }
        for (Bus bus : uMeasurements) {
            createMacroConnections(bus.getId(), BusModel.class, this::getVarConnectionsWithBus, context);
        }
    }

    private List<VarConnection> getVarConnectionsWithTapChanger(TapChangerModel connected) {
        return connected.getTapChangerBlockerVarConnections();
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected) {
        return connected.getUImpinVarName()
                .map(uImpinVarName -> List.of(new VarConnection("tapChangerBlocking_UMonitored", uImpinVarName)))
                .orElse(Collections.emptyList());
    }
}
