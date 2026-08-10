/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterBlockingIModel;
import com.powsybl.iidm.network.TwoWindingsTransformer;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoPhaseShifterBlockingIModelImpl extends AbstractDynawoAutomationSystemModelImpl<TwoWindingsTransformer, DynawoPhaseShifterBlockingIModel>
        implements DynawoPhaseShifterBlockingIModel {

    private final ArrayList<String> phaseShifterIdPerVariant;

    public DynawoPhaseShifterBlockingIModelImpl(TwoWindingsTransformer extendable, String modelName, String parameterSetId,
                                                String dynamicModelId, String phaseShifterId) {
        super(extendable, modelName, parameterSetId, dynamicModelId);
        this.phaseShifterIdPerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.phaseShifterIdPerVariant.set(getVariantIndex(), phaseShifterId);
        perVariantList.add(phaseShifterIdPerVariant);
    }

    @Override
    public String getPhaseShifterId() {
        return phaseShifterIdPerVariant.get(getVariantIndex());
    }

    @Override
    public DynawoPhaseShifterBlockingIModel setPhaseShifterId(String phaseShifterId) {
        setAttribute(phaseShifterIdPerVariant, phaseShifterId, "phaseShifterId");
        return self();
    }

    @Override
    protected DynawoPhaseShifterBlockingIModelImpl self() {
        return this;
    }
}
