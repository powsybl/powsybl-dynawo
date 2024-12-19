/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl.ieee;

import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.dynawo.DynawoSimulationParameters;
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
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.powsybl.dynawo.DynawoSimulationConstants.*;
import static com.powsybl.dynawo.commons.DynawoConstants.NETWORK_FILENAME;
import static com.powsybl.dynawo.commons.DynawoConstants.OUTPUTS_FOLDER;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynawoLocalCommandExecutor implements LocalCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynawoLocalCommandExecutor.class);
    private final FileSystem fileSystem;
    private final String networkId;
    private final DynawoSimulationParameters dynawoSimulationParameters;
    private final String baseDirName;
    private final String stdOutFileRef;

    public DynawoLocalCommandExecutor(FileSystem fileSystem, String networkId, DynawoSimulationParameters dynawoSimulationParameters, String baseDir, String stdOutFileRef) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.networkId = Objects.requireNonNull(networkId);
        this.dynawoSimulationParameters = Objects.requireNonNull(dynawoSimulationParameters);
        this.baseDirName = baseDir;
        this.stdOutFileRef = stdOutFileRef;
    }

    protected void validateInputs(Path workingDir) throws IOException {
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawo-inputs/powsybl_dynawo.xiidm"), Files.newInputStream(workingDir.resolve(NETWORK_FILENAME)));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawo-inputs/powsybl_dynawo.jobs"), Files.newInputStream(workingDir.resolve(JOBS_FILENAME)));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawo-inputs/powsybl_dynawo.dyd"), Files.newInputStream(workingDir.resolve(DYD_FILENAME)));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawo-inputs/models.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(DynawoSimulationParameters.MODELS_OUTPUT_PARAMETERS_FILE).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawo-inputs/network.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(DynawoSimulationParameters.NETWORK_OUTPUT_PARAMETERS_FILE).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawo-inputs/solvers.par"), Files.newInputStream(workingDir.resolve(fileSystem.getPath(DynawoSimulationParameters.SOLVER_OUTPUT_PARAMETERS_FILE).getFileName().toString())));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawo-inputs/" + networkId + ".par"), Files.newInputStream(workingDir.resolve(networkId + ".par")));
        compareXml(getClass().getResourceAsStream("/" + baseDirName + "/dynawo-inputs/powsybl_dynawo.crv"), Files.newInputStream(workingDir.resolve(CRV_FILENAME)));
    }

    protected void copyOutputs(Path workingDir) throws IOException {
        Path output = Files.createDirectories(workingDir.resolve(OUTPUTS_FOLDER).resolve(CURVES_OUTPUT_PATH).toAbsolutePath());
        Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/" + baseDirName + "/dynawo-outputs/curves.csv")), output.resolve(CURVES_FILENAME));
    }

    @Override
    public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
        return execute(program, -1, args, outFile, errFile, workingDir, env);
    }

    @Override
    public int execute(String program, long timeoutSeconds, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) {
        try {
            if (args.get(0).equals("version")) {
                copyFile(stdOutFileRef, outFile);
            } else {
                validateInputs(workingDir);
                copyOutputs(workingDir);
            }
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

    protected void copyFile(String source, Path target) throws IOException {
        try (InputStream is = Objects.requireNonNull(getClass().getResourceAsStream(source))) {
            Files.copy(is, target, StandardCopyOption.REPLACE_EXISTING);
        }
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
