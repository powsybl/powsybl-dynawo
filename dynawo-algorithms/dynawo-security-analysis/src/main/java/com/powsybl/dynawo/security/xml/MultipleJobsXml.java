/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.security.xml;

import com.powsybl.dynawo.algorithms.xml.XmlUtil;
import com.powsybl.dynawo.algorithms.ContingencyEventModels;
import com.powsybl.dynawo.security.SecurityAnalysisContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawo.DynawoSimulationConstants.JOBS_FILENAME;
import static com.powsybl.dynawo.algorithms.xml.AlgorithmsConstants.MULTIPLE_JOBS_FILENAME;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class MultipleJobsXml {

    private MultipleJobsXml() {
    }

    public static void write(Path workingDir, SecurityAnalysisContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(MULTIPLE_JOBS_FILENAME);
        XmlUtil.write(file, "multipleJobs", w -> writeContingencies(w, context));
    }

    private static void writeContingencies(XMLStreamWriter writer, SecurityAnalysisContext context) throws XMLStreamException {
        writer.writeStartElement("scenarios");
        writer.writeAttribute("jobsFile", JOBS_FILENAME);
        for (ContingencyEventModels model : context.getContingencyEventModels()) {
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
