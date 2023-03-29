/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.transformers;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.Side;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.buses.BusModel;
import com.powsybl.dynawaltz.models.utils.BusUtils;
import com.powsybl.dynawaltz.models.utils.SideConverter;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class TransformerFixedRatio extends AbstractBlackBoxModel {

    private final TwoWindingsTransformer transformer;
    private final String transformerLib;

    public TransformerFixedRatio(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId, String lib) {
        super(dynamicModelId, transformer.getId(), parameterSetId);
        this.transformer = Objects.requireNonNull(transformer);
        this.transformerLib = lib;
    }

    @Override
    public String getLib() {
        return transformerLib;
    }

    private List<VarConnection> getVarConnectionsWithBus(BusModel connected, Side side) {
        return List.of(new VarConnection(getTerminalVarName(side), connected.getTerminalVarName()));
    }

    private String getTerminalVarName(Side side) {
        return "transformer_terminal" + side.getSideNumber();
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        transformer.getTerminals().forEach(t -> {
            String busStaticId = BusUtils.getConnectableBusStaticId(t);
            createMacroConnections(busStaticId, BusModel.class, this::getVarConnectionsWithBus, context, SideConverter.convert(transformer.getSide(t)));
        });
    }
}
