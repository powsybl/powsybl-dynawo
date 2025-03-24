/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions;

import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.impl.AbstractMultiVariantIdentifiableExtension;
import com.powsybl.iidm.network.impl.NetworkImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynamicAutomationSystemInfosImpl extends AbstractMultiVariantIdentifiableExtension<Network>
        implements DynamicAutomationSystemInfos {

    private final ArrayList<List<DynamicAutomationSystemInfo>> dynamicAutomationSystemInfosPerVariant;

    public DynamicAutomationSystemInfosImpl(Network extendable, List<DynamicAutomationSystemInfo> dynamicAutomationSystemInfos) {
        super(extendable);
        this.dynamicAutomationSystemInfosPerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), Collections.emptyList()));
        this.dynamicAutomationSystemInfosPerVariant.set(getVariantIndex(), dynamicAutomationSystemInfos);
    }

    @Override
    public List<DynamicAutomationSystemInfo> getDynamicAutomationSystemInfos() {
        return dynamicAutomationSystemInfosPerVariant.get(getVariantIndex());
    }

    //TODO check usage
    @Override
    public void setDynamicAutomationSystemInfos(List<DynamicAutomationSystemInfo> dynamicAutomationSystemInfos) {
        int variantIndex = getVariantIndex();
        List<DynamicAutomationSystemInfo> oldInfos = this.dynamicAutomationSystemInfosPerVariant.get(variantIndex);
        //TODO check equals
        if (!Objects.equals(oldInfos, dynamicAutomationSystemInfos)) {
            this.dynamicAutomationSystemInfosPerVariant.set(variantIndex, dynamicAutomationSystemInfos);
            NetworkImpl network = (NetworkImpl) getExtendable().getNetwork();
            String variantId = getVariantManagerHolder().getVariantManager().getWorkingVariantId();
            network.getListeners().notifyExtensionUpdate(this, "dynamicAutomationSystemInfos",
                    variantId, oldInfos, dynamicAutomationSystemInfos);
        }
    }

    @Override
    public void addDynamicAutomationSystemInfos(DynamicAutomationSystemInfo dynamicAutomationSystemInfos) {
        dynamicAutomationSystemInfosPerVariant.get(getVariantIndex()).add(dynamicAutomationSystemInfos);
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        dynamicAutomationSystemInfosPerVariant.ensureCapacity(dynamicAutomationSystemInfosPerVariant.size() + number);
        for (int i = 0; i < number; ++i) {
            dynamicAutomationSystemInfosPerVariant.add(dynamicAutomationSystemInfosPerVariant.get(sourceIndex));
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        for (int i = 0; i < number; i++) {
            dynamicAutomationSystemInfosPerVariant.remove(dynamicAutomationSystemInfosPerVariant.size() - 1); // remove elements from the top to avoid moves inside the array
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        dynamicAutomationSystemInfosPerVariant.set(index, null);
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        List<DynamicAutomationSystemInfo> sourceInfos = dynamicAutomationSystemInfosPerVariant.get(sourceIndex);
        for (int index : indexes) {
            dynamicAutomationSystemInfosPerVariant.set(index, sourceInfos);
        }
    }
}
