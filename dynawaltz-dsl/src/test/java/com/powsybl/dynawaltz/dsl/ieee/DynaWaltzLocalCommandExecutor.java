/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl.ieee;

import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import javax.xml.transform.Source;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.*;
import static org.junit.Assert.assertFalse;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public class DynaWaltzLocalCommandExecutor implements LocalCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynaWaltzLocalCommandExecutor.class);
    private final FileSystem fileSystem;
    private final String networkId;
    private final DynaWaltzParameters dynaWaltzParameters;
    private final String baseDirName;
    private final String busSystem;

    public DynaWaltzLocalCommandExecutor(FileSystem fileSystem, String networkId, DynaWaltzParameters dynaWaltzParameters, String baseDir, String busSystem) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.networkId = Objects.requireNonNull(networkId);
        this.dynaWaltzParameters = Objects.requireNonNull(dynaWaltzParameters);
        this.baseDirName = baseDir;
        this.busSystem = busSystem;
    }

    protected void validateInputs(Path workingDir) throws IOException {
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawaltz-inputs/powsybl_dynawaltz.xiidm"), Files.newInputStream(workingDir.resolve(NETWORK_FILENAME)));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawaltz-inputs/powsybl_dynawaltz.jobs"), Files.newInputStream(workingDir.resolve(JOBS_FILENAME)));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawaltz-inputs/powsybl_dynawaltz.dyd"), Files.newInputStream(workingDir.resolve(DYD_FILENAME)));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawaltz-inputs/models.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(dynaWaltzParameters.getParametersFile()).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawaltz-inputs/network.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(dynaWaltzParameters.getNetwork().getParametersFile()).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawaltz-inputs/solvers.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(dynaWaltzParameters.getSolver().getParametersFile()).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawaltz-inputs/" + busSystem + "bus.par"), Files.newInputStream(workingDir.resolve(networkId + ".par")));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawaltz-inputs/powsybl_dynawaltz.crv"), Files.newInputStream(workingDir.resolve(CRV_FILENAME)));
    }

    protected void copyOutputs(Path workingDir) throws IOException {
        Path output = Files.createDirectories(workingDir.resolve("outputs/curves").toAbsolutePath());
        Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/" + baseDirName + "/dynawaltz-outputs/curves.csv")), output.resolve("curves.csv"));
    }

    @Override
    public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
        return execute(program, -1, args, outFile, errFile, workingDir, env);
    }

    @Override
    public int execute(String program, long timeoutSeconds, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
        try {
            validateInputs(workingDir);
            copyOutputs(workingDir);
        } catch (Throwable throwable) {
            LOGGER.error(throwable.toString(), throwable);
            return -1;
        }
        return 0;
    }

    @Override
    public void stop(Path workingDir) {
    }

    @Override
    public void stopForcibly(Path workingDir) {
    }

    protected static void compareXml(InputStream expected, InputStream actual) {
        Source control = Input.fromStream(expected).build();
        Source test = Input.fromStream(actual).build();
        Diff myDiff = DiffBuilder.compare(control).withTest(test).ignoreWhitespace().ignoreComments().build();
        boolean hasDiff = myDiff.hasDifferences();
        if (hasDiff) {
            System.err.println(myDiff);
        }
        assertFalse(hasDiff);
    }
}
