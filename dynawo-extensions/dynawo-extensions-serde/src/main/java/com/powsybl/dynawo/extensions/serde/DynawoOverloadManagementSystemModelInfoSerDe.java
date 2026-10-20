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
import com.powsybl.dynawo.extensions.api.model.DynawoOverloadManagementSystemModel;
import com.powsybl.dynawo.extensions.api.model.DynawoOverloadManagementSystemModelAdder;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class DynawoOverloadManagementSystemModelInfoSerDe<B extends Branch<B>>
        extends AbstractExtensionSerDe<B, DynawoOverloadManagementSystemModel<B>> {

    private static final String DYNAMIC_MODEL_ID = "dynamicModelId";
    private static final String MODEL_NAME = "modelName";
    private static final String PARAMETER_SET_ID = "parameterSetId";
    private static final String I_MEASUREMENT = "iMeasurement";
    private static final String I_MEASUREMENT_SIDE = "iMeasurementSide";

    public DynawoOverloadManagementSystemModelInfoSerDe() {
        super(DynawoOverloadManagementSystemModel.NAME, "network", DynawoOverloadManagementSystemModel.class, "dynawoOverloadManagementSystemModel.xsd",
                "http://www.powsybl.org/schema/iidm/ext/dynawo_overload_management_system_model/1_0", "doms");
    }

    @Override
    public void write(DynawoOverloadManagementSystemModel dynawoOverloadManagementSystemModelInfo, SerializerContext context) {
        context.getWriter().writeStringAttribute(DYNAMIC_MODEL_ID, dynawoOverloadManagementSystemModelInfo.getDynamicModelId());
        context.getWriter().writeStringAttribute(MODEL_NAME, dynawoOverloadManagementSystemModelInfo.getModelName());
        context.getWriter().writeStringAttribute(PARAMETER_SET_ID, dynawoOverloadManagementSystemModelInfo.getParameterSetId());
        context.getWriter().writeStringAttribute(I_MEASUREMENT, dynawoOverloadManagementSystemModelInfo.getIMeasurement());
        context.getWriter().writeEnumAttribute(I_MEASUREMENT_SIDE, dynawoOverloadManagementSystemModelInfo.getIMeasurementSide());
    }

    @Override
    public DynawoOverloadManagementSystemModel<B> read(B branch, DeserializerContext context) {
        String dynamicModelId = context.getReader().readStringAttribute(DYNAMIC_MODEL_ID);
        String modelName = context.getReader().readStringAttribute(MODEL_NAME);
        String parameterSetId = context.getReader().readStringAttribute(PARAMETER_SET_ID);
        String iMeasurement = context.getReader().readStringAttribute(I_MEASUREMENT);
        TwoSides iMeasurementSide = context.getReader().readEnumAttribute(I_MEASUREMENT_SIDE, TwoSides.class);
        context.getReader().readEndNode();
        DynawoOverloadManagementSystemModelAdder<B> adder = branch.newExtension(DynawoOverloadManagementSystemModelAdder.class);
        return adder.withModelName(modelName)
                .withParameterSetId(parameterSetId)
                .withDynamicModelId(dynamicModelId)
                .withIMeasurement(iMeasurement)
                .withIMeasurementSide(iMeasurementSide)
                .add();
    }
}
