/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.it;

import com.powsybl.commons.test.ComparisonUtils;
import com.powsybl.computation.ComputationManager;
import com.powsybl.dynawo.margincalculation.tool.MarginCalculationTool;
import com.powsybl.tools.CommandLineTools;
import com.powsybl.tools.ToolInitializationContext;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class IToolsTest extends AbstractDynawoTest {

    private static CommandLineTools TOOLS;
    private ToolInitializationContext toolContext;
    private ByteArrayOutputStream bout;
    private ByteArrayOutputStream berr;

    @BeforeAll
    static void beforeAll() {
        TOOLS = new CommandLineTools(List.of(new MarginCalculationTool()));
    }

    @BeforeEach
    void setUp() throws Exception {
        super.setUp();
        bout = new ByteArrayOutputStream();
        berr = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(bout);
        PrintStream err = new PrintStream(berr);
        toolContext = new ToolInitializationContext() {

            @Override
            public PrintStream getOutputStream() {
                return out;
            }

            @Override
            public PrintStream getErrorStream() {
                return err;
            }

            @Override
            public FileSystem getFileSystem() {
                return FileSystems.getDefault();
            }

            @Override
            public Options getAdditionalOptions() {
                return new Options();
            }

            @Override
            public ComputationManager createShortTimeExecutionComputationManager(CommandLine commandLine) {
                return computationManager;
            }

            @Override
            public ComputationManager createLongTimeExecutionComputationManager(CommandLine commandLine) {
                return computationManager;
            }
        };
    }

    @Test
    void testIeee14Itools() {

        String[] args = {"margin-calculation", "--case-file", getResource("/ieee14/IEEE14.iidm"),
            "--dynamic-models-file", getResource("/ieee14/dynamicModels.groovy"),
            "--contingencies-file", getResource("/ieee14/contingencies.groovy"),
            "--load-variations-file", getResource("/ieee14/loadsVariations.json")};
        int status = TOOLS.run(args, toolContext);

        assertEquals(CommandLineTools.COMMAND_OK_STATUS, status);
        ComparisonUtils.assertTxtEquals(getResourceAsStream("/itools/mc_out.txt"), getOutputString());
        assertThat(getErrorString()).isEmpty();
    }

    private String getOutputString() {
        return bout.toString(StandardCharsets.UTF_8);
    }

    private String getErrorString() {
        return berr.toString(StandardCharsets.UTF_8);
    }
}
