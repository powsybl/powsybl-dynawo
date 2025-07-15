/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.powsybl.commons.PowsyblException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Loads additional models from a json file path
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class AdditionalModelConfigLoader {

    private final Path modelConfigPath;

    public AdditionalModelConfigLoader(Path modelConfigPath) {
        this.modelConfigPath = modelConfigPath;
    }

    public Map<String, ModelConfigs> loadModelConfigs() {
        try {
            ObjectMapper objectMapper = ModelConfigLoader.getModelConfigObjectMapper();
            return objectMapper.readValue(Files.newBufferedReader(modelConfigPath), new TypeReference<>() {
                });
        } catch (IOException e) {
            throw new PowsyblException("Additional dynamic models configuration file not found");
        }
    }
}
