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
import com.powsybl.dynawo.extensions.api.model.DynawoEquipmentModel;
import com.powsybl.dynawo.extensions.api.model.DynawoEquipmentModelAdder;
import com.powsybl.iidm.network.Identifiable;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ExtensionSerDe.class)
public class DynawoEquipmentModelSerDe<I extends Identifiable<I>> extends AbstractExtensionSerDe<I, DynawoEquipmentModel<I>> {

    private static final String MODEL_NAME = "modelName";
    private static final String PARAMETER_SET_ID = "parameterSetId";

    public DynawoEquipmentModelSerDe() {
        super(DynawoEquipmentModel.NAME, "network", DynawoEquipmentModel.class, "dynawoEquipmentModel.xsd",
                "http://www.powsybl.org/schema/iidm/ext/dynawo_equipment_model/1_0", "dem");
    }

    @Override
    public void write(DynawoEquipmentModel<I> dynawoEquipmentModel, SerializerContext context) {
        context.getWriter().writeStringAttribute(MODEL_NAME, dynawoEquipmentModel.getModelName());
        context.getWriter().writeStringAttribute(PARAMETER_SET_ID, dynawoEquipmentModel.getParameterSetId());
    }

    @Override
    public DynawoEquipmentModel<I> read(I identifiable, DeserializerContext context) {
        String modelName = context.getReader().readStringAttribute(MODEL_NAME);
        String parameterSetId = context.getReader().readStringAttribute(PARAMETER_SET_ID);
        context.getReader().readEndNode();
        DynawoEquipmentModelAdder<I> adder = identifiable.newExtension(DynawoEquipmentModelAdder.class);
        return adder.withModelName(modelName)
                .withParameterSetId(parameterSetId)
                .add();
    }
}
