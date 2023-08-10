/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.google.common.collect.ImmutableMap;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class SimpleDynawoConfig {

    private final Path homeDir;
    private final boolean debug;

    public SimpleDynawoConfig(Path homeDir, boolean debug) {
        this.homeDir = Objects.requireNonNull(homeDir);
        this.debug = debug;
    }

    public SimpleDynawoConfig(String homeDir, boolean debug) {
        this(Path.of(homeDir), debug);
    }

    public Map<String, String> createEnv() {
        return ImmutableMap.<String, String>builder()
                .build();
    }

    public Path getHomeDir() {
        return homeDir;
    }

    public boolean isDebug() {
        return debug;
    }
}
