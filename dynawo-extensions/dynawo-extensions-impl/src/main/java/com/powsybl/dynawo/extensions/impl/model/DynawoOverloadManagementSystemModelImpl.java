/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.extensions.api.model.DynawoOverloadManagementSystemModel;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.impl.NetworkImpl;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoOverloadManagementSystemModelImpl<B extends Branch<B>>
        extends AbstractDynawoAutomationSystemModelImpl<B, DynawoOverloadManagementSystemModel<B>>
        implements DynawoOverloadManagementSystemModel<B> {

    private final ArrayList<String> iMeasurementPerVariant;
    private final ArrayList<TwoSides> iMeasurementSidePerVariant;

    public DynawoOverloadManagementSystemModelImpl(B extendable, String modelName, String parameterSetId,
                                                   String dynamicModelId, String iMeasurement, TwoSides iMeasurementSide) {
        super(extendable, modelName, parameterSetId, dynamicModelId);
        this.iMeasurementPerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.iMeasurementPerVariant.set(getVariantIndex(), iMeasurement);
        perVariantList.add(iMeasurementPerVariant);
        this.iMeasurementSidePerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.iMeasurementSidePerVariant.set(getVariantIndex(), iMeasurementSide);
    }

    @Override
    public String getIMeasurement() {
        return iMeasurementPerVariant.get(getVariantIndex());
    }

    @Override
    public DynawoOverloadManagementSystemModel<B> setIMeasurement(String iMeasurement) {
        setAttribute(iMeasurementPerVariant, iMeasurement, "iMeasurement");
        return self();
    }

    @Override
    public TwoSides getIMeasurementSide() {
        return iMeasurementSidePerVariant.get(getVariantIndex());
    }

    @Override
    public DynawoOverloadManagementSystemModel<B> setIMeasurementSide(TwoSides iMeasurementSide) {
        int variantIndex = getVariantIndex();
        TwoSides oldSide = iMeasurementSidePerVariant.get(variantIndex);
        if (oldSide != iMeasurementSide) {
            iMeasurementSidePerVariant.set(variantIndex, iMeasurementSide);
            NetworkImpl network = (NetworkImpl) getExtendable().getNetwork();
            String variantId = getVariantManagerHolder().getVariantManager().getWorkingVariantId();
            network.getListeners().notifyExtensionUpdate(this, "iMeasurementSide", variantId, oldSide, iMeasurementSide);
        }
        return self();
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        super.extendVariantArraySize(initVariantArraySize, number, sourceIndex);
        iMeasurementSidePerVariant.ensureCapacity(iMeasurementSidePerVariant.size() + number);
        TwoSides src = iMeasurementSidePerVariant.get(sourceIndex);
        for (int i = 0; i < number; i++) {
            iMeasurementSidePerVariant.add(src);
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        super.reduceVariantArraySize(number);
        for (int i = 0; i < number; i++) {
            int lastIdx = iMeasurementSidePerVariant.size() - 1;
            iMeasurementSidePerVariant.remove(lastIdx);
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        super.deleteVariantArrayElement(index);
        iMeasurementSidePerVariant.set(index, null);
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        super.allocateVariantArrayElement(indexes, sourceIndex);
        TwoSides srcSide = iMeasurementSidePerVariant.get(sourceIndex);
        for (int idx : indexes) {
            iMeasurementSidePerVariant.set(idx, srcSide);
        }
    }

    @Override
    protected DynawoOverloadManagementSystemModelImpl<B> self() {
        return this;
    }
}
