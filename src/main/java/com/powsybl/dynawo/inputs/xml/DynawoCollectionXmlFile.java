/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.xml;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
interface DynawoCollectionXmlFile {

    String getFilename();

    String getCollectionTag();

    void writeCollection(XMLStreamWriter writer, List<?> list) throws XMLStreamException;

    default String getNamespacePrefix() {
        return DynawoXmlConstants.DYN_PREFIX;
    }

    default String getNamespaceUri() {
        return DynawoXmlConstants.DYN_URI;
    }

    default void write(Path workingDir, List<?> list) throws IOException, XMLStreamException {
        Path file = workingDir.resolve(getFilename());
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = output.createXMLStreamWriter(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(getNamespacePrefix(), getNamespaceUri());
                xmlWriter.writeStartElement(getNamespaceUri(), getCollectionTag());
                xmlWriter.writeNamespace(getNamespacePrefix(), getNamespaceUri());

                writeCollection(xmlWriter, list);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }
}
