/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.commons;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractXmlParser<T> {

    public List<T> parse(Path path) {
        List<T> series = new ArrayList<>();
        parse(path, series::add);
        return series;
    }

    public void parse(Path path, Consumer<T> consumer) {
        Objects.requireNonNull(path);
        if (!Files.exists(path)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            parse(reader, consumer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public List<T> parse(Reader reader) throws XMLStreamException {
        List<T> series = new ArrayList<>();
        parse(reader, series::add);
        return series;
    }

    public void parse(Reader reader, Consumer<T> consumer) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        XMLStreamReader xmlReader = null;
        try {
            xmlReader = factory.createXMLStreamReader(reader);
            read(xmlReader, consumer);
        } finally {
            if (xmlReader != null) {
                xmlReader.close();
            }
        }
    }

    protected abstract void read(XMLStreamReader reader, Consumer<T> consumer) throws XMLStreamException;
}
