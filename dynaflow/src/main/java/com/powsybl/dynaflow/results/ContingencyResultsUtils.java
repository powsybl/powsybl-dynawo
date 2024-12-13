/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynaflow.results;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.contingency.Contingency;
import com.powsybl.dynaflow.DynaflowReports;
import com.powsybl.dynaflow.xml.ConstraintsReader;
import com.powsybl.dynawo.commons.CommonReports;
import com.powsybl.dynawo.commons.timeline.TimelineEntry;
import com.powsybl.dynawo.commons.timeline.XmlTimeLineParser;
import com.powsybl.iidm.network.Network;
import com.powsybl.loadflow.LoadFlowResult;
import com.powsybl.security.*;
import com.powsybl.security.results.NetworkResult;
import com.powsybl.security.results.PostContingencyResult;
import com.powsybl.security.results.PreContingencyResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.powsybl.dynaflow.SecurityAnalysisConstants.CONSTRAINTS_FOLDER;
import static com.powsybl.dynawo.commons.DynawoConstants.AGGREGATED_RESULTS;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class ContingencyResultsUtils {

    private ContingencyResultsUtils() {
    }

    public static SecurityAnalysisResult createSecurityAnalysisResult(Network network, LimitViolationFilter violationFilter,
                                                                      Path workingDir, List<Contingency> contingencies) {
        Map<String, Status> aggregatedResults = getAggregatedResults(workingDir);
        Path constraintsDir = workingDir.resolve(CONSTRAINTS_FOLDER);
        return new SecurityAnalysisResult(
                ContingencyResultsUtils.getPreContingencyResult(network, violationFilter),
                ContingencyResultsUtils.getPostContingencyResults(network, violationFilter, constraintsDir, aggregatedResults, contingencies),
                Collections.emptyList());
    }

    /**
     * Build the pre-contingency results from the constraints file written by dynawo or directly form the network if the results are not found
     */
    private static PreContingencyResult getPreContingencyResult(Network network, LimitViolationFilter violationFilter) {
        NetworkResult networkResult = new NetworkResult(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        List<LimitViolation> limitViolations = Security.checkLimits(network);
        List<LimitViolation> filteredViolations = violationFilter.apply(limitViolations, network);
        // Pre contingency always set to CONVERGED (see issue #174 & #414)
        return new PreContingencyResult(LoadFlowResult.ComponentResult.Status.CONVERGED, new LimitViolationsResult(filteredViolations), networkResult);
    }

    /**
     * Build the post-contingency results from the constraints files written by dynawo
     */
    private static List<PostContingencyResult> getPostContingencyResults(Network network, LimitViolationFilter violationFilter,
                                                                        Path constraintsDir, Map<String, Status> scenarioResults,
                                                                        List<Contingency> contingencies) {
        return contingencies.stream()
                .map(c -> new PostContingencyResult(c,
                        ResultsUtil.convertToPostStatus(scenarioResults.getOrDefault(c.getId(), Status.EXECUTION_PROBLEM)),
                        getLimitViolationsResult(network, violationFilter, constraintsDir, c.getId())))
                .toList();
    }

    private static Map<String, Status> getAggregatedResults(Path workingDir) {
        Path results = workingDir.resolve(AGGREGATED_RESULTS);
        Map<String, Status> scenarioResults = new HashMap<>();
        if (Files.exists(results)) {
            new XmlScenarioResultParser().parse(results, s -> scenarioResults.put(s.id(), s.status()));
        }
        return scenarioResults;
    }

    private static LimitViolationsResult getLimitViolationsResult(Network network, LimitViolationFilter violationFilter,
                                            Path constraintsDir, String contingencyName) {
        Path constraintsFile = constraintsDir.resolve("constraints_" + contingencyName + ".xml");
        if (Files.exists(constraintsFile)) {
            List<LimitViolation> limitViolationsRead = ConstraintsReader.read(network, constraintsFile);
            List<LimitViolation> limitViolationsFiltered = violationFilter.apply(limitViolationsRead, network);
            return new LimitViolationsResult(limitViolationsFiltered);
        }
        return LimitViolationsResult.empty();
    }

    // Report the timeline events from the timeline files written by dynawo
    public static void reportContingenciesTimelines(List<Contingency> contingencies, Path timelineDir, ReportNode reportNode) {
        contingencies.forEach(c -> {
            ReportNode contingencyReporter = DynaflowReports.createContingenciesTimelineReportNode(reportNode, c.getId());
            getTimeline(timelineDir, c).forEach(e -> CommonReports.reportTimelineEntry(contingencyReporter, e));
        });
    }

    private static List<TimelineEntry> getTimeline(Path timelineDir, Contingency c) {
        Path timelineFile = timelineDir.resolve("timeline_" + c.getId() + ".xml");
        return new XmlTimeLineParser().parse(timelineFile);
    }
}
