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
import com.powsybl.dynaflow.ContingencyResultsUtils;
import com.powsybl.dynawo.algorithms.AbstractDynawoAlgorithmsHandler;
import com.powsybl.dynawo.commons.DynawoConstants;
import com.powsybl.dynawo.commons.NetworkResultsUpdater;
import com.powsybl.dynawo.security.xml.MultipleJobsXml;
import com.powsybl.iidm.serde.NetworkSerDe;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynaflow.SecurityAnalysisConstants.DYNAWO_CONSTRAINTS_FOLDER;
import static com.powsybl.dynawo.commons.DynawoConstants.DYNAWO_TIMELINE_FOLDER;
import static com.powsybl.dynawo.xml.DynawoSimulationConstants.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
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
        Path outputNetworkFile = workingDir.resolve(BASE_SCENARIO_FOLDER).resolve(OUTPUTS_FOLDER).resolve(FINAL_STATE_FOLDER).resolve(DynawoConstants.OUTPUT_IIDM_FILENAME);
        if (Files.exists(outputNetworkFile)) {
            NetworkResultsUpdater.update(context.getNetwork(), NetworkSerDe.read(outputNetworkFile), context.getDynawoSimulationParameters().isMergeLoads());
        }
        ContingencyResultsUtils.reportContingenciesTimelines(context.getContingencies(), workingDir.resolve(DYNAWO_TIMELINE_FOLDER), reportNode);

        return new SecurityAnalysisReport(
                new SecurityAnalysisResult(
                        ContingencyResultsUtils.getPreContingencyResult(network, violationFilter),
                        ContingencyResultsUtils.getPostContingencyResults(network, violationFilter, workingDir.resolve(DYNAWO_CONSTRAINTS_FOLDER), context.getContingencies()),
                        Collections.emptyList())
        );
    }

    @Override
    protected void writeMultipleJobs(Path workingDir) throws XMLStreamException, IOException {
        MultipleJobsXml.write(workingDir, context);
    }
}
