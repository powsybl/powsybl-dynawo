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
import com.powsybl.contingency.ContingencyList;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.iidm.export.Exporters;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.xml.IidmXmlVersion;
import com.powsybl.iidm.xml.NetworkXml;
import com.powsybl.iidm.xml.XMLExporter;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.security.*;
import com.powsybl.security.interceptors.CurrentLimitViolationInterceptor;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;
import com.powsybl.security.json.SecurityAnalysisResultDeserializer;
import com.powsybl.security.results.PostContingencyResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.powsybl.dynaflow.DynaFlowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaFlowConstants.IIDM_FILENAME;

/**
 * @author Luma Zamarreno <zamarrenolm at aia.es>
 */
public class DynaFlowSecurityAnalysis {

    private static final String WORKING_DIR_PREFIX = "dynaflow_sa_";
    private static final String DYNAFLOW_LAUNCHER_PROGRAM_NAME = "dynaflow-launcher.sh";
    private static final String CONTINGENCIES_FILENAME = "contingencies.json";
    private static final String SECURITY_ANALISIS_RESULTS_FILENAME = "securityAnalysisResults.json";
    private static final String BASE_CASE_FOLDER = "BaseCase";
    private static final String DYNAFLOW_OUTPUT_FOLDER = "outputs";
    private static final String DYNAWO_FINAL_STATE_FOLDER = "finalState";
    private static final String DYNAWO_OUTPUT_NETWORK_FILENAME = "outputIIDM.xml";

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
        return config.getHomeDir().resolve(DYNAFLOW_LAUNCHER_PROGRAM_NAME).toString();
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
            writer.writeValue(os, ContingencyList.of(contingencies.toArray(Contingency[]::new)));
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
                super.after(workingDir, report);
                network.getVariantManager().setWorkingVariant(workingVariantId);

                // If the results have already been prepared, just read them ...
                Path saOutput = workingDir.resolve(DYNAFLOW_OUTPUT_FOLDER).resolve(SECURITY_ANALISIS_RESULTS_FILENAME);
                if (Files.exists(saOutput)) {
                    return new SecurityAnalysisReport(SecurityAnalysisResultDeserializer.read(saOutput));
                } else {
                    // Build the results from the output networks written by DynaFlow
                    LimitViolationsResult baseCaseResult = resultsFromOutputNetwork(workingDir.resolve(BASE_CASE_FOLDER));
                    List<PostContingencyResult> contingenciesResults = contingencies.stream()
                        .map(c -> new PostContingencyResult(c, resultsFromOutputNetwork(workingDir.resolve(c.getId()))))
                        .collect(Collectors.toList());
                    return new SecurityAnalysisReport(
                        new SecurityAnalysisResult(baseCaseResult, contingenciesResults)
                    );
                }
            }
        });
    }

    private static LimitViolationsResult resultsFromOutputNetwork(Path folder) {
        boolean computationOk;
        List<LimitViolation> limitViolations;

        Path outputNetworkPath = outputNetworkPath(folder);
        if (Files.exists(outputNetworkPath)) {
            Network outputNetwork = NetworkXml.read(outputNetworkPath);
            computationOk = true;
            limitViolations = Security.checkLimits(outputNetwork);
        } else {
            computationOk = false;
            limitViolations = Collections.emptyList();
        }
        return new LimitViolationsResult(computationOk, limitViolations);
    }

    private static Path outputNetworkPath(Path folder) {
        return folder
            .resolve(DYNAFLOW_OUTPUT_FOLDER)
            .resolve(DYNAWO_FINAL_STATE_FOLDER)
            .resolve(DYNAWO_OUTPUT_NETWORK_FILENAME);
    }
}
