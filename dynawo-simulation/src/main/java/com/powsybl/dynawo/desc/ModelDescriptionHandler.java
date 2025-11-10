/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.desc;

import com.powsybl.dynawo.builders.ModelConfigsHandler;
import com.powsybl.dynawo.commons.DynawoConfig;
import com.powsybl.dynawo.commons.DynawoVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class ModelDescriptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelDescriptionHandler.class);
    private static final String DB_FOLDER = "ddb";
    private static final String DESC_FILE = "%s.desc.xml";

    private final Map<String, ModelDescription> modelDescriptions;

    //TODO memoize loading
    public static ModelDescriptionHandler loadFrom(DynawoConfig config, DynawoVersion dynawoVersion) {
        Map<String, ModelDescription> descriptions = new HashMap<>();
        Path dbPath = config.getHomeDir().resolve(DB_FOLDER);
        if (Files.exists(dbPath)) {
            for (String lib : ModelConfigsHandler.getInstance().getSupportedLibs(dynawoVersion)) {
                Path libPath = dbPath.resolve(String.format(DESC_FILE, lib));
                if (Files.exists(libPath)) {
                    FilteredDescriptionXml.load(libPath, md -> descriptions.put(md.name(), md));
                } else {
                    LOGGER.warn("Model {} desc file not found, model description cannot be loaded", lib);
                }
            }
        } else {
            LOGGER.warn("Dynawo ddb folder {} not found, model descriptions cannot be loaded", dbPath);
        }
        return new ModelDescriptionHandler(descriptions);
    }

    ModelDescriptionHandler(Map<String, ModelDescription> modelDescriptions) {
        this.modelDescriptions = modelDescriptions;
    }

    public Map<String, ModelDescription> getModelDescriptions() {
        return modelDescriptions;
    }
}
