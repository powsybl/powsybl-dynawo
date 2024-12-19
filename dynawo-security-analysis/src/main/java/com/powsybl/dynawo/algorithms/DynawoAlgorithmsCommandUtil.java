/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.algorithms;

import com.powsybl.computation.Command;
import com.powsybl.computation.SimpleCommandBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynawo.algorithms.xml.AlgorithmsConstants.MULTIPLE_JOBS_FILENAME;
import static com.powsybl.dynawo.commons.DynawoConstants.AGGREGATED_RESULTS;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynawoAlgorithmsCommandUtil {

    private DynawoAlgorithmsCommandUtil() {
    }

    public static Command getCommand(DynawoAlgorithmsConfig config, String mode, String id) {
        List<String> args = Arrays.asList(
                mode,
                "--input", MULTIPLE_JOBS_FILENAME,
                "--output", AGGREGATED_RESULTS);
        return new SimpleCommandBuilder()
                .id(id)
                .program(config.getProgram())
                .args(args)
                .build();
    }

    public static Command getVersionCommand(DynawoAlgorithmsConfig config) {
        List<String> args = Collections.singletonList("--version");
        return new SimpleCommandBuilder()
                .id("dynawo_version")
                .program(config.getProgram())
                .args(args)
                .build();
    }
}
