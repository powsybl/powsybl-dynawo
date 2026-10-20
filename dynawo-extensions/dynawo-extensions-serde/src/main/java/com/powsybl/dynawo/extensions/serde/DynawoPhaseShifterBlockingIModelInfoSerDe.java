/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.serde;

import com.google.auto.service.AutoService;
import com.powsybl.commons.extensions.AbstractExtensionSerDe;
import com.powsybl.commons.extensions.ExtensionSerDe;
import com.powsybl.commons.io.DeserializerContext;
import com.powsybl.commons.io.SerializerContext;
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterBlockingIModel;
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterBlockingIModelAdder;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class DynawoPhaseShifterBlockingIModelInfoSerDe extends AbstractExtensionSerDe<TwoWindingsTransformer, DynawoPhaseShifterBlockingIModel> {

    private static final String DYNAMIC_MODEL_ID = "dynamicModelId";
    private static final String MODEL_NAME = "modelName";
    private static final String PARAMETER_SET_ID = "parameterSetId";
    private static final String PHASE_SHIFTER_ID = "phaseShifterId";

    public DynawoPhaseShifterBlockingIModelInfoSerDe() {
        super(DynawoPhaseShifterBlockingIModel.NAME, "network", DynawoPhaseShifterBlockingIModel.class, "dynawoPhaseShifterBlockingIModel.xsd",
                "http://www.powsybl.org/schema/iidm/ext/dynawo_phase_shifter_blocking_i_model/1_0", "dpsbi");
    }

    @Override
    public void write(DynawoPhaseShifterBlockingIModel dynawoPhaseShifterBlockingIModelInfo, SerializerContext context) {
        context.getWriter().writeStringAttribute(DYNAMIC_MODEL_ID, dynawoPhaseShifterBlockingIModelInfo.getDynamicModelId());
        context.getWriter().writeStringAttribute(MODEL_NAME, dynawoPhaseShifterBlockingIModelInfo.getModelName());
        context.getWriter().writeStringAttribute(PARAMETER_SET_ID, dynawoPhaseShifterBlockingIModelInfo.getParameterSetId());
        context.getWriter().writeStringAttribute(PHASE_SHIFTER_ID, dynawoPhaseShifterBlockingIModelInfo.getPhaseShifterId());
    }

    @Override
    public DynawoPhaseShifterBlockingIModel read(TwoWindingsTransformer twoWindingsTransformer, DeserializerContext context) {
        String dynamicModelId = context.getReader().readStringAttribute(DYNAMIC_MODEL_ID);
        String modelName = context.getReader().readStringAttribute(MODEL_NAME);
        String parameterSetId = context.getReader().readStringAttribute(PARAMETER_SET_ID);
        String phaseShifterId = context.getReader().readStringAttribute(PHASE_SHIFTER_ID);

        context.getReader().readEndNode();
        return twoWindingsTransformer.newExtension(DynawoPhaseShifterBlockingIModelAdder.class)
                .withModelName(modelName)
                .withParameterSetId(parameterSetId)
                .withDynamicModelId(dynamicModelId)
                .withPhaseShifterId(phaseShifterId)
                .add();
    }
}
