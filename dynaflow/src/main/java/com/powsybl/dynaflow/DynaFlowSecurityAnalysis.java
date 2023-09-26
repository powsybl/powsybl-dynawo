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
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.computation.*;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.contingency.list.ContingencyList;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynaflow.xml.ConstraintsReader;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.timeline.TimelineEntry;
import com.powsybl.dynawo.commons.timeline.XmlTimeLineParser;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.security.*;
import com.powsybl.security.interceptors.CurrentLimitViolationInterceptor;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;
import com.powsybl.security.results.NetworkResult;
import com.powsybl.security.results.PostContingencyResult;
import com.powsybl.security.results.PreContingencyResult;

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
    private static final String DYNAFLOW_LAUNCHER_PROGRAM_NAME = "dynaflow-launcher.sh";
    private static final String CONTINGENCIES_FILENAME = "contingencies.json";
    private static final String DYNAWO_CONSTRAINTS_FOLDER = "constraints";
    private static final String DYNAWO_TIMELINE_FOLDER = "timeLine";

    private final Supplier<DynaFlowConfig> configSupplier;

    private final ComputationManager computationManager;
    private final Network network;
    private final LimitViolationFilter violationFilter;
    private final List<SecurityAnalysisInterceptor> interceptors;

    public DynaFlowSecurityAnalysis(Network network, LimitViolationFilter filter, ComputationManager computationManager,
                                    Supplier<DynaFlowConfig> configSupplier) {
        this.network = Objects.requireNonNull(network);
        this.violationFilter = Objects.requireNonNull(filter);
        this.interceptors = new ArrayList<>();
        this.computationManager = Objects.requireNonNull(computationManager);

        interceptors.add(new CurrentLimitViolationInterceptor());
        this.configSupplier = Objects.requireNonNull(configSupplier);
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
        DynaFlowConfigSerializer.serialize(loadFlowParameters, dynaFlowParameters, Path.of("."), workingDir.resolve(CONFIG_FILENAME));
    }

    public void addInterceptor(SecurityAnalysisInterceptor interceptor) {
        interceptors.add(Objects.requireNonNull(interceptor));
    }

    public boolean removeInterceptor(SecurityAnalysisInterceptor interceptor) {
        return interceptors.remove(interceptor);
    }

    public CompletableFuture<SecurityAnalysisReport> run(String workingVariantId,
                                                         SecurityAnalysisParameters securityAnalysisParameters,
                                                         ContingenciesProvider contingenciesProvider,
                                                         Reporter reporter) {
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(securityAnalysisParameters);
        Objects.requireNonNull(contingenciesProvider);
        Objects.requireNonNull(reporter);

        DynaFlowConfig config = Objects.requireNonNull(configSupplier.get());
        ExecutionEnvironment env = new ExecutionEnvironment(config.createEnv(), WORKING_DIR_PREFIX, config.isDebug());
        Command versionCmd = getVersionCommand(config);
        DynawoUtil.requireDynawoMinVersion(env, computationManager, versionCmd, true);
        List<Contingency> contingencies = contingenciesProvider.getContingencies(network);
        return computationManager.execute(env, new AbstractExecutionHandler<>() {
            @Override
            public List<CommandExecution> before(Path workingDir) throws IOException {
                network.getVariantManager().setWorkingVariant(workingVariantId);

                DynawoUtil.writeIidm(network, workingDir.resolve(IIDM_FILENAME));
                writeParameters(securityAnalysisParameters, workingDir);
                writeContingencies(contingencies, workingDir);
                return Collections.singletonList(createCommandExecution(config));
            }

            @Override
            public SecurityAnalysisReport after(Path workingDir, ExecutionReport report) throws IOException {
                super.after(workingDir, report);
                network.getVariantManager().setWorkingVariant(workingVariantId);

                // Build the pre-contingency results from the input network
                PreContingencyResult preContingencyResult = getPreContingencyResult(network, violationFilter);

                // Build the post-contingency results from the constraints files written by dynawo
                Path constraintsDir = workingDir.resolve(DYNAWO_CONSTRAINTS_FOLDER);
                List<PostContingencyResult> contingenciesResults = contingencies.stream()
                    .map(c -> getPostContingencyResult(network, violationFilter, constraintsDir, c))
                    .toList();

                // Report the timeline events from the timeline files written by dynawo
                Path timelineDir = workingDir.resolve(DYNAWO_TIMELINE_FOLDER);
                contingencies.forEach(c -> {
                    Reporter contingencyReporter = Reports.createDynaFlowTimelineReporter(reporter, c.getId());
                    getTimeline(timelineDir, c).forEach(e -> Reports.reportTimelineEvent(contingencyReporter, e));
                });

                return new SecurityAnalysisReport(
                    new SecurityAnalysisResult(preContingencyResult, contingenciesResults, Collections.emptyList())
                );
            }
        });
    }

    private static PreContingencyResult getPreContingencyResult(Network network, LimitViolationFilter violationFilter) {
        List<LimitViolation> limitViolations = Security.checkLimits(network);
        List<LimitViolation> filteredViolations = violationFilter.apply(limitViolations, network);
        NetworkResult networkResult = new NetworkResult(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        return new PreContingencyResult(LoadFlowResult.ComponentResult.Status.CONVERGED, new LimitViolationsResult(filteredViolations), networkResult);
    }

    private static PostContingencyResult getPostContingencyResult(Network network, LimitViolationFilter violationFilter,
                                                                  Path constraintsDir, Contingency c) {
        Path constraintsFile = constraintsDir.resolve("constraints_" + c.getId() + ".xml");
        if (Files.exists(constraintsFile)) {
            List<LimitViolation> limitViolationsRead = ConstraintsReader.read(network, constraintsFile);
            List<LimitViolation> limitViolationsFiltered = violationFilter.apply(limitViolationsRead, network);
            return new PostContingencyResult(c, PostContingencyComputationStatus.CONVERGED, new LimitViolationsResult(limitViolationsFiltered));
        } else {
            return new PostContingencyResult(c, PostContingencyComputationStatus.FAILED, Collections.emptyList());
        }
    }

    private List<TimelineEntry> getTimeline(Path constraintsDir, Contingency c) {
        Path timelineFile = constraintsDir.resolve("timeline_" + c.getId() + ".xml");
        return new XmlTimeLineParser().parse(timelineFile);
    }
}
