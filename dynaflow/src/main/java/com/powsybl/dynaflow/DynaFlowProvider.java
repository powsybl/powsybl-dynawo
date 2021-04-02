/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.google.common.base.Charsets;
import com.powsybl.computation.*;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynawo.commons.DynawoResultsNetworkUpdate;
import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.IidmXmlVersion;
import com.powsybl.iidm.xml.NetworkXml;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowProvider;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.LoadFlowResultImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        params.setProperty(XMLExporter.VERSION, IidmXmlVersion.V_1_2.toString("."));
        Exporters.export("XIIDM", network, params, workingDir.resolve(IIDM_FILENAME));
    }

    private String getProgram() {
        return config.getHomeDir().resolve("dynaflow-launcher.sh").toString();
    }

    public Command getCommand() {
        List<String> args = Arrays.asList("--network", IIDM_FILENAME, "--config", CONFIG_FILENAME);

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
        DynaFlowContext context = new DynaFlowContext(network, parameters, dynaFlowParameters, workingStateId);
        return computationManager.execute(env, new DynaFlowHandler(context));
    }

    private final class DynaFlowHandler extends AbstractExecutionHandler<LoadFlowResult> {

        private final DynaFlowContext context;

        public DynaFlowHandler(DynaFlowContext context) {
            this.context = context;
        }

        @Override
        public List<CommandExecution> before(Path workingDir) throws IOException {
            context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingStateId());
            writeIIDM(workingDir, context.getNetwork());
            DynaFlowConfigSerializer.serialize(context.getLoadFlowParameters(), context.getDynaFlowParameters(), workingDir, workingDir.resolve(CONFIG_FILENAME));
            return Collections.singletonList(createCommandExecution());
        }

        @Override
        public LoadFlowResult after(Path workingDir, ExecutionReport report) throws IOException {
            Path absoluteWorkingDir = workingDir.toAbsolutePath();
            super.after(absoluteWorkingDir, report);
            context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingStateId());
            DynawoResultsNetworkUpdate.update(context.getNetwork(), NetworkXml.read(workingDir.resolve("outputs").resolve("finalState").resolve("outputIIDM.xml")));

            Map<String, String> metrics = new HashMap<>();
            String logs = null;
            return new LoadFlowResultImpl(true, metrics, logs);
        }
    }

    private void peakPathToConsole(Path pathToPeak) {

        // Reading the folder and getting Stream.
        try (Stream<Path> walk = Files.walk(pathToPeak)) {

            // Filtering the paths by a regular file and adding into a list.
            List<Path> fileList = walk.filter(Files::isRegularFile)
                    .collect(Collectors.toList());

            fileList.forEach(file -> {
                try {
                    System.out.printf("%s (%db)%n", file, Files.readAllBytes(file).length);
                    for (String line : Files.readAllLines(file, Charsets.UTF_8)) {
                        System.out.println(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
