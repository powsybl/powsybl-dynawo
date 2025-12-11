/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.generator;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.extensions.api.generator.RpclType;
import com.powsybl.dynawo.extensions.api.generator.SynchronizedGeneratorProperties;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.impl.AbstractMultiVariantIdentifiableExtension;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public class SynchronizedGeneratorPropertiesImpl
        extends AbstractMultiVariantIdentifiableExtension<Generator>
        implements SynchronizedGeneratorProperties {

    private final ArrayList<String> typeByVariant;
    private final ArrayList<RpclType> rpclByVariant;

    public SynchronizedGeneratorPropertiesImpl(Generator generator, String type, RpclType rpcl) {
        super(generator);

        int variantArraySize = getVariantManagerHolder().getVariantManager().getVariantArraySize();
        this.typeByVariant = new ArrayList<>(variantArraySize);
        this.rpclByVariant = new ArrayList<>(variantArraySize);
        for (int i = 0; i < variantArraySize; i++) {
            this.typeByVariant.add(type);
            this.rpclByVariant.add(rpcl);
        }
    }

    @Override
    public String getType() {
        return this.typeByVariant.get(getVariantIndex());
    }

    @Override
    public void setType(String type) {
        this.typeByVariant.set(getVariantIndex(), Objects.requireNonNull(type, "type"));
    }

    @Override
    public boolean isRpcl2() {
        RpclType r = rpclByVariant.get(getVariantIndex());
        return r.isRpcl2();
    }

    @Override
    public RpclType getRpcl() {
        return this.rpclByVariant.get(getVariantIndex());
    }

    @Override
    public void setRpcl(RpclType rpcl) {
        if (rpcl == RpclType.RPCL1) {
            throw new PowsyblException("RPCL1 is not supported");
        }
        this.rpclByVariant.set(getVariantIndex(), rpcl);
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        this.typeByVariant.ensureCapacity(this.typeByVariant.size() + number);
        this.rpclByVariant.ensureCapacity(this.rpclByVariant.size() + number);
        String type = this.typeByVariant.get(sourceIndex);
        RpclType rpcl = this.rpclByVariant.get(sourceIndex);
        for (int i = 0; i < number; i++) {
            this.typeByVariant.add(type);
            this.rpclByVariant.add(rpcl);
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        for (int i = 0; i < number; i++) {
            int last = this.typeByVariant.size() - 1;
            this.typeByVariant.remove(last);
            this.rpclByVariant.remove(last);
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        // Nothing to do
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        String type = this.typeByVariant.get(sourceIndex);
        RpclType rpcl = this.rpclByVariant.get(sourceIndex);
        for (int index : indexes) {
            this.typeByVariant.set(index, type);
            this.rpclByVariant.set(index, rpcl);
        }
    }
}
