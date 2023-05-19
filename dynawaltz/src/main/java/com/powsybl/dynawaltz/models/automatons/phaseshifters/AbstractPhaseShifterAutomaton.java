/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.models.automatons.phaseshifters;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawaltz.models.VarConnection;
import com.powsybl.dynawaltz.models.transformers.TransformerModel;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public abstract class AbstractPhaseShifterAutomaton extends AbstractPureDynamicBlackBoxModel {

    protected final TwoWindingsTransformer transformer;

    protected AbstractPhaseShifterAutomaton(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId) {
        super(dynamicModelId, parameterSetId);
        this.transformer = Objects.requireNonNull(transformer);
    }

    @Override
    public void createMacroConnections(DynaWaltzContext context) {
        createMacroConnections(transformer, TransformerModel.class, this::getVarConnectionsWith, context);
    }

    protected abstract List<VarConnection> getVarConnectionsWith(TransformerModel connected);
}
