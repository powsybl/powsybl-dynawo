/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterPModel;
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterPModelAdder;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoPhaseShifterPModelAdderImpl extends AbstractDynawoAutomationSystemModelAdder<TwoWindingsTransformer, DynawoPhaseShifterPModel, DynawoPhaseShifterPModelAdderImpl>
        implements DynawoPhaseShifterPModelAdder {

    public DynawoPhaseShifterPModelAdderImpl(TwoWindingsTransformer transformer) {
        super(transformer);
    }

    @Override
    protected DynawoPhaseShifterPModel createExtension(TwoWindingsTransformer extendable) {
        return new DynawoPhaseShifterPModelImpl(extendable, modelName, parameterSetId, dynamicModelId);
    }

    @Override
    protected DynawoPhaseShifterPModelAdderImpl self() {
        return this;
    }
}
