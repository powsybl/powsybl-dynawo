/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.dynawo.extensions.api.model.DynawoModelExtension;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.impl.AbstractMultiVariantIdentifiableExtension;
import com.powsybl.iidm.network.impl.NetworkImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
//TODO rajouter une couche automate
public abstract class AbstractDynawoModel<I extends Identifiable<I>, E extends DynawoModelExtension<I, E>>
        extends AbstractMultiVariantIdentifiableExtension<I> implements DynawoModelExtension<I, E> {

    private final ArrayList<String> modelNamePerVariant;
    private final ArrayList<String> parameterSetIdPerVariant;
    protected final List<ArrayList<String>> perVariantList = new ArrayList<>();

    public AbstractDynawoModel(I extendable, String modelName, String parameterSetId) {
        super(extendable);
        this.modelNamePerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.modelNamePerVariant.set(getVariantIndex(), modelName);
        this.parameterSetIdPerVariant = new ArrayList<>(Collections.nCopies(
                getVariantManagerHolder().getVariantManager().getVariantArraySize(), null));
        this.parameterSetIdPerVariant.set(getVariantIndex(), parameterSetId);
        perVariantList.add(modelNamePerVariant);
        perVariantList.add(parameterSetIdPerVariant);
    }

    @Override
    public String getModelName() {
        return modelNamePerVariant.get(getVariantIndex());
    }

    @Override
    public E setModelName(String modelName) {
        setAttribute(modelNamePerVariant, modelName, "modelName");
        return self();
    }

    @Override
    public String getParameterSetId() {
        return parameterSetIdPerVariant.get(getVariantIndex());
    }

    @Override
    public E setParameterSetId(String parameterSetId) {
        setAttribute(parameterSetIdPerVariant, parameterSetId, "parameterSetId");
        return self();
    }

    protected void setAttribute(ArrayList<String> variant, String currentValue, String attributeName) {
        int variantIndex = getVariantIndex();
        String oldValue = variant.get(variantIndex);
        if (!Objects.equals(oldValue, currentValue)) {
            variant.set(variantIndex, currentValue);
            NetworkImpl network = (NetworkImpl) getExtendable().getNetwork();
            String variantId = getVariantManagerHolder().getVariantManager().getWorkingVariantId();
            network.getListeners().notifyExtensionUpdate(this, attributeName, variantId, oldValue, currentValue);
        }
    }

    protected abstract E self();

    @Override
    public void extendVariantArraySize(int initVariantArraySize, int number, int sourceIndex) {
        perVariantList.forEach(list -> list.ensureCapacity(list.size() + number));
        for (int i = 0; i < number; ++i) {
            perVariantList.forEach(list -> list.add(list.get(sourceIndex)));
        }
    }

    @Override
    public void reduceVariantArraySize(int number) {
        for (int i = 0; i < number; i++) {
            perVariantList.forEach(list -> list.remove(list.size() - 1));  // remove elements from the top to avoid moves inside the array
        }
    }

    @Override
    public void deleteVariantArrayElement(int index) {
        perVariantList.forEach(list -> list.set(index, null));
    }

    @Override
    public void allocateVariantArrayElement(int[] indexes, int sourceIndex) {
        perVariantList.forEach(list -> {
            String sourceValue = list.get(sourceIndex);
            for (int index : indexes) {
                list.set(index, sourceValue);
            }
        });
    }
}
