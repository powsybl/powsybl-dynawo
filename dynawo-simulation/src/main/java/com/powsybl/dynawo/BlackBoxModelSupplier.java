/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.dynawo.models.AbstractPureDynamicBlackBoxModel;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.EquipmentBlackBoxModel;
import com.powsybl.iidm.network.Identifiable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class BlackBoxModelSupplier {

    private final Map<String, EquipmentBlackBoxModel> staticIdBlackBoxModelMap;
    private final Map<String, BlackBoxModel> pureDynamicModelMap;

    public static BlackBoxModelSupplier createFrom(List<BlackBoxModel> dynamicModels) {
        return new BlackBoxModelSupplier(createStaticIdBlackBoxModelMap(dynamicModels),
                createPureDynamicModelMap(dynamicModels));
    }

    public static BlackBoxModelSupplier createFrom(BlackBoxModelSupplier bbmSupplier, List<BlackBoxModel> dynamicModels) {
        Map<String, EquipmentBlackBoxModel> newStaticIdBlackBoxModelMap = createStaticIdBlackBoxModelMap(dynamicModels);
        Map<String, BlackBoxModel> newPureDynamicModelMap = createPureDynamicModelMap(dynamicModels);
        newStaticIdBlackBoxModelMap.putAll(bbmSupplier.staticIdBlackBoxModelMap);
        newPureDynamicModelMap.putAll(bbmSupplier.pureDynamicModelMap);
        return new BlackBoxModelSupplier(newStaticIdBlackBoxModelMap, newPureDynamicModelMap);
    }

    private static Map<String, EquipmentBlackBoxModel> createStaticIdBlackBoxModelMap(List<BlackBoxModel> dynamicModels) {
        return dynamicModels.stream()
                .filter(EquipmentBlackBoxModel.class::isInstance)
                .map(EquipmentBlackBoxModel.class::cast)
                .collect(Collectors.toMap(EquipmentBlackBoxModel::getStaticId, Function.identity()));
    }

    private static Map<String, BlackBoxModel> createPureDynamicModelMap(List<BlackBoxModel> dynamicModels) {
        return dynamicModels.stream()
                .filter(AbstractPureDynamicBlackBoxModel.class::isInstance)
                .collect(Collectors.toMap(BlackBoxModel::getDynamicModelId, Function.identity()));
    }

    private BlackBoxModelSupplier(Map<String, EquipmentBlackBoxModel> staticIdBlackBoxModelMap, Map<String, BlackBoxModel> pureDynamicModelMap) {
        this.staticIdBlackBoxModelMap = staticIdBlackBoxModelMap;
        this.pureDynamicModelMap = pureDynamicModelMap;
    }

    public EquipmentBlackBoxModel getStaticIdBlackBoxModel(String id) {
        return staticIdBlackBoxModelMap.get(id);
    }

    public BlackBoxModel getPureDynamicModel(String id) {
        return pureDynamicModelMap.get(id);
    }

    public boolean hasDynamicModel(Identifiable<?> equipment) {
        return staticIdBlackBoxModelMap.containsKey(equipment.getId());
    }
}
