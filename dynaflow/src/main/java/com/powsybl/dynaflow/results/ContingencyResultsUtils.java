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
import java.util.stream.Collectors;

import static com.powsybl.dynaflow.SecurityAnalysisConstants.CONSTRAINTS_FOLDER;
import static com.powsybl.dynawo.commons.DynawoConstants.AGGREGATED_RESULTS;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class ContingencyResultsUtils {

    private ContingencyResultsUtils() {
    }

    /**
     * Build the pre-contingency results from the input network
     */
    public static PreContingencyResult getPreContingencyResult(Network network, LimitViolationFilter violationFilter) {
        List<LimitViolation> limitViolations = Security.checkLimits(network);
        List<LimitViolation> filteredViolations = violationFilter.apply(limitViolations, network);
        NetworkResult networkResult = new NetworkResult(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        return new PreContingencyResult(LoadFlowResult.ComponentResult.Status.CONVERGED, new LimitViolationsResult(filteredViolations), networkResult);
    }

    /**
     * Build the post-contingency results from the constraints files written by dynawo
     */
    public static List<PostContingencyResult> getPostContingencyResults(Network network, LimitViolationFilter violationFilter,
                                                                  Path workingDir, List<Contingency> contingencies) {
        Path constraintsDir = workingDir.resolve(CONSTRAINTS_FOLDER);
        Path results = workingDir.resolve(AGGREGATED_RESULTS);
        Map<String, Status> scenarioResults = new HashMap<>();
        if (Files.exists(results)) {
            new XmlScenarioResultParser().parse(results, s -> scenarioResults.put(s.id(), s.status()));
        }
        return contingencies.stream()
                .map(c -> new PostContingencyResult(c,
                        ResultsUtil.convertStatus(scenarioResults.getOrDefault(c.getId(), Status.EXECUTION_PROBLEM)),
                        getLimitViolationsResult(network, violationFilter, constraintsDir, c)))
                .toList();
    }

    private static LimitViolationsResult getLimitViolationsResult(Network network, LimitViolationFilter violationFilter,
                                            Path constraintsDir, Contingency contingency) {
        Path constraintsFile = constraintsDir.resolve("constraints_" + contingency.getId() + ".xml");
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
