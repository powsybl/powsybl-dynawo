/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml.securityanalysis;

import com.powsybl.dynawaltz.ContingencyEventModels;
import com.powsybl.dynawaltz.SecurityAnalysisContext;
import com.powsybl.dynawaltz.xml.XmlUtil;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.*;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class MultipleJobsXml {

    private MultipleJobsXml() {
    }

    public static void write(Path workingDir, SecurityAnalysisContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(MULTIPLE_JOBS_FILENAME);

        XmlUtil.write(file, context, "multipleJobs", MultipleJobsXml::writeContingency, false);
    }

    private static void writeContingency(XMLStreamWriter writer, SecurityAnalysisContext context) throws XMLStreamException {
        writer.writeStartElement("scenarios");
        writer.writeAttribute("jobsFile", JOBS_FILENAME);
        for (ContingencyEventModels model : context.getContingencyEventModels()) {
            writeScenario(writer, model.getId());
        }
        writeBaseScenario(writer);
        writer.writeEndElement();
    }

    private static void writeScenario(XMLStreamWriter writer, String id) throws XMLStreamException {
        writer.writeEmptyElement("scenario");
        writer.writeAttribute("id", id);
        writer.writeAttribute("dydFile", id + ".dyd");
    }

    private static void writeBaseScenario(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEmptyElement("scenario");
        writer.writeAttribute("id", "Base");
    }
}
