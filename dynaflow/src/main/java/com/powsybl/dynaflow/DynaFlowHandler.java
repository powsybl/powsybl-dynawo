/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.AbstractExecutionHandler;
import com.powsybl.computation.Command;
import com.powsybl.computation.CommandExecution;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.computation.local.LocalComputationConfig;
import com.powsybl.dynaflow.json.DynaFlowConfigSerializer;
import com.powsybl.dynawo.commons.*;
import com.powsybl.dynawo.commons.loadmerge.LoadsMerger;
import com.powsybl.dynawo.commons.timeline.TimelineEntry;
import com.powsybl.dynawo.commons.timeline.XmlTimeLineParser;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.serde.NetworkSerDe;
import com.powsybl.loadflow.LoadFlowParameters;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.loadflow.LoadFlowResultImpl;
import com.powsybl.loadflow.json.LoadFlowResultDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.powsybl.dynaflow.DynaFlowConstants.*;
import static com.powsybl.dynawo.commons.DynawoConstants.*;
import static com.powsybl.dynawo.commons.DynawoUtil.getCommandExecutions;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class DynaFlowHandler extends AbstractExecutionHandler<LoadFlowResult> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynaFlowHandler.class);

    private final Network network;
    private final Network dynawoInput;
    private final String workingStateId;
    private final DynaFlowParameters dynaFlowParameters;
    private final LoadFlowParameters loadFlowParameters;
    private final Command command;
    private final DynawoVersion dynawoVersion;
    private final ReportNode reportNode;

    public DynaFlowHandler(Network network, String workingStateId, DynaFlowParameters dynaFlowParameters,
                           LoadFlowParameters loadFlowParameters, Command command, DynawoVersion dynawoVersion, ReportNode reportNode) {
        this.network = network;
        this.workingStateId = workingStateId;
        this.dynaFlowParameters = dynaFlowParameters;
        this.loadFlowParameters = loadFlowParameters;
        this.command = command;
        this.dynawoInput = this.dynaFlowParameters.isMergeLoads() ? LoadsMerger.mergeLoads(this.network) : this.network;
        this.dynawoVersion = dynawoVersion;
        this.reportNode = reportNode;
    }

    @Override
    public List<CommandExecution> before(Path workingDir) throws IOException {
        network.getVariantManager().setWorkingVariant(workingStateId);
        DynawoUtil.writeIidm(dynawoInput, workingDir.resolve(NETWORK_FILENAME), dynawoVersion);
        DynaFlowConfigSerializer.serialize(loadFlowParameters, dynaFlowParameters, Path.of("."), workingDir.resolve(CONFIG_FILENAME));

        Path tmpExecFile = LocalComputationConfig.load().getLocalDir().resolve(EXEC_TMP_FILENAME);
        Files.writeString(tmpExecFile, workingDir.toAbsolutePath().toString());

        return getCommandExecutions(command);
    }

    @Override
    public LoadFlowResult after(Path workingDir, ExecutionReport report) {
        reportTimeLine(workingDir);

        report.log();
        network.getVariantManager().setWorkingVariant(workingStateId);
        boolean status = true;
        Path outputNetworkFile = workingDir.resolve(OUTPUT_IIDM_FILENAME_PATH);

        if (Files.exists(outputNetworkFile)) {
            NetworkResultsUpdater.update(network, NetworkSerDe.read(outputNetworkFile), dynaFlowParameters.isMergeLoads());
        } else {
            status = false;
        }
        Path resultsPath = workingDir.resolve(OUTPUT_RESULTS_FILENAME);
        if (!Files.exists(resultsPath)) {
            Map<String, String> metrics = new HashMap<>();
            List<LoadFlowResult.ComponentResult> componentResults = new ArrayList<>(1);
            componentResults.add(new LoadFlowResultImpl.ComponentResultImpl(0,
                    0,
                    status ? LoadFlowResult.ComponentResult.Status.CONVERGED : LoadFlowResult.ComponentResult.Status.FAILED,
                    0,
                    "not-found",
                    0.,
                    Double.NaN));
            return new LoadFlowResultImpl(status, metrics, null, componentResults);
        }
        return LoadFlowResultDeserializer.read(resultsPath);
    }

    private void reportTimeLine(Path workingDir) {
        ReportNode dfReporter = DynaflowReports.createDynaFlowReportNode(reportNode, network.getId());
        Path timelineFile = workingDir.resolve(OUTPUTS_FOLDER)
                .resolve(TIMELINE_FOLDER)
                .resolve(TIMELINE_FILENAME + ExportMode.XML.getFileExtension());
        if (Files.exists(timelineFile)) {
            List<TimelineEntry> entries = new XmlTimeLineParser().parse(timelineFile);
            if (!entries.isEmpty()) {
                ReportNode timelineReporter = CommonReports.createDynawoTimelineReportNode(dfReporter);
                entries.forEach(e -> CommonReports.reportTimelineEntry(timelineReporter, e));
            } else {
                CommonReports.reportEmptyTimeline(dfReporter);
            }
        } else {
            LOGGER.warn("Timeline file not found");
        }
    }
}
