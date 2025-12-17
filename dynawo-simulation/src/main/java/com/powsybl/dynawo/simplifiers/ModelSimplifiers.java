/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.simplifiers;

import com.google.common.base.Suppliers;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ModelSimplifiers {

    private static final Supplier<List<ModelsRemovalSimplifier>> MODELS_REMOVAL_SUPPLIER =
            Suppliers.memoize(() -> ServiceLoader.load(ModelsRemovalSimplifier.class).stream()
                    .map(ServiceLoader.Provider::get)
                    .collect(Collectors.toList()));

    private static final Supplier<List<ModelsSubstitutionSimplifier>> MODELS_SUBSTITUTION_SUPPLIER =
            Suppliers.memoize(() -> ServiceLoader.load(ModelsSubstitutionSimplifier.class).stream()
                    .map(ServiceLoader.Provider::get)
                    .collect(Collectors.toList()));

    private final List<ModelsRemovalSimplifier> modelsRemovalSimplifiers;
    private final List<ModelsSubstitutionSimplifier> modelsSubstitutionSimplifiers;
    private final List<ModelSimplifierInfo> simplifierInfos;

    public ModelSimplifiers() {
        this.modelsRemovalSimplifiers = MODELS_REMOVAL_SUPPLIER.get();
        this.modelsSubstitutionSimplifiers = MODELS_SUBSTITUTION_SUPPLIER.get();
        this.simplifierInfos = Stream.concat(modelsRemovalSimplifiers.stream(), modelsSubstitutionSimplifiers.stream())
                .map(ModelSimplifier::getSimplifierInfo)
                .toList();
    }

    public List<ModelsRemovalSimplifier> getModelsRemovalSimplifiers() {
        return modelsRemovalSimplifiers;
    }

    public List<ModelsSubstitutionSimplifier> getModelsSubstitutionSimplifiers() {
        return modelsSubstitutionSimplifiers;
    }

    public List<String> getModelSimplifierNames() {
        return simplifierInfos.stream()
                .map(ModelSimplifierInfo::name)
                .toList();
    }

    public List<ModelSimplifierInfo> getModelSimplifierInfos() {
        return simplifierInfos;
    }
}
