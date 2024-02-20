/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynawaltz.DynaWaltzContext;

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
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public abstract class AbstractXmlDynawaltzWriter implements XmlDynawaltzWriter {

    private final String xmlFileName;
    private final String xmlRootName;

    protected AbstractXmlDynawaltzWriter(String xmlFileName, String xmlRootName) {
        this.xmlFileName = Objects.requireNonNull(xmlFileName);
        this.xmlRootName = Objects.requireNonNull(xmlRootName);
    }

    @Override
    public void createXmlFileFromContext(Path workingDir, DynaWaltzContext context) throws IOException {
        Objects.requireNonNull(workingDir);
        Objects.requireNonNull(context);
        Path file = workingDir.resolve(xmlFileName);

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, xmlRootName);
                xmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);

                write(xmlWriter, context);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    abstract void write(XMLStreamWriter writer, DynaWaltzContext dynaWaltzContext) throws XMLStreamException;
}
