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

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class TapChangerBlockingAutomaton extends AbstractPureDynamicBlackBoxModel {

    private static final Set<IdentifiableType> COMPATIBLE_EQUIPMENTS = EnumSet.of(IdentifiableType.LOAD, IdentifiableType.TWO_WINDINGS_TRANSFORMER);
    private static final int MAX_TRANSFORMER_NUMBER = 5;

    private final List<TwoWindingsTransformer> transformers;
    private final List<Load> loadsWithTransformer;
    private final Bus uMeasurement;

    public TapChangerBlockingAutomaton(String dynamicModelId, String parameterSetId, List<TwoWindingsTransformer> transformers, List<Load> loadsWithTransformer, Bus uMeasurement) {
        super(dynamicModelId, parameterSetId);
        this.transformers = transformers;
        this.loadsWithTransformer = loadsWithTransformer;
        this.uMeasurement = uMeasurement;
        if (transformers.isEmpty() && loadsWithTransformer.isEmpty()) {
            throw new PowsyblException("No Tap changers to monitor");
        }
        if (transformers.size() + loadsWithTransformer.size() > MAX_TRANSFORMER_NUMBER) {
            throw new PowsyblException("Tap changer blocking automaton can only handle " + MAX_TRANSFORMER_NUMBER + " equipments at the same time");
        }
    }

    public TapChangerBlockingAutomaton(String dynamicModelId, String parameterSetId, List<TwoWindingsTransformer> transformers, Bus uMeasurement) {
        this(dynamicModelId, parameterSetId, transformers, Collections.emptyList(), uMeasurement);
    }

    public static boolean isCompatibleEquipment(IdentifiableType type) {
        return COMPATIBLE_EQUIPMENTS.contains(type);
    }

    //TODO verify lib name
    @Override
    public String getLib() {
        return "TapChangerBlockingAutomaton1";
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        //TODO no index ?
        for (TwoWindingsTransformer transformer : transformers) {
            createMacroConnections(transformer, TapChangerModel.class, this::getVarConnectionsWithTapChanger, context);
        }
        for (Load load : loadsWithTransformer) {
            createMacroConnections(load, TapChangerModel.class, this::getVarConnectionsWithTapChanger, context);
        }
        createMacroConnections(uMeasurement.getId(), BusModel.class, this::getVarConnectionsWithBus, context);
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
