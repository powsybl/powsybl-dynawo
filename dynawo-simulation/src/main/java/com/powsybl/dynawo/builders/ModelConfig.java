/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public record ModelConfig(String lib, String alias, String internalModelPrefix, List<String> properties, String doc, VersionBound version) implements ModelInfo {

    private static final String CONTROLLABLE_PROPERTY = "CONTROLLABLE";
    private static final String DANGLING_PROPERTY = "DANGLING";
    private static final String SYNCHRONIZED_PROPERTY = "SYNCHRONIZED";
    private static final String TRANSFORMER_PROPERTY = "TRANSFORMER";
    private static final String AUXILIARY_PROPERTY = "AUXILIARY";

    public ModelConfig(String lib, String alias, String internalModelPrefix, List<String> properties, String doc, VersionBound version) {
        this.lib = Objects.requireNonNull(lib);
        this.alias = alias;
        this.internalModelPrefix = internalModelPrefix;
        this.properties = Objects.requireNonNull(properties);
        this.doc = doc;
        this.version = Objects.requireNonNull(version);
    }

    public ModelConfig(String lib, List<String> properties) {
        this(lib, null, null, properties, null, VersionBound.createDefaultVersion());
    }

    public ModelConfig(String lib) {
        this(lib, null, null, Collections.emptyList(), null, VersionBound.createDefaultVersion());
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

    @Override
    public String name() {
        return alias == null ? lib : alias;
    }

    @Override
    public String formattedInfo() {
        return name() + (alias != null ? " (" + lib + ")" : "")
                + (doc != null ? ": " + doc : "")
                + " (" + version.formattedInfo() + ")";
    }
}
