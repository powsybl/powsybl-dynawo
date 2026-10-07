/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterBlockingIModel;
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterBlockingIModelAdder;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoPhaseShifterBlockingIModelAdderImpl
        extends AbstractDynawoAutomationSystemModelAdder<TwoWindingsTransformer, DynawoPhaseShifterBlockingIModel, DynawoPhaseShifterBlockingIModelAdderImpl>
        implements DynawoPhaseShifterBlockingIModelAdder {

    private String phaseShifterId;

    public DynawoPhaseShifterBlockingIModelAdderImpl(TwoWindingsTransformer extendable) {
        super(extendable);
    }

    @Override
    protected DynawoPhaseShifterBlockingIModel createExtension(TwoWindingsTransformer extendable) {
        return new DynawoPhaseShifterBlockingIModelImpl(extendable, modelName, parameterSetId, dynamicModelId, phaseShifterId);
    }

    @Override
    public DynawoPhaseShifterBlockingIModelAdder withPhaseShifterId(String phaseShifterId) {
        this.phaseShifterId = phaseShifterId;
        return self();
    }

    @Override
    protected DynawoPhaseShifterBlockingIModelAdderImpl self() {
        return this;
    }

}
