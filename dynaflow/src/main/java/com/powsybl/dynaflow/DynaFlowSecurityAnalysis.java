/**
 * Copyright (c) 2021, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynaflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.computation.*;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.IidmXmlVersion;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.security.*;
import com.powsybl.security.interceptors.CurrentLimitViolationInterceptor;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;
import com.powsybl.security.json.SecurityAnalysisResultDeserializer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static com.powsybl.dynaflow.DynaFlowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaFlowConstants.IIDM_FILENAME;

/**
 * @author Luma Zamarreno <zamarrenolm at aia.es>
 */
public class DynaFlowSecurityAnalysis {

    private static final String WORKING_DIR_PREFIX = "dynaflow_sa_";
    private static final String CONTINGENCIES_FILENAME = "contingencies.json";
    private static final String SECURITY_ANALISIS_RESULTS_FILENAME = "securityAnalysisResults.json";

    private final Supplier<DynaFlowConfig> configSupplier;

    private final ComputationManager computationManager;
    private final Network network;
    private final LimitViolationDetector violationDetector;
    private final LimitViolationFilter violationFilter;
    private final List<SecurityAnalysisInterceptor> interceptors;

    public DynaFlowSecurityAnalysis(Network network, LimitViolationDetector detector,
                                    LimitViolationFilter filter, ComputationManager computationManager) {
        this.network = Objects.requireNonNull(network);
        this.violationDetector = Objects.requireNonNull(detector);
        this.violationFilter = Objects.requireNonNull(filter);
        this.interceptors = new ArrayList<>();
        this.computationManager = Objects.requireNonNull(computationManager);

        interceptors.add(new CurrentLimitViolationInterceptor());
        // TODO(Luma) Allow additional sources for configuration?
        this.configSupplier = DynaFlowConfig::fromPropertyFile;
    }

    private static DynaFlowParameters getParametersExt(LoadFlowParameters parameters) {
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        if (parametersExt == null) {
            parametersExt = new DynaFlowParameters();
        }
        return parametersExt;
    }

    private static String getProgram(DynaFlowConfig config) {
        return config.getHomeDir().resolve("dynaflow-launcher.sh").toString();
    }

    public static Command getCommand(DynaFlowConfig config) {
        List<String> args = Arrays.asList("--network", IIDM_FILENAME,
            "--config", CONFIG_FILENAME,
            "--contingencies", CONTINGENCIES_FILENAME);
        return new SimpleCommandBuilder()
            .id("dynaflow_sa")
            .program(getProgram(config))
            .args(args)
            .build();
    }

    private static CommandExecution createCommandExecution(DynaFlowConfig config) {
        Command cmd = getCommand(config);
        return new CommandExecution(cmd, 1, 0);
    }

    public static Command getVersionCommand(DynaFlowConfig config) {
        List<String> args = Collections.singletonList("--version");
        return new SimpleCommandBuilder()
            .id("dynaflow_version")
            .program(getProgram(config))
            .args(args)
            .build();
    }

    private static void writeIIDM(Network network, Path workingDir) {
        Properties params = new Properties();
        params.setProperty(XMLExporter.VERSION, IidmXmlVersion.V_1_2.toString("."));
        Exporters.export("XIIDM", network, params, workingDir.resolve(IIDM_FILENAME));
    }

    private static void writeContingencies(List<Contingency> contingencies, Path workingDir) throws IOException {
        try (OutputStream os = Files.newOutputStream(workingDir.resolve(CONTINGENCIES_FILENAME))) {
            ObjectMapper mapper = JsonUtil.createObjectMapper();
            ContingencyJsonModule module = new ContingencyJsonModule();
            mapper.registerModule(module);
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(os, contingencies);
        }
    }

    private static void writeParameters(SecurityAnalysisParameters securityAnalysisParameters, Path workingDir) throws IOException {
        // TODO(Luma) Take into account also Security Analysis parameters
        LoadFlowParameters loadFlowParameters = securityAnalysisParameters.getLoadFlowParameters();
        DynaFlowParameters dynaFlowParameters = getParametersExt(loadFlowParameters);
        DynaFlowConfigSerializer.serialize(loadFlowParameters, dynaFlowParameters, workingDir, workingDir.resolve(CONFIG_FILENAME));
    }

    public void addInterceptor(SecurityAnalysisInterceptor interceptor) {
        interceptors.add(Objects.requireNonNull(interceptor));
    }

    public boolean removeInterceptor(SecurityAnalysisInterceptor interceptor) {
        return interceptors.remove(interceptor);
    }

    public CompletableFuture<SecurityAnalysisReport> run(String workingVariantId,
                                                         SecurityAnalysisParameters securityAnalysisParameters,
                                                         ContingenciesProvider contingenciesProvider) {
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(securityAnalysisParameters);
        Objects.requireNonNull(contingenciesProvider);

        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());
        ExecutionEnvironment env = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        Command versionCmd = getVersionCommand(config);
        DynaFlowUtil.checkDynaFlowVersion(env, computationManager, versionCmd);
        List<Contingency> contingencies = contingenciesProvider.getContingencies(network);
        return computationManager.execute(env, new AbstractExecutionHandler<SecurityAnalysisReport>() {
            @Override
            public List<CommandExecution> before(Path workingDir) throws IOException {
                network.getVariantManager().setWorkingVariant(workingVariantId);

                writeIIDM(network, workingDir);
                writeParameters(securityAnalysisParameters, workingDir);
                writeContingencies(contingencies, workingDir);
                return Collections.singletonList(createCommandExecution(config));
            }

            @Override
            public SecurityAnalysisReport after(Path workingDir, ExecutionReport report) throws IOException {
                Path absoluteWorkingDir = workingDir.toAbsolutePath();
                super.after(absoluteWorkingDir, report);
                network.getVariantManager().setWorkingVariant(workingVariantId);

                return new SecurityAnalysisReport(SecurityAnalysisResultDeserializer.read(workingDir.resolve("outputs").resolve(SECURITY_ANALISIS_RESULTS_FILENAME)));
            }
        });
    }
}