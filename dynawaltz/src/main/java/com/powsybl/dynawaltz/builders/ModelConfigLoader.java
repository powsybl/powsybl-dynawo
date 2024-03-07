/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.powsybl.commons.PowsyblException;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface ModelConfigLoader {

    String getModelConfigFileName();

    default Map<String, ModelConfigs> loadModelConfigs() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(Map.class, new ModelConfigsJsonDeserializer());
            objectMapper.registerModule(module);
            return objectMapper.readValue(Objects.requireNonNull(
                            ModelConfigLoader.class.getClassLoader().getResource(getModelConfigFileName())).openStream(),
                    new TypeReference<>() {
                    });
        } catch (IOException e) {
            throw new PowsyblException("Dynamic models configuration file not found");
        }
    }

    Stream<BuilderConfig> loadBuilderConfigs();
}
