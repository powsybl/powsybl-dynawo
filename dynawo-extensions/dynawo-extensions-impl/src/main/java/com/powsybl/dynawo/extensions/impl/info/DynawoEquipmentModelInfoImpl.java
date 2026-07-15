/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.info;

import com.powsybl.dynawo.extensions.api.info.DynawoEquipmentModelInfo;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.impl.NetworkImpl;
import com.powsybl.iidm.network.impl.extensions.DynamicModelInfoImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoEquipmentModelInfoImpl<I extends Identifiable<I>> extends DynamicModelInfoImpl<I> implements DynawoEquipmentModelInfo<I> {

    private final ArrayList<String> parameterSetIdPerVariant;

    public DynawoEquipmentModelInfoImpl(I extendable, String modelName, String parameterSetId) {
        super(extendable, modelName);
        this.parameterSetIdPerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.parameterSetIdPerVariant.set(getVariantIndex(), parameterSetId);
    }

    @Override
    public String getParameterSetId() {
        return parameterSetIdPerVariant.get(getVariantIndex());
    }

    @Override
    public void setParameterSetId(String parameterSetId) {
        int variantIndex = getVariantIndex();
        String oldParameterSetId = parameterSetIdPerVariant.get(variantIndex);
        if (!Objects.equals(oldParameterSetId, parameterSetId)) {
            parameterSetIdPerVariant.set(variantIndex, parameterSetId);
            NetworkImpl network = (NetworkImpl) getExtendable().getNetwork();
            String variantId = getVariantManagerHolder().getVariantManager().getWorkingVariantId();
            network.getListeners().notifyExtensionUpdate(this, "parameterSetId", variantId, oldParameterSetId, parameterSetId);
        }
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        super.extendVariantArraySize(initVariantArraySize, number, sourceIndex);
        parameterSetIdPerVariant.ensureCapacity(parameterSetIdPerVariant.size() + number);
        for (int i = 0; i < number; ++i) {
            parameterSetIdPerVariant.add(parameterSetIdPerVariant.get(sourceIndex));
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        super.reduceVariantArraySize(number);
        for (int i = 0; i < number; i++) {
            parameterSetIdPerVariant.remove(parameterSetIdPerVariant.size() - 1); // remove elements from the top to avoid moves inside the array
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        super.deleteVariantArrayElement(index);
        parameterSetIdPerVariant.set(index, null);
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        super.allocateVariantArrayElement(indexes, sourceIndex);
        String sourceModelName = parameterSetIdPerVariant.get(sourceIndex);
        for (int index : indexes) {
            parameterSetIdPerVariant.set(index, sourceModelName);
        }
    }
}
