/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

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

    private final Map<String, BlackBoxModel> dynamicModelMap;
    private final Map<String, BlackBoxModel> pureDynamicModelMap;

    public static BlackBoxModelSupplier createFrom(List<BlackBoxModel> dynamicModels) {
        Map<Boolean, Map<String, BlackBoxModel>> maps = createDynamicModelMaps(dynamicModels);
        return new BlackBoxModelSupplier(maps.get(true), maps.get(false));
    }

    public static BlackBoxModelSupplier createFrom(BlackBoxModelSupplier bbmSupplier, List<BlackBoxModel> dynamicModels) {
        Map<Boolean, Map<String, BlackBoxModel>> newMaps = createDynamicModelMaps(dynamicModels);
        newMaps.get(true).putAll(bbmSupplier.dynamicModelMap);
        newMaps.get(false).putAll(bbmSupplier.pureDynamicModelMap);
        return new BlackBoxModelSupplier(newMaps.get(true), newMaps.get(false));
    }

    private static Map<Boolean, Map<String, BlackBoxModel>> createDynamicModelMaps(List<BlackBoxModel> dynamicModels) {
        return dynamicModels.stream()
                .collect(Collectors.partitioningBy(EquipmentBlackBoxModel.class::isInstance,
                        Collectors.toMap(BlackBoxModel::getDynamicModelId, Function.identity())));
    }

    private BlackBoxModelSupplier(Map<String, BlackBoxModel> dynamicModelMap, Map<String, BlackBoxModel> pureDynamicModelMap) {
        this.dynamicModelMap = dynamicModelMap;
        this.pureDynamicModelMap = pureDynamicModelMap;
    }

    public BlackBoxModel getEquipmentDynamicModel(String id) {
        return dynamicModelMap.get(id);
    }

    public BlackBoxModel getPureDynamicModel(String id) {
        return pureDynamicModelMap.get(id);
    }

    public boolean hasDynamicModel(Identifiable<?> equipment) {
        return dynamicModelMap.containsKey(equipment.getId());
    }

    public boolean hasDynamicModel(String id) {
        return dynamicModelMap.containsKey(id) || pureDynamicModelMap.containsKey(id);
    }

    public boolean dynamicModelIsConnected(String id) {
        BlackBoxModel model =
                pureDynamicModelMap.getOrDefault(id, dynamicModelMap.get(id));
        return model != null && model.isConnected();
    }

}
