/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.powsybl.computation.*;
import com.powsybl.dynaflow.json.DynaflowConfigSerializer;
import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.util.Networks;
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

import static com.powsybl.dynaflow.DynaflowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaflowConstants.IIDM_FILENAME;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
@AutoService(LoadFlowProvider.class)
public class DynaflowProvider implements LoadFlowProvider {

    private static final String WORKING_DIR_PREFIX = "dynaflow_";
    private final ExecutionEnvironment env;
    private final Command versionCmd;
    private final DynaflowConfig config;

    public DynaflowProvider() {
        this(DynaflowConfig.fromPropertyFile());
    }

    public DynaflowProvider(DynaflowConfig config) {
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

    public Command getCommand(Path workingDir) {
        String iidmPath = workingDir.resolve(IIDM_FILENAME).toString();
        String configPath = workingDir.resolve(CONFIG_FILENAME).toString();
        List<String> args = Arrays.asList("--iidm", iidmPath, "--config", configPath);

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

    private static DynaflowParameters getParametersExt(LoadFlowParameters parameters) {
        DynaflowParameters parametersExt = parameters.getExtension(DynaflowParameters.class);
        if (parametersExt == null) {
            parametersExt = new DynaflowParameters();
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

    private CommandExecution createCommandExecution(Network network, Path workingDir) {
        Command cmd = getCommand(workingDir);
        return new CommandExecution(cmd, 1, 0, Networks.getExecutionTags(network));
    }

    @Override
    public CompletableFuture<LoadFlowResult> run(Network network, ComputationManager computationManager, String workingStateId, LoadFlowParameters parameters) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(computationManager);
        Objects.requireNonNull(workingStateId);
        Objects.requireNonNull(parameters);
        DynaflowParameters dynaflowParameters = getParametersExt(parameters);
        DynaflowUtil.checkDynaflowVersion(env, computationManager, versionCmd);
        return computationManager.execute(env, new AbstractExecutionHandler<LoadFlowResult>() {
            @Override
            public List<CommandExecution> before(Path workingDir) throws IOException {
                network.getVariantManager().setWorkingVariant(workingStateId);

                writeIIDM(workingDir, network);
                DynaflowConfigSerializer.serialize(parameters, dynaflowParameters, workingDir.resolve(CONFIG_FILENAME));
                return Collections.singletonList(createCommandExecution(network, workingDir));
            }

            @Override
            public LoadFlowResult after(Path workingDir, ExecutionReport report) throws IOException {
                super.after(workingDir, report);
                network.getVariantManager().setWorkingVariant(workingStateId);

                Map<String, String> metrics = new HashMap<>();
                String logs = null;
                return new LoadFlowResultImpl(true, metrics, logs);
            }
        });
    }
}
