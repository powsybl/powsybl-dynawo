/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import static com.powsybl.dynawo.xml.DynawoConstants.CRV_FILENAME;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.simulator.DynawoCurve;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class CurvesXml {

    private CurvesXml() {
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Objects.requireNonNull(context);
        Path file = workingDir.resolve(CRV_FILENAME);
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, "curvesInput");
                xmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);

                write(xmlWriter, context);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) throws XMLStreamException {
        for (Curve curve : context.getCurves()) {
            DynawoCurve dynCurve = (DynawoCurve) curve;
            writer.writeEmptyElement(DYN_URI, "curve");
            writer.writeAttribute("model", dynCurve.getModelId());
            writer.writeAttribute("variable", dynCurve.getVariable());
        }
    }
}
