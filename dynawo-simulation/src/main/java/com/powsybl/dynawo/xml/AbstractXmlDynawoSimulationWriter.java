/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

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

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
abstract class AbstractXmlDynawoSimulationWriter<T> {

    private final String xmlFileName;
    private final String xmlRootName;

    protected AbstractXmlDynawoSimulationWriter(String xmlFileName, String xmlRootName) {
        this.xmlFileName = Objects.requireNonNull(xmlFileName);
        this.xmlRootName = Objects.requireNonNull(xmlRootName);
    }

    public void createXmlFileFromDataSupplier(Path workingDir, T dataSupplier) throws IOException {
        Objects.requireNonNull(workingDir);
        Objects.requireNonNull(dataSupplier);
        Path file = workingDir.resolve(xmlFileName);

        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, xmlRootName);
                xmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);

                write(xmlWriter, dataSupplier);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    abstract void write(XMLStreamWriter writer, T dataSupplier) throws XMLStreamException;
}
