/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.info;

import com.powsybl.dynawo.extensions.api.info.DynawoUnderVoltageModelInfo;
import com.powsybl.iidm.network.Generator;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoUnderVoltageModelInfoImpl extends AbstractDynawoModelInfo<Generator, DynawoUnderVoltageModelInfo> implements DynawoUnderVoltageModelInfo {

    private final ArrayList<String> dynamicModelIdPerVariant;

    public DynawoUnderVoltageModelInfoImpl(Generator extendable, String modelName, String parameterSetId, String dynamicModelId) {
        super(extendable, modelName, parameterSetId);
        this.dynamicModelIdPerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.dynamicModelIdPerVariant.set(getVariantIndex(), dynamicModelId);
        perVariantList.add(dynamicModelIdPerVariant);
    }

    @Override
    public String getDynamicModelId() {
        return dynamicModelIdPerVariant.get(getVariantIndex());
    }

    @Override
    public DynawoUnderVoltageModelInfoImpl setDynamicModelId(String dynamicModelId) {
        setAttribute(dynamicModelIdPerVariant, dynamicModelId, "dynamicModelId");
        return this;
    }

    @Override
    protected DynawoUnderVoltageModelInfoImpl self() {
        return this;
    }
}
