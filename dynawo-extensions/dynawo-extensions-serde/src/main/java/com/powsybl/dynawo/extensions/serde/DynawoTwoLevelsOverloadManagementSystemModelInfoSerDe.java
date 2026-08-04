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
import com.powsybl.dynawo.extensions.api.model.DynawoTwoLevelsOverloadManagementSystemModel;
import com.powsybl.dynawo.extensions.api.model.DynawoTwoLevelsOverloadManagementSystemModelAdder;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class DynawoTwoLevelsOverloadManagementSystemModelInfoSerDe<B extends Branch<B>>
        extends AbstractExtensionSerDe<B, DynawoTwoLevelsOverloadManagementSystemModel<B>> {

    private static final String DYNAMIC_MODEL_ID = "dynamicModelId";
    private static final String MODEL_NAME = "modelName";
    private static final String PARAMETER_SET_ID = "parameterSetId";
    private static final String I_MEASUREMENT_1 = "iMeasurement1";
    private static final String I_MEASUREMENT_1_SIDE = "iMeasurement1Side";
    private static final String I_MEASUREMENT_2 = "iMeasurement2";
    private static final String I_MEASUREMENT_2_SIDE = "iMeasurement2Side";

    public DynawoTwoLevelsOverloadManagementSystemModelInfoSerDe() {
        super(DynawoTwoLevelsOverloadManagementSystemModel.NAME, "network", DynawoTwoLevelsOverloadManagementSystemModel.class, "dynawoTwoLevelsOverloadManagementSystemModel.xsd",
                "http://www.powsybl.org/schema/iidm/ext/dynawo_two_levels_overload_management_system_model/1_0", "dtloms");
    }

    @Override
    public void write(DynawoTwoLevelsOverloadManagementSystemModel dynawoTwoLevelsOverloadManagementSystemModelInfo, SerializerContext context) {
        context.getWriter().writeStringAttribute(DYNAMIC_MODEL_ID, dynawoTwoLevelsOverloadManagementSystemModelInfo.getDynamicModelId());
        context.getWriter().writeStringAttribute(MODEL_NAME, dynawoTwoLevelsOverloadManagementSystemModelInfo.getModelName());
        context.getWriter().writeStringAttribute(PARAMETER_SET_ID, dynawoTwoLevelsOverloadManagementSystemModelInfo.getParameterSetId());
        context.getWriter().writeStringAttribute(I_MEASUREMENT_1, dynawoTwoLevelsOverloadManagementSystemModelInfo.getIMeasurement1());
        context.getWriter().writeEnumAttribute(I_MEASUREMENT_1_SIDE, dynawoTwoLevelsOverloadManagementSystemModelInfo.getIMeasurement1Side());
        context.getWriter().writeStringAttribute(I_MEASUREMENT_2, dynawoTwoLevelsOverloadManagementSystemModelInfo.getIMeasurement2());
        context.getWriter().writeEnumAttribute(I_MEASUREMENT_2_SIDE, dynawoTwoLevelsOverloadManagementSystemModelInfo.getIMeasurement2Side());
    }

    @Override
    public DynawoTwoLevelsOverloadManagementSystemModel<B> read(B branch, DeserializerContext context) {
        String dynamicModelId = context.getReader().readStringAttribute(DYNAMIC_MODEL_ID);
        String modelName = context.getReader().readStringAttribute(MODEL_NAME);
        String parameterSetId = context.getReader().readStringAttribute(PARAMETER_SET_ID);
        String iMeasurement = context.getReader().readStringAttribute(I_MEASUREMENT_1);
        TwoSides iMeasurementSide = context.getReader().readEnumAttribute(I_MEASUREMENT_1_SIDE, TwoSides.class);
        String iMeasurement2 = context.getReader().readStringAttribute(I_MEASUREMENT_2);
        TwoSides iMeasurement2Side = context.getReader().readEnumAttribute(I_MEASUREMENT_2_SIDE, TwoSides.class);
        context.getReader().readEndNode();
        DynawoTwoLevelsOverloadManagementSystemModelAdder<B> adder = branch.newExtension(DynawoTwoLevelsOverloadManagementSystemModelAdder.class);
        return adder.withModelName(modelName)
                .withParameterSetId(parameterSetId)
                .withDynamicModelId(dynamicModelId)
                .withIMeasurement1(iMeasurement)
                .withIMeasurement1Side(iMeasurementSide)
                .withIMeasurement2(iMeasurement2)
                .withIMeasurement2Side(iMeasurement2Side)
                .add();
    }
}
