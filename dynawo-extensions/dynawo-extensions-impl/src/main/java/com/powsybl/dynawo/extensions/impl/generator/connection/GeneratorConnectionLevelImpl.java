/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.generator.connection;

import com.powsybl.dynawo.extensions.api.generator.connection.GeneratorConnectionLevel;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.impl.AbstractMultiVariantIdentifiableExtension;

import java.util.ArrayList;
import java.util.Objects;

/**
 * @author Gautier Bureau {@literal <gautier.bureau at rte-france.com>}
 */
public class GeneratorConnectionLevelImpl extends AbstractMultiVariantIdentifiableExtension<Generator>
        implements GeneratorConnectionLevel {

    private final ArrayList<GeneratorConnectionLevelType> levelByVariant;

    public GeneratorConnectionLevelImpl(Generator generator, GeneratorConnectionLevelType initialLevel) {
        super(generator);
        Objects.requireNonNull(initialLevel, "initialLevel");
        int variantArraySize = getVariantManagerHolder().getVariantManager().getVariantArraySize();
        this.levelByVariant = new ArrayList<>(variantArraySize);
        for (int i = 0; i < variantArraySize; i++) {
            this.levelByVariant.add(initialLevel);
        }
    }

    @Override
    public GeneratorConnectionLevelType getLevel() {
        return levelByVariant.get(getVariantIndex());
    }

    @Override
    public void setLevel(GeneratorConnectionLevelType level) {
        this.levelByVariant.set(getVariantIndex(), Objects.requireNonNull(level, "level"));
    }

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        levelByVariant.ensureCapacity(levelByVariant.size() + number);
        GeneratorConnectionLevelType source = levelByVariant.get(sourceIndex);
        for (int i = 0; i < number; i++) {
            levelByVariant.add(source);
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        for (int i = 0; i < number; i++) {
            int last = this.levelByVariant.size() - 1;
            this.levelByVariant.remove(last);
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        //Nothing to do
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        GeneratorConnectionLevelType source = levelByVariant.get(sourceIndex);
        for (int idx : indexes) {
            levelByVariant.set(idx, source);
        }
    }
}
