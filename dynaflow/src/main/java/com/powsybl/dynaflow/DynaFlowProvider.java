/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.powsybl.computation.*;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.IidmXmlVersion;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowProvider;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.LoadFlowResultImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynaflow.DynaFlowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaFlowConstants.IIDM_FILENAME;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
@AutoService(LoadFlowProvider.class)
public class DynaFlowProvider implements LoadFlowProvider {

    private static final String WORKING_DIR_PREFIX = "dynaflow_";
    private final ExecutionEnvironment env;
    private final Command versionCmd;
    private final DynaFlowConfig config;

    public DynaFlowProvider() {
        this(DynaFlowConfig.fromPropertyFile());
    }

    public DynaFlowProvider(DynaFlowConfig config) {
        this.config = Objects.requireNonNull(config, "Config is null");
        this.env = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        this.versionCmd = getVersionCommand();
    }

    private static void writeIIDM(Path workingDir, Network network) {
        Properties params = new Properties();
        params.setProperty(XMLExporter.VERSION, IidmXmlVersion.V_1_0.toString("."));
        Exporters.export("XIIDM", network, params, workingDir.resolve(IIDM_FILENAME));
    }

    private String getProgram() {
        return config.getHomeDir().resolve("dynaflow-launcher.sh").toString();
    }

    public Command getCommand() {
        List<String> args = Arrays.asList("--iidm", IIDM_FILENAME, "--config", CONFIG_FILENAME);

        return new SimpleCommandBuilder()
                .id("dynaflow_lf")
                .program(getProgram())
                .args(args)
                .build();
    }

    public Command getVersionCommand() {
        List<String> args = Collections.singletonList("--version");
        return new SimpleCommandBuilder()
                .id("dynaflow_version")
                .program(getProgram())
                .args(args)
                .build();
    }

    private static DynaFlowParameters getParametersExt(LoadFlowParameters parameters) {
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        if (parametersExt == null) {
            parametersExt = new DynaFlowParameters();
        }
        return parametersExt;
    }

    @Override
    public String getName() {
        return "DynaFlow";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    private CommandExecution createCommandExecution() {
        Command cmd = getCommand();
        return new CommandExecution(cmd, 1, 0);
    }

    @Override
    public CompletableFuture<LoadFlowResult> run(Network network, ComputationManager computationManager, String workingStateId, LoadFlowParameters parameters) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(computationManager);
        Objects.requireNonNull(workingStateId);
        Objects.requireNonNull(parameters);
        DynaFlowParameters dynaFlowParameters = getParametersExt(parameters);
        DynaFlowUtil.checkDynaFlowVersion(env, computationManager, versionCmd);
        return computationManager.execute(env, new AbstractExecutionHandler<LoadFlowResult>() {
            @Override
            public List<CommandExecution> before(Path workingDir) throws IOException {
                network.getVariantManager().setWorkingVariant(workingStateId);

                writeIIDM(workingDir, network);
                DynaFlowConfigSerializer.serialize(parameters, dynaFlowParameters, workingDir, workingDir.resolve(CONFIG_FILENAME));
                return Collections.singletonList(createCommandExecution());
            }

            @Override
            public LoadFlowResult after(Path workingDir, ExecutionReport report) throws IOException {
                Path absoluteWorkingDir = workingDir.toAbsolutePath();
                super.after(absoluteWorkingDir, report);
                network.getVariantManager().setWorkingVariant(workingStateId);

                Map<String, String> metrics = new HashMap<>();
                String logs = null;
                return new LoadFlowResultImpl(true, metrics, logs);
            }
        });
    }
}
