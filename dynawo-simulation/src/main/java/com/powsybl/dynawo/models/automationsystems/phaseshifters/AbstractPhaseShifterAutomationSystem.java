/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.models.automationsystems.phaseshifters;

import com.powsybl.dynawo.builders.ModelConfig;
import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.models.VarConnection;
import com.powsybl.dynawo.models.transformers.TransformerModel;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractPhaseShifterAutomationSystem extends AbstractPureDynamicBlackBoxModel {

    protected final TwoWindingsTransformer transformer;

    protected AbstractPhaseShifterAutomationSystem(String dynamicModelId, TwoWindingsTransformer transformer, String parameterSetId, ModelConfig modelConfig) {
        super(dynamicModelId, parameterSetId, modelConfig);
        this.transformer = Objects.requireNonNull(transformer);
    }

    @Override
    public void createMacroConnections(MacroConnectionsAdder adder) {
        adder.createMacroConnections(this, transformer, TransformerModel.class, this::getVarConnectionsWith);
    }

    protected abstract List<VarConnection> getVarConnectionsWith(TransformerModel connected);
}
