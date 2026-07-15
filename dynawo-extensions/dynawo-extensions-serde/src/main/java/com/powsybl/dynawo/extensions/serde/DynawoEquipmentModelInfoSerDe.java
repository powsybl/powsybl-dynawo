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
import com.powsybl.dynawo.extensions.api.info.DynawoEquipmentModelInfo;
import com.powsybl.dynawo.extensions.api.info.DynawoEquipmentModelInfoAdder;
import com.powsybl.iidm.network.Identifiable;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class DynawoEquipmentModelInfoSerDe<I extends Identifiable<I>> extends AbstractExtensionSerDe<I, DynawoEquipmentModelInfo<I>> {

    private static final String MODEL_NAME = "modelName";
    private static final String PARAMETER_SET_ID = "parameterSetId";

    public DynawoEquipmentModelInfoSerDe() {
        super(DynawoEquipmentModelInfo.NAME, "network", DynawoEquipmentModelInfo.class, "dynawoEquipmentModelInfo.xsd",
                "http://www.powsybl.org/schema/iidm/ext/dynawo_equipment_model_info/1_0", "demi");
    }

    @Override
    public void write(DynawoEquipmentModelInfo<I> dynawoEquipmentModelInfo, SerializerContext context) {
        context.getWriter().writeStringAttribute(MODEL_NAME, dynawoEquipmentModelInfo.getModelName());
        context.getWriter().writeStringAttribute(PARAMETER_SET_ID, dynawoEquipmentModelInfo.getParameterSetId());
    }

    @Override
    public DynawoEquipmentModelInfo<I> read(I identifiable, DeserializerContext context) {
        String modelName = context.getReader().readStringAttribute(MODEL_NAME);
        String parameterSetId = context.getReader().readStringAttribute(PARAMETER_SET_ID);
        context.getReader().readEndNode();
        DynawoEquipmentModelInfoAdder<I> adder = identifiable.newExtension(DynawoEquipmentModelInfoAdder.class);
        return adder.setModelName(modelName)
                .setParameterSetId(parameterSetId)
                .add();
    }
}
