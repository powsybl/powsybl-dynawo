/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.ieee;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.transform.Source;

import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.dynawo.DynawoParameters;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractDynawoLocalCommandExecutor implements LocalCommandExecutor {

    protected final FileSystem fileSystem;
    protected final Network network;
    protected final DynawoParameters dynawoParameters;

    public AbstractDynawoLocalCommandExecutor(FileSystem fileSystem, Network network, DynawoParameters dynawoParameters) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.network = Objects.requireNonNull(network);
        this.dynawoParameters = Objects.requireNonNull(dynawoParameters);
    }

    protected abstract void validateInputs(Path workingDir) throws IOException;

    protected abstract void copyOutputs(Path workingDir) throws IOException;

    @Override
    public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) throws IOException {
        return execute(program, -1, args, outFile, errFile, workingDir, env);
    }

    @Override
    public int execute(String program, long timeoutSeconds, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) throws IOException {
        validateInputs(workingDir);
        copyOutputs(workingDir);
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
            System.err.println(myDiff.toString());
        }
        assertFalse(hasDiff);
    }
}
