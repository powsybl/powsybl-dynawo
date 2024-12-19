/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.computation.Command;
import com.powsybl.computation.ExecutionReport;
import com.powsybl.dynawo.algorithms.AbstractDynawoAlgorithmsHandler;
import com.powsybl.dynawo.algorithms.xml.ContingenciesDydXml;
import com.powsybl.dynawo.algorithms.xml.ContingenciesParXml;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationReport;
import com.powsybl.dynawo.margincalculation.results.XmlMarginCalculationResultParser;
import com.powsybl.dynawo.margincalculation.xml.MultipleJobsXml;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;

import static com.powsybl.dynawo.commons.DynawoConstants.AGGREGATED_RESULTS;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationHandler extends AbstractDynawoAlgorithmsHandler<MarginCalculationReport, MarginCalculationContext> {

    public MarginCalculationHandler(MarginCalculationContext context, Command command, ReportNode reportNode) {
        super(context, command, reportNode);
    }

    @Override
    public MarginCalculationReport after(Path workingDir, ExecutionReport report) throws IOException {
        super.after(workingDir, report);
        context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        Path resultsFile = workingDir.resolve(AGGREGATED_RESULTS);
        new XmlMarginCalculationResultParser().parse(resultsFile);
        return new MarginCalculationReport(new XmlMarginCalculationResultParser().parse(resultsFile));
    }

    @Override
    protected void writeMultipleJobs(Path workingDir) throws XMLStreamException, IOException {
        MultipleJobsXml.write(workingDir, context);
        ContingenciesDydXml.write(workingDir, context.getContingencyEventModels());
        ContingenciesParXml.write(workingDir, context.getContingencyEventModels());
    }
}
