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
import com.powsybl.dynawo.commons.TransformerSide;
import com.powsybl.dynawo.extensions.api.model.DynawoTapChangerModel;
import com.powsybl.dynawo.extensions.api.model.DynawoTapChangerModelAdder;
import com.powsybl.iidm.network.Load;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class DynawoTapChangerModelInfoSerDe extends AbstractExtensionSerDe<Load, DynawoTapChangerModel> {

    private static final String DYNAMIC_MODEL_ID = "dynamicModelId";
    private static final String MODEL_NAME = "modelName";
    private static final String PARAMETER_SET_ID = "parameterSetId";
    private static final String SIDE = "side";

    public DynawoTapChangerModelInfoSerDe() {
        super(DynawoTapChangerModel.NAME, "network", DynawoTapChangerModel.class, "dynawoTapChangerModel.xsd",
                "http://www.powsybl.org/schema/iidm/ext/dynawo_tap_changer_model/1_0", "dtc");
    }

    @Override
    public void write(DynawoTapChangerModel dynawoTapChangerModelInfo, SerializerContext context) {
        context.getWriter().writeStringAttribute(DYNAMIC_MODEL_ID, dynawoTapChangerModelInfo.getDynamicModelId());
        context.getWriter().writeStringAttribute(MODEL_NAME, dynawoTapChangerModelInfo.getModelName());
        context.getWriter().writeStringAttribute(PARAMETER_SET_ID, dynawoTapChangerModelInfo.getParameterSetId());
        context.getWriter().writeEnumAttribute(SIDE, dynawoTapChangerModelInfo.getSide());
    }

    @Override
    public DynawoTapChangerModel read(Load load, DeserializerContext context) {
        String dynamicModelId = context.getReader().readStringAttribute(DYNAMIC_MODEL_ID);
        String modelName = context.getReader().readStringAttribute(MODEL_NAME);
        String parameterSetId = context.getReader().readStringAttribute(PARAMETER_SET_ID);
        TransformerSide side = context.getReader().readEnumAttribute(SIDE, TransformerSide.class);
        context.getReader().readEndNode();
        return load.newExtension(DynawoTapChangerModelAdder.class)
                .withModelName(modelName)
                .withParameterSetId(parameterSetId)
                .withDynamicModelId(dynamicModelId)
                .withSide(side)
                .add();
    }
}
