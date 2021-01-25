/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.ieee;

import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.transform.Source;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import com.powsybl.computation.local.LocalCommandExecutor;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractDynaWaltzLocalCommandExecutor implements LocalCommandExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDynaWaltzLocalCommandExecutor.class);

    protected final FileSystem fileSystem;
    protected final Network network;
    protected final DynaWaltzParameters dynaWaltzParameters;

    public AbstractDynaWaltzLocalCommandExecutor(FileSystem fileSystem, Network network, DynaWaltzParameters dynaWaltzParameters) {
        this.fileSystem = Objects.requireNonNull(fileSystem);
        this.network = Objects.requireNonNull(network);
        this.dynaWaltzParameters = Objects.requireNonNull(dynaWaltzParameters);
    }

    protected abstract void validateInputs(Path workingDir) throws IOException;

    protected abstract void copyOutputs(Path workingDir) throws IOException;

    @Override
    public int execute(String program, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) throws IOException {
        return execute(program, -1, args, outFile, errFile, workingDir, env);
    }

    @Override
    public int execute(String program, long timeoutSeconds, List<String> args, Path outFile, Path errFile, Path workingDir, Map<String, String> env) throws IOException {
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
            System.err.println(myDiff.toString());
        }
        assertFalse(hasDiff);
    }
}
