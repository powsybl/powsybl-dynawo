/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.powsybl.commons.json.JsonUtil;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.contingency.list.ContingencyList;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynaflow.xml.ConstraintsReader;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.security.*;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;
import com.powsybl.security.results.NetworkResult;
import com.powsybl.security.results.PostContingencyResult;
import com.powsybl.security.results.PreContingencyResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.powsybl.dynaflow.DynaFlowConstants.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class DynaFlowSecurityAnalysisHandler extends AbstractExecutionHandler<SecurityAnalysisReport> {

    private static final String DYNAWO_CONSTRAINTS_FOLDER = "constraints";

    private final DynaFlowConfig config;
    private final Network network;
    private final String workingVariantId;
    private final SecurityAnalysisParameters securityAnalysisParameters;
    private final List<Contingency> contingencies;
    private final LimitViolationFilter violationFilter;
    private final List<SecurityAnalysisInterceptor> interceptors;

    public DynaFlowSecurityAnalysisHandler(Network network, String workingVariantId, DynaFlowConfig config,
                                           SecurityAnalysisParameters securityAnalysisParameters, List<Contingency> contingencies,
                                           LimitViolationFilter violationFilter, List<SecurityAnalysisInterceptor> interceptors) {
        this.network = network;
        this.workingVariantId = workingVariantId;
        this.config = config;
        this.securityAnalysisParameters = securityAnalysisParameters;
        this.contingencies = contingencies;
        this.violationFilter = violationFilter;
        this.interceptors = interceptors;
    }

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
        Path constraintsDir = workingDir.resolve(DYNAWO_CONSTRAINTS_FOLDER);

        // Build the post-contingency results from the constraints files written by dynawo
        List<PostContingencyResult> contingenciesResults = contingencies.stream()
                .map(c -> getPostContingencyResult(network, violationFilter, constraintsDir, c))
                .collect(Collectors.toList());

        return new SecurityAnalysisReport(
                new SecurityAnalysisResult(preContingencyResult, contingenciesResults, Collections.emptyList())
        );
    }

    private static CommandExecution createCommandExecution(DynaFlowConfig config) {
        Command cmd = DynaFlowSecurityAnalysisProvider.getCommand(config);
        return new CommandExecution(cmd, 1, 0);
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

    private static DynaFlowParameters getParametersExt(LoadFlowParameters parameters) {
        DynaFlowParameters parametersExt = parameters.getExtension(DynaFlowParameters.class);
        if (parametersExt == null) {
            parametersExt = new DynaFlowParameters();
        }
        return parametersExt;
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
}
