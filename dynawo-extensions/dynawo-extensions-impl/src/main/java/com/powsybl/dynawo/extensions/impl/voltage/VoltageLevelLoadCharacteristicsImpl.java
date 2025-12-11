/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.voltage;

import com.powsybl.dynawo.extensions.api.voltage.VoltageLevelLoadCharacteristics;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.impl.AbstractMultiVariantIdentifiableExtension;

import java.util.ArrayList;
import java.util.Objects;

/**
 *
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public class VoltageLevelLoadCharacteristicsImpl
        extends AbstractMultiVariantIdentifiableExtension<VoltageLevel>
        implements VoltageLevelLoadCharacteristics {

    private final ArrayList<Type> characteristicByVariant;

    public VoltageLevelLoadCharacteristicsImpl(VoltageLevel voltageLevel,
                                               Type initialCharacteristic) {
        super(voltageLevel);
        Objects.requireNonNull(initialCharacteristic, "initialCharacteristic");
        int variantArraySize = getVariantManagerHolder().getVariantManager().getVariantArraySize();
        this.characteristicByVariant = new ArrayList<>(variantArraySize);
        for (int i = 0; i < variantArraySize; i++) {
            this.characteristicByVariant.add(initialCharacteristic);
        }
    }

    @Override
    public Type getCharacteristic() {
        return characteristicByVariant.get(getVariantIndex());
    }

    @Override
    public void setCharacteristic(Type characteristic) {
        characteristicByVariant.set(getVariantIndex(), Objects.requireNonNull(characteristic, "characteristic"));
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        characteristicByVariant.ensureCapacity(characteristicByVariant.size() + number);
        Type src = characteristicByVariant.get(sourceIndex);
        for (int i = 0; i < number; i++) {
            characteristicByVariant.add(src);
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        for (int i = 0; i < number; i++) {
            int last = characteristicByVariant.size() - 1;
            characteristicByVariant.remove(last);
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        // Nothing to do
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        Type src = characteristicByVariant.get(sourceIndex);
        for (int idx : indexes) {
            characteristicByVariant.set(idx, src);
        }
    }
}
