/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.common.io.CharStreams;
import com.powsybl.commons.PowsyblException;
import com.powsybl.computation.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import com.powsybl.dynaflow.DynaFlowConstants.DynaFlowVersion;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
final class DynaFlowUtil {

    public static boolean checkDynaFlowVersion(ExecutionEnvironment env, ComputationManager computationManager, Command versionCmd) {
        return computationManager.execute(env, new AbstractExecutionHandler<Boolean>() {
            @Override
            public List<CommandExecution> before(Path path) {
                return Collections.singletonList(new CommandExecution(versionCmd, 1));
            }

            @Override
            public Boolean after(Path workingDir, ExecutionReport report) throws IOException {
                super.after(workingDir, report);
                Optional<InputStream> stdErr = report.getStdErr(versionCmd, 0);
                if (!stdErr.isPresent()) {
                    throw new PowsyblException("No output for DynaFlow version command");
                }
                try (Reader reader = new InputStreamReader(stdErr.get())) {
                    String stdErrContent = CharStreams.toString(reader);
                    return DynaFlowUtil.versionIsInRange(versionSanitizer(stdErrContent), DynaFlowConstants.VERSION_MIN, DynaFlowConstants.VERSION);
                }
            }
        }).join();
    }

    private static String versionSanitizer(String version) {
        return Arrays.stream(version.split(" ")).findFirst().get();
    }

    public static boolean versionRespectsMin(String version, DynaFlowVersion minDynaFlowVersion) {
        return DynaFlowVersion.of(version).map(v -> v.compareTo(minDynaFlowVersion) >= 0).orElse(false);
    }

    public static boolean versionRespectsMax(String version, DynaFlowVersion maxDynaFlowVersion) {
        return DynaFlowVersion.of(version).map(v -> v.compareTo(maxDynaFlowVersion) <= 0).orElse(false);
    }

    public static boolean versionIsInRange(String version, DynaFlowVersion minDynaFlowVersion, DynaFlowVersion maxDynaFlowVersion) {
        return versionRespectsMin(version, minDynaFlowVersion)
                && versionRespectsMax(version, maxDynaFlowVersion);
    }

    private DynaFlowUtil() {
    }
}
