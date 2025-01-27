/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.margincalculation.xml;

import com.powsybl.dynawo.algorithms.xml.XmlUtil;
import com.powsybl.dynawo.margincalculation.MarginCalculationContext;
import com.powsybl.dynawo.margincalculation.MarginCalculationParameters;
import com.powsybl.dynawo.algorithms.ContingencyEventModels;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.DynawoSimulationConstants.JOBS_FILENAME;
import static com.powsybl.dynawo.DynawoSimulationConstants.FINAL_STEP_JOBS_FILENAME;
import static com.powsybl.dynawo.algorithms.xml.AlgorithmsConstants.MULTIPLE_JOBS_FILENAME;
import static com.powsybl.dynawo.margincalculation.xml.MarginCalculationConstant.LOAD_VARIATION_AREA_ID;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class MultipleJobsXml {

    private MultipleJobsXml() {
    }

    public static void write(Path workingDir, MarginCalculationContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(MULTIPLE_JOBS_FILENAME);
        XmlUtil.write(file, "multipleJobs", w -> writeMarginCalculation(w, context));
    }

    private static void writeMarginCalculation(XMLStreamWriter writer, MarginCalculationContext context) throws XMLStreamException {
        MarginCalculationParameters parameters = context.getMarginCalculationParameters();
        writer.writeStartElement("marginCalculation");
        writer.writeAttribute("calculationType", parameters.getCalculationType().toString());
        writer.writeAttribute("accuracy", Integer.toString(parameters.getAccuracy()));
        writeScenarios(writer, context.getContingencyEventModels());
        writer.writeEmptyElement("loadIncrease");
        writer.writeAttribute("id", LOAD_VARIATION_AREA_ID);
        writer.writeAttribute("jobsFile", JOBS_FILENAME);
        writer.writeEndElement();
    }

    private static void writeScenarios(XMLStreamWriter writer, List<ContingencyEventModels> models) throws XMLStreamException {
        writer.writeStartElement("scenarios");
        writer.writeAttribute("jobsFile", FINAL_STEP_JOBS_FILENAME);
        for (ContingencyEventModels model : models) {
            writeScenario(writer, model.getId());
        }
        writer.writeEndElement();
    }

    private static void writeScenario(XMLStreamWriter writer, String id) throws XMLStreamException {
        writer.writeEmptyElement("scenario");
        writer.writeAttribute("id", id);
        writer.writeAttribute("dydFile", id + ".dyd");
    }
}
