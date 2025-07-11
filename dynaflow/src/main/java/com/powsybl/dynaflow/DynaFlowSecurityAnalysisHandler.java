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
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.SidedContingencyElement;
import com.powsybl.contingency.contingency.list.ContingencyList;
import com.powsybl.contingency.contingency.list.DefaultContingencyList;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.ExportMode;
import com.powsybl.dynawo.contingency.ContingencyResultsUtils;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.SecurityAnalysisResult;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import static com.powsybl.dynaflow.DynaFlowConstants.CONFIG_FILENAME;
import static com.powsybl.dynaflow.DynaflowReports.createSidedContingencyReportNode;
import static com.powsybl.dynaflow.SecurityAnalysisConstants.CONTINGENCIES_FILENAME;
import static com.powsybl.dynawo.commons.DynawoConstants.NETWORK_FILENAME;
import static com.powsybl.dynawo.commons.DynawoConstants.TIMELINE_FOLDER;
import static com.powsybl.dynawo.commons.DynawoUtil.getCommandExecutions;
import static com.powsybl.dynawo.contingency.ContingencyResultsUtils.createSecurityAnalysisResult;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynaFlowSecurityAnalysisHandler extends AbstractExecutionHandler<SecurityAnalysisReport> {

    private final Command command;
    private final Network network;
    private final String workingVariantId;
    private final SecurityAnalysisParameters securityAnalysisParameters;
    private final List<Contingency> contingencies;
    private final LimitViolationFilter violationFilter;
    private final ReportNode reportNode;

    public DynaFlowSecurityAnalysisHandler(Network network, String workingVariantId, Command command,
                                           SecurityAnalysisParameters securityAnalysisParameters, List<Contingency> contingencies,
                                           LimitViolationFilter violationFilter, ReportNode reportNode) {
        this.network = network;
        this.workingVariantId = workingVariantId;
        this.command = command;
        this.securityAnalysisParameters = securityAnalysisParameters;
        this.contingencies = contingencies;
        this.violationFilter = violationFilter;
        this.reportNode = reportNode;
    }

    @Override
    public List<CommandExecution> before(Path workingDir) throws IOException {
        network.getVariantManager().setWorkingVariant(workingVariantId);

        DynawoUtil.writeIidm(network, workingDir.resolve(NETWORK_FILENAME));
        writeParameters(securityAnalysisParameters, workingDir);
        writeContingencies(contingencies, workingDir);
        return getCommandExecutions(command);
    }

    @Override
    public SecurityAnalysisReport after(Path workingDir, ExecutionReport report) throws IOException {
        super.after(workingDir, report);
        network.getVariantManager().setWorkingVariant(workingVariantId);
        SecurityAnalysisResult result = createSecurityAnalysisResult(network, violationFilter, workingDir, contingencies);
        ContingencyResultsUtils.reportContingencyResults(result.getPostContingencyResults(),
                workingDir.resolve(TIMELINE_FOLDER), ExportMode.XML, reportNode);
        return new SecurityAnalysisReport(result);
    }

    private void writeContingencies(List<Contingency> contingencies, Path workingDir) throws IOException {
        try (OutputStream os = Files.newOutputStream(workingDir.resolve(CONTINGENCIES_FILENAME))) {
            ObjectMapper mapper = JsonUtil.createObjectMapper();
            ContingencyJsonModule module = new ContingencyJsonModule();
            mapper.registerModule(module);
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
            writer.writeValue(os, buildContingencyList(contingencies));
        }
    }

    private ContingencyList buildContingencyList(List<Contingency> contingencies) {
        return new DefaultContingencyList("", contingencies.stream().filter(nonSidedContingency()).toList());
    }

    private Predicate<Contingency> nonSidedContingency() {
        return c -> {
            if (c instanceof SidedContingencyElement sidedC && sidedC.getVoltageLevelId() != null) {
                createSidedContingencyReportNode(reportNode, c.getId());
                return false;
            }
            return true;
        };
    }

    private static void writeParameters(SecurityAnalysisParameters securityAnalysisParameters, Path workingDir) throws IOException {
        // TODO(Luma) Take into account also Security Analysis parameters
        LoadFlowParameters loadFlowParameters = securityAnalysisParameters.getLoadFlowParameters();
        DynaFlowParameters dynaFlowParameters = DynaFlowProvider.getParametersExt(loadFlowParameters);
        DynaFlowSecurityAnalysisParameters dynaFlowSecurityAnalysisParameters = DynaFlowSecurityAnalysisProvider.getParametersExt(securityAnalysisParameters);
        DynaFlowConfigSerializer.serialize(loadFlowParameters, dynaFlowParameters, dynaFlowSecurityAnalysisParameters,
                Path.of("."), workingDir.resolve(CONFIG_FILENAME));
    }
}
