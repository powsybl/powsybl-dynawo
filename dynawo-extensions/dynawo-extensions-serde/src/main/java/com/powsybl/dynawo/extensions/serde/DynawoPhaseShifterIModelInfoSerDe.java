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
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterIModel;
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterIModelAdder;
import com.powsybl.iidm.network.TwoWindingsTransformer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class DynawoPhaseShifterIModelInfoSerDe extends AbstractExtensionSerDe<TwoWindingsTransformer, DynawoPhaseShifterIModel> {

    private static final String DYNAMIC_MODEL_ID = "dynamicModelId";
    private static final String MODEL_NAME = "modelName";
    private static final String PARAMETER_SET_ID = "parameterSetId";

    public DynawoPhaseShifterIModelInfoSerDe() {
        super(DynawoPhaseShifterIModel.NAME, "network", DynawoPhaseShifterIModel.class, "dynawoPhaseShifterIModel.xsd",
                "http://www.powsybl.org/schema/iidm/ext/dynawo_phase_shifter_i_model/1_0", "dpsi");
    }

    @Override
    public void write(DynawoPhaseShifterIModel dynawoPhaseShifterIModelInfo, SerializerContext context) {
        context.getWriter().writeStringAttribute(DYNAMIC_MODEL_ID, dynawoPhaseShifterIModelInfo.getDynamicModelId());
        context.getWriter().writeStringAttribute(MODEL_NAME, dynawoPhaseShifterIModelInfo.getModelName());
        context.getWriter().writeStringAttribute(PARAMETER_SET_ID, dynawoPhaseShifterIModelInfo.getParameterSetId());
    }

    @Override
    public DynawoPhaseShifterIModel read(TwoWindingsTransformer twoWindingsTransformer, DeserializerContext context) {
        String dynamicModelId = context.getReader().readStringAttribute(DYNAMIC_MODEL_ID);
        String modelName = context.getReader().readStringAttribute(MODEL_NAME);
        String parameterSetId = context.getReader().readStringAttribute(PARAMETER_SET_ID);
        context.getReader().readEndNode();
        return twoWindingsTransformer.newExtension(DynawoPhaseShifterIModelAdder.class)
                .withModelName(modelName)
                .withParameterSetId(parameterSetId)
                .withDynamicModelId(dynamicModelId)
                .add();
    }
}
