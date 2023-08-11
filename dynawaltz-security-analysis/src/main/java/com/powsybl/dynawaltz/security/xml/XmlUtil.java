/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.security.xml;

import com.powsybl.dynawaltz.security.ContingencyEventModels;
import com.powsybl.dynawaltz.security.SecurityAnalysisContext;
import com.powsybl.dynawaltz.xml.XmlStreamWriterFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public final class XmlUtil {

    @FunctionalInterface
    public interface XmlDynawaltzContingenciesWriter {
        void writeContingencies(XMLStreamWriter writer, SecurityAnalysisContext dynaWaltzContext) throws XMLStreamException;
    }

    @FunctionalInterface
    public interface XmlDynawaltzEventWriter {
        void writeEvent(XMLStreamWriter writer, SecurityAnalysisContext dynaWaltzContext, ContingencyEventModels model) throws XMLStreamException;
    }

    private XmlUtil() {
    }

    public static void write(Path file, SecurityAnalysisContext context, String elementName, XmlDynawaltzEventWriter xmlDynawaltzEventWriter, ContingencyEventModels model) throws IOException, XMLStreamException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(context);
        Objects.requireNonNull(elementName);
        Objects.requireNonNull(xmlDynawaltzEventWriter);

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, elementName);
                xmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);

                xmlDynawaltzEventWriter.writeEvent(xmlWriter, context, model);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }

    public static void write(Path file, SecurityAnalysisContext context, String elementName, XmlDynawaltzContingenciesWriter xmlDynawaltzWriter) throws IOException, XMLStreamException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(context);
        Objects.requireNonNull(elementName);
        Objects.requireNonNull(xmlDynawaltzWriter);

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.writeStartElement(elementName);
                xmlWriter.writeNamespace("", DYN_URI);

                xmlDynawaltzWriter.writeContingencies(xmlWriter, context);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }
}
