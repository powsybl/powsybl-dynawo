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
import com.powsybl.contingency.Contingency;
import com.powsybl.dynawo.algorithms.AbstractDynawoAlgorithmsHandler;
import com.powsybl.dynawo.algorithms.xml.ContingenciesDydXml;
import com.powsybl.dynawo.algorithms.xml.ContingenciesParXml;
import com.powsybl.dynawo.margincalculation.results.MarginCalculationResult;
import com.powsybl.dynawo.margincalculation.results.XmlMarginCalculationResultParser;
import com.powsybl.dynawo.margincalculation.xml.MultipleJobsXml;
import com.powsybl.dynawo.xml.DynawoData;
import com.powsybl.dynawo.xml.DydXml;
import com.powsybl.dynawo.xml.JobsXml;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.powsybl.dynawo.DynawoSimulationConstants.FINAL_STEP_DYD_FILENAME;
import static com.powsybl.dynawo.contingency.ContingencyConstants.AGGREGATED_RESULTS;
import static com.powsybl.dynawo.margincalculation.xml.MarginCalculationConstant.LOAD_VARIATION_AREA_FILENAME;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public class MarginCalculationHandler extends AbstractDynawoAlgorithmsHandler<MarginCalculationResult, MarginCalculationContext> {

    public MarginCalculationHandler(MarginCalculationContext context, Command command, ReportNode reportNode) {
        super(context, command, reportNode);
    }

    @Override
    public MarginCalculationResult after(Path workingDir, ExecutionReport report) throws IOException {
        super.after(workingDir, report);
        context.getNetwork().getVariantManager().setWorkingVariant(context.getWorkingVariantId());
        Path resultsFile = workingDir.resolve(AGGREGATED_RESULTS);
        new XmlMarginCalculationResultParser().parse(resultsFile);
        return new MarginCalculationResult(new XmlMarginCalculationResultParser().parse(resultsFile));
    }

    @Override
    protected List<Contingency> getContingencies() {
        return context.getContingencies();
    }

    @Override
    protected void writeMultipleJobs(Path workingDir) throws XMLStreamException, IOException {
        MultipleJobsXml.write(workingDir, context);
        JobsXml.write(workingDir, context, LOAD_VARIATION_AREA_FILENAME);
        DydXml.write(workingDir, LOAD_VARIATION_AREA_FILENAME, context.getLoadVariationAreaDydData());
        ContingenciesDydXml.write(workingDir, context.getContingencyEventModels());
        ContingenciesParXml.write(workingDir, context.getContingencyEventModels());
        JobsXml.writeFinalStep(workingDir, context);
        Optional<DynawoData> dydData = context.getFinalStepDydData();
        if (dydData.isPresent()) {
            DydXml.write(workingDir, FINAL_STEP_DYD_FILENAME, dydData.get());
        }
    }
}
