/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DynawoContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.BiConsumer;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

/**
 * @author Mathieu Bague <mathieu.bague@rte-france.com>
 */
public final class XmlUtil {

    private XmlUtil() {
    }

    public static void write(Path file, DynawoContext context, String elementName, BiConsumer<XMLStreamWriter, DynawoContext> write) throws IOException, XMLStreamException {
        Objects.requireNonNull(file);
        Objects.requireNonNull(context);
        Objects.requireNonNull(elementName);
        Objects.requireNonNull(write);

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, elementName);
                xmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);

                write.accept(xmlWriter, context);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }
}
