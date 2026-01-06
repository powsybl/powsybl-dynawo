/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.xml;

import com.powsybl.dynawo.algorithms.xml.XmlUtil;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultEventModels;
import com.powsybl.dynawo.xml.XmlStreamWriterFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class NodeFaultXmlUtil extends XmlUtil {

    @FunctionalInterface
    public interface XmlDynawoEventWriter {
        void writeEvent(XMLStreamWriter writer, NodeFaultEventModels model) throws XMLStreamException;
    }

    private NodeFaultXmlUtil() {
        super();
    }

    public static void write(Path file, String elementName, XmlDynawoEventWriter xmlNodeFaultEventWriter, NodeFaultEventModels model) throws IOException, XMLStreamException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(elementName);
        Objects.requireNonNull(xmlNodeFaultEventWriter);

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, elementName);
                xmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);

                xmlNodeFaultEventWriter.writeEvent(xmlWriter, model);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }
}
