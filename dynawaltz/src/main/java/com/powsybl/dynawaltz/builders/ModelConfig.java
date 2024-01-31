/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record ModelConfig(String lib, String alias, String internalModelPrefix, List<String> properties, boolean isDefaultLib) {

    private static final String CONTROLLABLE_PROPERTY = "CONTROLLABLE";
    private static final String DANGLING_PROPERTY = "DANGLING";
    private static final String SYNCHRONIZED_PROPERTY = "SYNCHRONIZED";
    private static final String TRANSFORMER_PROPERTY = "TRANSFORMER";
    private static final String AUXILIARY_PROPERTY = "AUXILIARY";

    public static ModelConfig copyOf(ModelConfig baseModelConfig, boolean isDefaultLib) {
        return new ModelConfig(baseModelConfig.lib, baseModelConfig.alias, baseModelConfig.internalModelPrefix, baseModelConfig.properties, isDefaultLib);
    }

    public ModelConfig(String lib, String alias, String internalModelPrefix, List<String> properties, boolean isDefaultLib) {
        this.lib = Objects.requireNonNull(lib);
        this.alias = alias;
        this.internalModelPrefix = internalModelPrefix;
        this.properties = Objects.requireNonNull(properties);
        this.isDefaultLib = isDefaultLib;
    }

    public boolean isControllable() {
        return properties.contains(CONTROLLABLE_PROPERTY);
    }

    public boolean isDangling() {
        return properties.contains(DANGLING_PROPERTY);
    }

    public boolean isSynchronized() {
        return properties.contains(SYNCHRONIZED_PROPERTY);
    }

    public boolean hasTransformer() {
        return properties.contains(TRANSFORMER_PROPERTY);
    }

    public boolean hasAuxiliary() {
        return properties.contains(AUXILIARY_PROPERTY);
    }

    public boolean hasProperty(String property) {
        return properties.contains(property);
    }

    public String name() {
        return alias == null ? lib : alias;
    }
}
