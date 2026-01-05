/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.Command;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.dynawo.algorithms.AbstractDynawoAlgorithmsHandler;
import com.powsybl.dynawo.algorithms.xml.NodeFaultsDydXml;
import com.powsybl.dynawo.algorithms.xml.NodeFaultsParXml;
import com.powsybl.dynawo.criticaltimecalculation.results.CriticalTimeCalculationResults;
import com.powsybl.dynawo.criticaltimecalculation.results.XmlCriticalTimeCalculationResultsParser;
import com.powsybl.dynawo.criticaltimecalculation.xml.MultipleJobsXml;
import com.powsybl.dynawo.xml.JobsXml;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;

import static com.powsybl.dynawo.contingency.ContingencyConstants.AGGREGATED_RESULTS;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public class CriticalTimeCalculationHandler extends AbstractDynawoAlgorithmsHandler<CriticalTimeCalculationResults, CriticalTimeCalculationContext> {

    public CriticalTimeCalculationHandler(CriticalTimeCalculationContext context, Command command, ReportNode reportNode) {
        super(context, command, reportNode);
    }

    @Override
    public CriticalTimeCalculationResults after(Path workingDir, ExecutionReport report) throws IOException {
        super.after(workingDir, report);
        context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        Path resultsFile = workingDir.resolve(AGGREGATED_RESULTS);
        new XmlCriticalTimeCalculationResultsParser().parse(resultsFile);
        return new CriticalTimeCalculationResults(new XmlCriticalTimeCalculationResultsParser().parse(resultsFile));
    }

    @Override
    protected void writeMultipleJobs(Path workingDir) throws XMLStreamException, IOException {
        MultipleJobsXml.write(workingDir, context);
        NodeFaultsDydXml.write(workingDir, context.getNodeFaultEventModels());
        NodeFaultsParXml.write(workingDir, context.getNodeFaultEventModels());
        JobsXml.write(workingDir, context);
    }
}
