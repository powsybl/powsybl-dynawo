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
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.contingency.Contingency;
import com.powsybl.contingency.contingency.list.ContingencyList;
import com.powsybl.contingency.json.ContingencyJsonModule;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynawo.commons.CommonReports;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.timeline.TimelineEntry;
import com.powsybl.dynawo.commons.timeline.XmlTimeLineParser;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisParameters;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynaflow.SecurityAnalysisConstants.CONTINGENCIES_FILENAME;
import static com.powsybl.dynaflow.SecurityAnalysisConstants.DYNAWO_CONSTRAINTS_FOLDER;
import static com.powsybl.dynawo.commons.DynawoConstants.DYNAWO_TIMELINE_FOLDER;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class DynaFlowSecurityAnalysisHandler extends AbstractExecutionHandler<SecurityAnalysisReport> {

    private final DynaFlowConfig config;
    private final Network network;
    private final String workingVariantId;
    private final SecurityAnalysisParameters securityAnalysisParameters;
    private final List<Contingency> contingencies;
    private final LimitViolationFilter violationFilter;
    private final List<SecurityAnalysisInterceptor> interceptors;
    private final Reporter reporter;

    public DynaFlowSecurityAnalysisHandler(Network network, String workingVariantId, DynaFlowConfig config,
                                           SecurityAnalysisParameters securityAnalysisParameters, List<Contingency> contingencies,
                                           LimitViolationFilter violationFilter, List<SecurityAnalysisInterceptor> interceptors,
                                           Reporter reporter) {
        this.network = network;
        this.workingVariantId = workingVariantId;
        this.config = config;
        this.securityAnalysisParameters = securityAnalysisParameters;
        this.contingencies = contingencies;
        this.violationFilter = violationFilter;
        this.interceptors = interceptors;
        this.reporter = reporter;
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
        // Report the timeline events from the timeline files written by dynawo
        Path timelineDir = workingDir.resolve(DYNAWO_TIMELINE_FOLDER);
        contingencies.forEach(c -> {
            Reporter contingencyReporter = Reports.createDynaFlowTimelineReporter(reporter, c.getId());
            getTimeline(timelineDir, c).forEach(e -> CommonReports.reportTimelineEvent(contingencyReporter, e));
        });
        return new SecurityAnalysisReport(
                new SecurityAnalysisResult(
                        ContingencyResultsUtils.getPreContingencyResult(network, violationFilter),
                        ContingencyResultsUtils.getPostContingencyResults(network, violationFilter, workingDir.resolve(DYNAWO_CONSTRAINTS_FOLDER), contingencies),
                        Collections.emptyList())
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

    private List<TimelineEntry> getTimeline(Path timelineDir, Contingency c) {
        Path timelineFile = timelineDir.resolve("timeline_" + c.getId() + ".xml");
        return new XmlTimeLineParser().parse(timelineFile);
    }
}
