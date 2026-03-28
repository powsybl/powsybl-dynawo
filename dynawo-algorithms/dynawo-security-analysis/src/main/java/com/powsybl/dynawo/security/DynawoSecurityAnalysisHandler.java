/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.Command;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.algorithms.AbstractDynawoAlgorithmsHandler;
import com.powsybl.dynawo.algorithms.xml.ContingenciesDydXml;
import com.powsybl.dynawo.algorithms.xml.ContingenciesParXml;
import com.powsybl.dynawo.commons.NetworkResultsUpdater;
import com.powsybl.dynawo.contingency.ContingencyResultsUtils;
import com.powsybl.dynawo.security.xml.MultipleJobsXml;
import com.powsybl.dynawo.xml.JobsXml;
import com.powsybl.iidm.serde.NetworkSerDe;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.powsybl.dynawo.commons.DynawoConstants.*;
import static com.powsybl.dynawo.contingency.ContingencyResultsUtils.createSecurityAnalysisResult;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynawoSecurityAnalysisHandler extends AbstractDynawoAlgorithmsHandler<SecurityAnalysisReport, SecurityAnalysisContext> {

    private final LimitViolationFilter violationFilter;
    private final List<SecurityAnalysisInterceptor> interceptors;

    public DynawoSecurityAnalysisHandler(SecurityAnalysisContext context, Command command,
                                         LimitViolationFilter violationFilter, List<SecurityAnalysisInterceptor> interceptors,
                                         ReportNode reportNode) {
        super(context, command, reportNode);
        this.violationFilter = violationFilter;
        this.interceptors = interceptors;
    }

    @Override
    public SecurityAnalysisReport after(Path workingDir, ExecutionReport report) throws IOException {
        super.after(workingDir, report);
        context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        Path outputNetworkFile = workingDir.resolve(OUTPUT_IIDM_FILENAME_PATH);
        if (Files.exists(outputNetworkFile)) {
            NetworkResultsUpdater.update(context.getNetwork(), NetworkSerDe.read(outputNetworkFile), context.getDynawoSimulationParameters().isMergeLoads());
        }
        SecurityAnalysisResult result = createSecurityAnalysisResult(network, violationFilter, workingDir, context.getContingencies());
        ContingencyResultsUtils.reportContingencyResults(result.getPostContingencyResults(), workingDir.resolve(TIMELINE_FOLDER),
                context.getDynawoSimulationParameters().getTimelineExportMode(), reportNode);
        return new SecurityAnalysisReport(result);
    }

    @Override
    protected List<Contingency> getContingencies() {
        return context.getContingencies();
    }

    @Override
    protected void writeMultipleJobs(Path workingDir) throws XMLStreamException, IOException {
        JobsXml.write(workingDir, context);
        MultipleJobsXml.write(workingDir, context);
        ContingenciesDydXml.write(workingDir, context.getContingencyEventModels());
        ContingenciesParXml.write(workingDir, context.getContingencyEventModels());
    }
}
