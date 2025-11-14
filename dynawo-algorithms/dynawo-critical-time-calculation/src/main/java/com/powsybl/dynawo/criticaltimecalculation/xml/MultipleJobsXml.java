/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.xml;

import com.powsybl.dynawo.algorithms.NodeFaultEventModels;
import com.powsybl.dynawo.algorithms.xml.XmlUtil;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationContext;
import com.powsybl.dynawo.criticaltimecalculation.CriticalTimeCalculationParameters;
import com.powsybl.dynawo.models.BlackBoxModel;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawo.DynawoSimulationConstants.JOBS_FILENAME;
import static com.powsybl.dynawo.algorithms.xml.AlgorithmsConstants.MULTIPLE_JOBS_FILENAME;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class MultipleJobsXml {

    private MultipleJobsXml() {
    }

    public static void write(Path workingDir, CriticalTimeCalculationContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(MULTIPLE_JOBS_FILENAME);
        XmlUtil.write(file, "multipleJobs", w -> writeCriticalTimeCalculation(w, context));
    }

    private static void writeCriticalTimeCalculation(XMLStreamWriter writer, CriticalTimeCalculationContext context) throws XMLStreamException {
        CriticalTimeCalculationParameters parameters = context.getCriticalTimeCalculationParameters();
        writer.writeStartElement("criticalTimeCalculation");
        writer.writeAttribute("accuracy", Double.toString(parameters.getAccuracy()));
        writer.writeAttribute("dydId", parameters.getElementId());
        writer.writeAttribute("parName", parameters.getParName());
        writer.writeAttribute("minValue", Double.toString(parameters.getMinValue()));
        writer.writeAttribute("maxValue", Double.toString(parameters.getMaxValue()));
        writer.writeAttribute("mode", parameters.getMode().toString());
        writeScenarios(writer, context.getNodeFaultEventModels());
        writer.writeEndElement();
    }

    private static void writeScenarios(XMLStreamWriter writer, List<NodeFaultEventModels> models) throws XMLStreamException {
        writer.writeStartElement("scenarios");
        writer.writeAttribute("jobsFile", JOBS_FILENAME);
        for (NodeFaultEventModels model : models) {
            String dydId = null;
            for (BlackBoxModel bbm : model.eventModels()) {
                if ("NodeFault".equals(bbm.getLib())) {
                    dydId = bbm.getDynamicModelId();
                    break;
                }
            }
            writeScenario(writer, model.getId(), dydId);
        }
        writer.writeEndElement();
    }

    private static void writeScenario(XMLStreamWriter writer, String id, String dydId) throws XMLStreamException {
        writer.writeEmptyElement("scenario");
        writer.writeAttribute("id", id);
        writer.writeAttribute("dydFile", id + ".dyd");
        if (dydId != null) {
            writer.writeAttribute("dydId", dydId);
        }
    }
}
