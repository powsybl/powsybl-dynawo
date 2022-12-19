/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawaltz.xml;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawaltz.DynaWaltzContext;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public final class XmlUtil {

    @FunctionalInterface
    public interface XmlDynawaltzWriter {
        void write(XMLStreamWriter writer, DynaWaltzContext dynaWaltzContext) throws XMLStreamException;
    }

    private XmlUtil() {
    }

    public static void write(Path file, DynaWaltzContext context, String elementName, XmlDynawaltzWriter xmlDynawaltzWriter) throws IOException, XMLStreamException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(context);
        Objects.requireNonNull(elementName);
        Objects.requireNonNull(xmlDynawaltzWriter);

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, elementName);
                xmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);

                xmlDynawaltzWriter.write(xmlWriter, context);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }
}
