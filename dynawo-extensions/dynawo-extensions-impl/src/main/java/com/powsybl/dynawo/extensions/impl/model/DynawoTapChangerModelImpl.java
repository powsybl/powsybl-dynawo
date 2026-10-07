/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.commons.TransformerSide;
import com.powsybl.dynawo.extensions.api.model.DynawoTapChangerModel;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.impl.NetworkImpl;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoTapChangerModelImpl extends AbstractDynawoAutomationSystemModelImpl<Load, DynawoTapChangerModel> implements DynawoTapChangerModel {

    private final ArrayList<TransformerSide> sidePerVariant;

    public DynawoTapChangerModelImpl(Load extendable, String modelName, String parameterSetId, String dynamicModelId, TransformerSide side) {
        super(extendable, modelName, parameterSetId, dynamicModelId);
        this.sidePerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.sidePerVariant.set(getVariantIndex(), side);
    }

    @Override
    public TransformerSide getSide() {
        return sidePerVariant.get(getVariantIndex());
    }

    @Override
    public DynawoTapChangerModel setSide(TransformerSide side) {
        int variantIndex = getVariantIndex();
        TransformerSide oldSide = sidePerVariant.get(variantIndex);
        if (oldSide != side) {
            sidePerVariant.set(variantIndex, side);
            NetworkImpl network = (NetworkImpl) getExtendable().getNetwork();
            String variantId = getVariantManagerHolder().getVariantManager().getWorkingVariantId();
            network.getListeners().notifyExtensionUpdate(this, "side", variantId, oldSide, side);
        }
        return self();
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        super.extendVariantArraySize(initVariantArraySize, number, sourceIndex);
        sidePerVariant.ensureCapacity(sidePerVariant.size() + number);
        TransformerSide src = sidePerVariant.get(sourceIndex);
        for (int i = 0; i < number; i++) {
            sidePerVariant.add(src);
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        super.reduceVariantArraySize(number);
        for (int i = 0; i < number; i++) {
            int last = sidePerVariant.size() - 1;
            sidePerVariant.remove(last);
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        super.deleteVariantArrayElement(index);
        sidePerVariant.set(index, null);
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        super.allocateVariantArrayElement(indexes, sourceIndex);
        TransformerSide src = sidePerVariant.get(sourceIndex);
        for (int idx : indexes) {
            sidePerVariant.set(idx, src);
        }
    }

    @Override
    protected DynawoTapChangerModelImpl self() {
        return this;
    }
}
