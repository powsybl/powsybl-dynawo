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
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterPModel;
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterPModelAdder;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class DynawoPhaseShifterPModelInfoSerDe extends AbstractExtensionSerDe<TwoWindingsTransformer, DynawoPhaseShifterPModel> {

    private static final String DYNAMIC_MODEL_ID = "dynamicModelId";
    private static final String MODEL_NAME = "modelName";
    private static final String PARAMETER_SET_ID = "parameterSetId";

    public DynawoPhaseShifterPModelInfoSerDe() {
        super(DynawoPhaseShifterPModel.NAME, "network", DynawoPhaseShifterPModel.class, "dynawoPhaseShifterPModel.xsd",
                "http://www.powsybl.org/schema/iidm/ext/dynawo_phase_shifter_p_model/1_0", "dpsp");
    }

    @Override
    public void write(DynawoPhaseShifterPModel dynawoPhaseShifterPModelInfo, SerializerContext context) {
        context.getWriter().writeStringAttribute(DYNAMIC_MODEL_ID, dynawoPhaseShifterPModelInfo.getDynamicModelId());
        context.getWriter().writeStringAttribute(MODEL_NAME, dynawoPhaseShifterPModelInfo.getModelName());
        context.getWriter().writeStringAttribute(PARAMETER_SET_ID, dynawoPhaseShifterPModelInfo.getParameterSetId());
    }

    @Override
    public DynawoPhaseShifterPModel read(TwoWindingsTransformer twoWindingsTransformer, DeserializerContext context) {
        String dynamicModelId = context.getReader().readStringAttribute(DYNAMIC_MODEL_ID);
        String modelName = context.getReader().readStringAttribute(MODEL_NAME);
        String parameterSetId = context.getReader().readStringAttribute(PARAMETER_SET_ID);
        context.getReader().readEndNode();
        return twoWindingsTransformer.newExtension(DynawoPhaseShifterPModelAdder.class)
                .withModelName(modelName)
                .withParameterSetId(parameterSetId)
                .withDynamicModelId(dynamicModelId)
                .add();
    }
}
