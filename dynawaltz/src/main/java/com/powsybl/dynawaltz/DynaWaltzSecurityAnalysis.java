/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.computation.*;
import com.powsybl.contingency.ContingenciesProvider;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.contingency.list.ContingencyList;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.dynaflow.DynaFlowParameters;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynaflow.xml.ConstraintsReader;
import com.powsybl.dynawaltz.xml.CurvesXml;
import com.powsybl.dynawaltz.xml.DydXml;
import com.powsybl.dynawaltz.xml.JobsXml;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.dynawaltz.xml.securityanalysis.*;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.security.*;
import com.powsybl.security.interceptors.CurrentLimitViolationInterceptor;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;
import com.powsybl.security.results.NetworkResult;
import com.powsybl.security.results.PostContingencyResult;
import com.powsybl.security.results.PreContingencyResult;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.powsybl.dynaflow.DynaFlowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaFlowConstants.IIDM_FILENAME;
import static com.powsybl.dynawaltz.DynaWaltzSecurityAnalysisProvider.getCommand;
import static com.powsybl.dynawaltz.DynaWaltzSecurityAnalysisProvider.getVersionCommand;
import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
//TODO factorize with DynaFlowSecurityAnalysis
public class DynaWaltzSecurityAnalysis {

    private static final String WORKING_DIR_PREFIX = "dynaflow_sa_";
    private static final String CONTINGENCIES_FILENAME = "contingencies.json";
    private static final String DYNAWO_CONSTRAINTS_FOLDER = "constraints";

    private final DynaWaltzConfig dynaWaltzConfig;
    private final ComputationManager computationManager;
    private final Network network;
    private final LimitViolationFilter violationFilter;
    private final List<SecurityAnalysisInterceptor> interceptors;

    public DynaWaltzSecurityAnalysis(Network network, LimitViolationFilter filter, ComputationManager computationManager,
                                     DynaWaltzConfig dynaWaltzConfig) {
        this.network = Objects.requireNonNull(network);
        this.violationFilter = Objects.requireNonNull(filter);
        this.interceptors = new ArrayList<>();
        this.computationManager = Objects.requireNonNull(computationManager);

        interceptors.add(new CurrentLimitViolationInterceptor());
        this.dynaWaltzConfig = Objects.requireNonNull(dynaWaltzConfig);
    }

    private static DynaFlowParameters getParametersExt(LoadFlowParameters parameters) {
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        if (parametersExt == null) {
            parametersExt = new DynaFlowParameters();
        }
        return parametersExt;
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
                                                         ContingenciesProvider contingenciesProvider) {
        Objects.requireNonNull(workingVariantId);
        Objects.requireNonNull(securityAnalysisParameters);
        Objects.requireNonNull(contingenciesProvider);

        ExecutionEnvironment env = new ExecutionEnvironment(Collections.emptyMap(), WORKING_DIR_PREFIX, dynaWaltzConfig.isDebug());
        Command versionCmd = getVersionCommand(dynaWaltzConfig);
        DynawoUtil.requireDynawoMinVersion(env, computationManager, versionCmd, true);
        List<Contingency> contingencies = contingenciesProvider.getContingencies(network);
        //TODO
        SecurityAnalysisContext context = null;
        return computationManager.execute(env, new DynaWaltzSecurityAnalysisHandler(context, contingencies, securityAnalysisParameters));
    }

    private final class DynaWaltzSecurityAnalysisHandler extends AbstractExecutionHandler<SecurityAnalysisReport> {

        private final SecurityAnalysisContext context;
        private final Network dynawoInput;
        private final List<Contingency> contingencies;
        private final SecurityAnalysisParameters securityAnalysisParameters;

        public DynaWaltzSecurityAnalysisHandler(SecurityAnalysisContext context, List<Contingency> contingencies, SecurityAnalysisParameters securityAnalysisParameters) {
            this.context = context;
            this.dynawoInput = context.getDynaWaltzParameters().isMergeLoads()
                    ? LoadsMerger.mergeLoads(context.getNetwork())
                    : context.getNetwork();
            this.contingencies = contingencies;
            this.securityAnalysisParameters = securityAnalysisParameters;
        }

        @Override
        public List<CommandExecution> before(Path workingDir) throws IOException {
            network.getVariantManager().setWorkingVariant(context.getWorkingVariantId());

            DynawoUtil.writeIidm(network, workingDir.resolve(IIDM_FILENAME));
            writeParameters(securityAnalysisParameters, workingDir);
            writeContingencies(contingencies, workingDir);
            Command cmd = getCommand(dynaWaltzConfig);
            return Collections.singletonList(new CommandExecution(cmd, 1));
        }

        @Override
        public SecurityAnalysisReport after(Path workingDir, ExecutionReport report) throws IOException {
            super.after(workingDir, report);
            network.getVariantManager().setWorkingVariant(context.getWorkingVariantId());

            // Build the pre-contingency results from the input network
            PreContingencyResult preContingencyResult = getPreContingencyResult(network, violationFilter);
            Path constraintsDir = workingDir.resolve(DYNAWO_CONSTRAINTS_FOLDER);

            // Build the post-contingency results from the constraints files written by dynawo
            List<PostContingencyResult> contingenciesResults = contingencies.stream()
                    .map(c -> getPostContingencyResult(network, violationFilter, constraintsDir, c))
                    .collect(Collectors.toList());

            return new SecurityAnalysisReport(
                    new SecurityAnalysisResult(preContingencyResult, contingenciesResults, Collections.emptyList())
            );
        }

        private void writeInputFiles(Path workingDir) {
            try {
                DynawoUtil.writeIidm(dynawoInput, workingDir.resolve(NETWORK_FILENAME));
                JobsXml.write(workingDir, context);
                DydXml.write(workingDir, context);
                ParametersXml.write(workingDir, context);
                if (context.withCurves()) {
                    CurvesXml.write(workingDir, context);
                }
                MultipleJobsXml.write(workingDir, context);
                ContingenciesDydXml.write(workingDir, context);
                ContingenciesParXml.write(workingDir, context);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (XMLStreamException e) {
                throw new UncheckedXmlStreamException(e);
            }
        }
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
            // TODO passer dans un module SA avec ConstraintsReader( ou en commons)
            List<LimitViolation> limitViolationsRead = ConstraintsReader.read(network, constraintsFile);
            List<LimitViolation> limitViolationsFiltered = violationFilter.apply(limitViolationsRead, network);
            return new PostContingencyResult(c, PostContingencyComputationStatus.CONVERGED, new LimitViolationsResult(limitViolationsFiltered));
        } else {
            return new PostContingencyResult(c, PostContingencyComputationStatus.FAILED, Collections.emptyList());
        }
    }
}
