/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.google.common.io.CharStreams;
import com.powsybl.commons.PowsyblException;
import com.powsybl.computation.*;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.XMLExporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.util.*;

import static com.powsybl.dynawo.commons.DynawoConstants.IIDM_EXTENSIONS;
import static com.powsybl.dynawo.commons.DynawoConstants.IIDM_VERSION;

/**
 * @author Geoffroy Jamgotchian {@literal <geoffroy.jamgotchian at rte-france.com>}
 */
public final class DynawoUtil {

    private DynawoUtil() {
    }

    public static void writeIidm(Network network, Path file) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(file);
        Properties params = new Properties();
        params.setProperty(XMLExporter.VERSION, IIDM_VERSION);
        params.setProperty(XMLExporter.EXTENSIONS_LIST, String.join(",", IIDM_EXTENSIONS));
        network.write("XIIDM", params, file);
    }

    public static void requireDynaMinVersion(ExecutionEnvironment env, ComputationManager computationManager, Command versionCmd,
                                             String dynaName, boolean fromErr) {
        if (!checkDynawoVersion(env, computationManager, versionCmd, fromErr)) {
            throw new PowsyblException(dynaName + " version not supported. Must be >= " + DynawoConstants.VERSION_MIN);
        }
    }

    public static boolean checkDynawoVersion(ExecutionEnvironment env, ComputationManager computationManager, Command versionCmd, boolean fromErr) {
        return computationManager.execute(env, new AbstractExecutionHandler<Boolean>() {
            @Override
            public List<CommandExecution> before(Path path) {
                return Collections.singletonList(new CommandExecution(versionCmd, 1));
            }

            @Override
            public Boolean after(Path workingDir, ExecutionReport report) throws IOException {
                super.after(workingDir, report);
                Optional<InputStream> std = fromErr ? report.getStdErr(versionCmd, 0) : report.getStdOut(versionCmd, 0);
                if (std.isEmpty()) {
                    throw new PowsyblException("No output for DynaFlow version command");
                }
                try (Reader reader = new InputStreamReader(std.get())) {
                    String stdErrContent = CharStreams.toString(reader);
                    DynawoVersion version = DynawoVersion.createFromString(versionSanitizer(stdErrContent));
                    return DynawoConstants.VERSION_MIN.compareTo(version) < 1;
                }
            }
        }).join();
    }

    private static String versionSanitizer(String version) {
        return version.split(" ")[0];
    }
}
