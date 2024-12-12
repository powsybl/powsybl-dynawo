/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.dynaflow.results.ContingencyResultsUtils;
import com.powsybl.dynawo.xml.DydXml;
import com.powsybl.dynawo.xml.JobsXml;
import com.powsybl.dynawo.xml.ParametersXml;
import com.powsybl.dynawo.commons.DynawoUtil;
import com.powsybl.dynawo.commons.NetworkResultsUpdater;
import com.powsybl.dynawo.security.xml.ContingenciesDydXml;
import com.powsybl.dynawo.security.xml.ContingenciesParXml;
import com.powsybl.dynawo.security.xml.MultipleJobsXml;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.NetworkSerDe;
import com.powsybl.security.LimitViolationFilter;
import com.powsybl.security.SecurityAnalysisReport;
import com.powsybl.security.SecurityAnalysisResult;
import com.powsybl.security.interceptors.SecurityAnalysisInterceptor;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import static com.powsybl.dynawo.commons.DynawoConstants.*;
import static com.powsybl.dynawo.commons.DynawoUtil.getCommandExecutions;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class DynawoSecurityAnalysisHandler extends AbstractExecutionHandler<SecurityAnalysisReport> {

    private final SecurityAnalysisContext context;
    private final Command command;
    private final Network network;
    private final LimitViolationFilter violationFilter;
    private final List<SecurityAnalysisInterceptor> interceptors;
    private final ReportNode reportNode;

    public DynawoSecurityAnalysisHandler(SecurityAnalysisContext context, Command command,
                                         LimitViolationFilter violationFilter, List<SecurityAnalysisInterceptor> interceptors,
                                         ReportNode reportNode) {
        this.context = context;
        this.network = context.getNetwork();
        this.command = command;
        this.violationFilter = violationFilter;
        this.interceptors = interceptors;
        this.reportNode = reportNode;
    }

    @Override
    public List<CommandExecution> before(Path workingDir) throws IOException {
        network.getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        Path outputNetworkFile = workingDir.resolve(OUTPUT_IIDM_FILENAME_PATH);
        if (Files.exists(outputNetworkFile)) {
            Files.delete(outputNetworkFile);
        }
        writeInputFiles(workingDir);
        return getCommandExecutions(command);
    }

    @Override
    public SecurityAnalysisReport after(Path workingDir, ExecutionReport report) throws IOException {
        super.after(workingDir, report);
        context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        Path outputNetworkFile = workingDir.resolve(OUTPUT_IIDM_FILENAME_PATH);
        if (Files.exists(outputNetworkFile)) {
            NetworkResultsUpdater.update(context.getNetwork(), NetworkSerDe.read(outputNetworkFile), context.getDynawoSimulationParameters().isMergeLoads());
        }
        ContingencyResultsUtils.reportContingenciesTimelines(context.getContingencies(), workingDir.resolve(TIMELINE_FOLDER), reportNode);

        return new SecurityAnalysisReport(
                new SecurityAnalysisResult(
                        ContingencyResultsUtils.getPreContingencyResult(network, violationFilter),
                        ContingencyResultsUtils.getPostContingencyResults(network, violationFilter, workingDir, context.getContingencies()),
                        Collections.emptyList())
        );
    }

    private void writeInputFiles(Path workingDir) {
        try {
            DynawoUtil.writeIidm(network, workingDir.resolve(NETWORK_FILENAME));
            JobsXml.write(workingDir, context);
            DydXml.write(workingDir, context);
            ParametersXml.write(workingDir, context);
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
