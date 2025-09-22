/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.buses.StandardBusBuilder;
import com.powsybl.dynawo.models.lines.LineBuilder;
import com.powsybl.dynawo.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.iidm.network.Network;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynamicModelsConfigUtils {

    private static final String BUS_PARAM_SET_ID = "bus";

    private DynamicModelsConfigUtils() {
    }

    /**
     * Add mandatory dynamic models if not already defined
     */
    public static void mandatoryModelsAdder(Network network, List<BlackBoxModel> models) {
        Set<String> existingModels = models.stream()
                .map(BlackBoxModel::getDynamicModelId)
                .collect(Collectors.toSet());
        network.getLines().forEach(l -> addLine(network, models, existingModels, l.getId()));
        network.getTwoWindingsTransformers().forEach(tf -> addTransformer(network, models, existingModels, tf.getId()));
        network.getBusBreakerView().getBuses().forEach(b -> addBus(network, models, existingModels, b.getId()));
    }

    private static void addBus(Network network, List<BlackBoxModel> models, Set<String> existingModels, String staticId) {
        if (!existingModels.contains(staticId)) {
            models.add(StandardBusBuilder.of(network)
                    .staticId(staticId)
                    .parameterSetId(BUS_PARAM_SET_ID)
                    .build());
        }
    }

    private static void addLine(Network network, List<BlackBoxModel> models, Set<String> existingModels, String staticId) {
        if (!existingModels.contains(staticId)) {
            models.add(LineBuilder.of(network)
                    .staticId(staticId)
                    .parameterSetId("line")
                    .build());
        }
    }

    private static void addTransformer(Network network, List<BlackBoxModel> models, Set<String> existingModels, String staticId) {
        if (!existingModels.contains(staticId)) {
            models.add(TransformerFixedRatioBuilder.of(network)
                    .staticId(staticId)
                    .parameterSetId("tf")
                    .build());
        }
    }
}
