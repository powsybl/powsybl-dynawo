/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.google.auto.service.AutoService;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
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
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.powsybl.dynaflow.DynaFlowConstants.*;

/**
 *
 * @author Guillaume Pernin <guillaume.pernin at rte-france.com>
 */
@AutoService(LoadFlowProvider.class)
public class DynaFlowProvider implements LoadFlowProvider {

    private static final String WORKING_DIR_PREFIX = "dynaflow_";

    private final Supplier<DynaFlowConfig> configSupplier;

    public DynaFlowProvider() {
        this(DynaFlowConfig::fromPropertyFile);
    }

    public DynaFlowProvider(Supplier<DynaFlowConfig> configSupplier) {
        this.configSupplier = Suppliers.memoize(Objects.requireNonNull(configSupplier, "Config supplier is null"));
    }

    private static void writeIIDM(Path workingDir, Network network) {
        Properties params = new Properties();
        params.setProperty(XMLExporter.VERSION, IidmXmlVersion.V_1_2.toString("."));
        Exporters.export("XIIDM", network, params, workingDir.resolve(IIDM_FILENAME));
    }

    private static String getProgram(DynaFlowConfig config) {
        return config.getHomeDir().resolve("dynaflow-launcher.sh").toString();
    }

    public static Command getCommand(DynaFlowConfig config) {
        List<String> args = Arrays.asList("--network", IIDM_FILENAME, "--config", CONFIG_FILENAME);

        return new SimpleCommandBuilder()
                .id("dynaflow_lf")
                .program(getProgram(config))
                .args(args)
                .build();
    }

    public static Command getVersionCommand(DynaFlowConfig config) {
        List<String> args = Collections.singletonList("--version");
        return new SimpleCommandBuilder()
                .id("dynaflow_version")
                .program(getProgram(config))
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

    private static CommandExecution createCommandExecution(DynaFlowConfig config) {
        Command cmd = getCommand(config);
        return new CommandExecution(cmd, 1, 0);
    }

    @Override
    public CompletableFuture<LoadFlowResult> run(Network network, ComputationManager computationManager, String workingStateId, LoadFlowParameters loadFlowParameters) {
        Objects.requireNonNull(network);
        Objects.requireNonNull(computationManager);
        Objects.requireNonNull(workingStateId);
        Objects.requireNonNull(loadFlowParameters);
        DynaFlowParameters dynaFlowParameters = getParametersExt(loadFlowParameters);
        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());
        ExecutionEnvironment env = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        Command versionCmd = getVersionCommand(config);
        DynaFlowUtil.checkDynaFlowVersion(env, computationManager, versionCmd);
        return computationManager.execute(env, new AbstractExecutionHandler<LoadFlowResult>() {

            @Override
            public List<CommandExecution> before(Path workingDir) throws IOException {
                network.getVariantManager().setWorkingVariant(workingStateId);
                writeIIDM(workingDir, network);
                DynaFlowConfigSerializer.serialize(loadFlowParameters, dynaFlowParameters, workingDir, workingDir.resolve(CONFIG_FILENAME));
                return Collections.singletonList(createCommandExecution(config));
            }

            @Override
            public LoadFlowResult after(Path workingDir, ExecutionReport report) throws IOException {
                Path absoluteWorkingDir = workingDir.toAbsolutePath();
                super.after(absoluteWorkingDir, report);
                network.getVariantManager().setWorkingVariant(workingStateId);
                DynawoResultsNetworkUpdate.update(network, NetworkXml.read(workingDir.resolve("outputs").resolve("finalState").resolve(OUTPUT_IIDM_FILENAME)));

                Map<String, String> metrics = new HashMap<>();
                String logs = null;
                return new LoadFlowResultImpl(true, metrics, logs);
            }
        });
    }
}
