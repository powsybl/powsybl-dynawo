/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.extensions.api.model.DynawoTwoLevelsOverloadManagementSystemModel;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.TwoSides;
import com.powsybl.iidm.network.impl.NetworkImpl;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoTwoLevelsOverloadManagementSystemModelImpl<B extends Branch<B>>
        extends AbstractDynawoAutomationSystemModelImpl<B, DynawoTwoLevelsOverloadManagementSystemModel<B>>
        implements DynawoTwoLevelsOverloadManagementSystemModel<B> {

    private final ArrayList<String> iMeasurement1PerVariant;
    private final ArrayList<TwoSides> iMeasurement1SidePerVariant;
    private final ArrayList<String> iMeasurement2PerVariant;
    private final ArrayList<TwoSides> iMeasurement2SidePerVariant;

    public DynawoTwoLevelsOverloadManagementSystemModelImpl(B extendable, String modelName, String parameterSetId,
                                                            String dynamicModelId, String iMeasurement1, TwoSides iMeasurement1Side,
                                                            String iMeasurement2, TwoSides iMeasurement2Side) {
        super(extendable, modelName, parameterSetId, dynamicModelId);
        this.iMeasurement1PerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.iMeasurement1PerVariant.set(getVariantIndex(), iMeasurement1);
        perVariantList.add(iMeasurement1PerVariant);
        this.iMeasurement1SidePerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.iMeasurement1SidePerVariant.set(getVariantIndex(), iMeasurement1Side);
        this.iMeasurement2PerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.iMeasurement2PerVariant.set(getVariantIndex(), iMeasurement2);
        perVariantList.add(iMeasurement2PerVariant);
        this.iMeasurement2SidePerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.iMeasurement2SidePerVariant.set(getVariantIndex(), iMeasurement2Side);

    }

    @Override
    public String getIMeasurement1() {
        return iMeasurement1PerVariant.get(getVariantIndex());
    }

    @Override
    public DynawoTwoLevelsOverloadManagementSystemModel<B> setIMeasurement1(String iMeasurement1) {
        setAttribute(iMeasurement1PerVariant, iMeasurement1, "iMeasurement1");
        return self();
    }

    @Override
    public TwoSides getIMeasurement1Side() {
        return iMeasurement1SidePerVariant.get(getVariantIndex());
    }

    @Override
    public DynawoTwoLevelsOverloadManagementSystemModel<B> setIMeasurement1Side(TwoSides iMeasurement1Side) {
        int variantIndex = getVariantIndex();
        TwoSides oldSide = iMeasurement1SidePerVariant.get(variantIndex);
        if (oldSide != iMeasurement1Side) {
            iMeasurement1SidePerVariant.set(variantIndex, iMeasurement1Side);
            NetworkImpl network = (NetworkImpl) getExtendable().getNetwork();
            String variantId = getVariantManagerHolder().getVariantManager().getWorkingVariantId();
            network.getListeners().notifyExtensionUpdate(this, "iMeasurement1Side", variantId, oldSide, iMeasurement1Side);
        }
        return self();
    }

    @Override
    public String getIMeasurement2() {
        return iMeasurement2PerVariant.get(getVariantIndex());
    }

    @Override
    public DynawoTwoLevelsOverloadManagementSystemModel<B> setIMeasurement2(String iMeasurement2) {
        setAttribute(iMeasurement2PerVariant, iMeasurement2, "iMeasurement2");
        return self();
    }

    @Override
    public TwoSides getIMeasurement2Side() {
        return iMeasurement2SidePerVariant.get(getVariantIndex());
    }

    @Override
    public DynawoTwoLevelsOverloadManagementSystemModel<B> setIMeasurement2Side(TwoSides iMeasurement2Side) {
        int variantIndex = getVariantIndex();
        TwoSides oldSide = iMeasurement2SidePerVariant.get(variantIndex);
        if (oldSide != iMeasurement2Side) {
            iMeasurement2SidePerVariant.set(variantIndex, iMeasurement2Side);
            NetworkImpl network = (NetworkImpl) getExtendable().getNetwork();
            String variantId = getVariantManagerHolder().getVariantManager().getWorkingVariantId();
            network.getListeners().notifyExtensionUpdate(this, "iMeasurement2Side", variantId, oldSide, iMeasurement2Side);
        }
        return self();
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        super.extendVariantArraySize(initVariantArraySize, number, sourceIndex);
        iMeasurement1SidePerVariant.ensureCapacity(iMeasurement1SidePerVariant.size() + number);
        iMeasurement2SidePerVariant.ensureCapacity(iMeasurement2SidePerVariant.size() + number);
        TwoSides src = iMeasurement1SidePerVariant.get(sourceIndex);
        TwoSides src2 = iMeasurement2SidePerVariant.get(sourceIndex);
        for (int i = 0; i < number; i++) {
            iMeasurement1SidePerVariant.add(src);
            iMeasurement2SidePerVariant.add(src2);
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        super.reduceVariantArraySize(number);
        for (int i = 0; i < number; i++) {
            int lastIdx = iMeasurement1SidePerVariant.size() - 1;
            iMeasurement1SidePerVariant.remove(lastIdx);
            iMeasurement2SidePerVariant.remove(lastIdx);
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        super.deleteVariantArrayElement(index);
        iMeasurement1SidePerVariant.set(index, null);
        iMeasurement2SidePerVariant.set(index, null);
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        super.allocateVariantArrayElement(indexes, sourceIndex);
        TwoSides srcSide = iMeasurement1SidePerVariant.get(sourceIndex);
        TwoSides src2Side = iMeasurement2SidePerVariant.get(sourceIndex);
        for (int idx : indexes) {
            iMeasurement1SidePerVariant.set(idx, srcSide);
            iMeasurement2SidePerVariant.set(idx, src2Side);
        }
    }

    @Override
    protected DynawoTwoLevelsOverloadManagementSystemModelImpl<B> self() {
        return this;
    }
}
