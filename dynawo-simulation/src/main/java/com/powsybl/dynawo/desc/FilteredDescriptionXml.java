/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.desc;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.dynawo.parameters.ParameterType;

import javax.xml.XMLConstants;
import javax.xml.stream.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Parse ModelDescription with modifiable Parameter only
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class FilteredDescriptionXml {

    private FilteredDescriptionXml() {
    }

    public static ModelDescription load(InputStream descFile) {
        try {
            XMLStreamReader xmlReader = createXmlInputFactory().createXMLStreamReader(descFile);
            return readAndClose(xmlReader);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public static ModelDescription load(Path descFile) {
        try (Reader reader = Files.newBufferedReader(descFile, StandardCharsets.UTF_8)) {
            XMLStreamReader xmlReader = createXmlInputFactory().createXMLStreamReader(reader);
            return readAndClose(xmlReader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public static void load(Path descFile, Consumer<ModelDescription> consumer) {
        try (Reader reader = Files.newBufferedReader(descFile, StandardCharsets.UTF_8)) {
            XMLStreamReader xmlReader = createXmlInputFactory().createXMLStreamReader(reader);
            consumer.accept(readAndClose(xmlReader));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static ModelDescription readAndClose(XMLStreamReader xmlReader) throws XMLStreamException {
        ParsingContext parsingContext = new ParsingContext();
        skipComments(xmlReader);
        XmlUtil.readSubElements(xmlReader, elementName -> {
            try {
                switch (elementName) {
                    case "name" -> parsingContext.name = XmlUtil.readText(xmlReader);
                    case "elements" -> XmlUtil.readSubElements(xmlReader, subElem -> {
                        switch (subElem) {
                            case "parameters" -> createModifiableParameterList(xmlReader, parsingContext.parameters);
                            case "variables" -> createVariableList(xmlReader, parsingContext.variables);
                            default -> closeAndThrowException(xmlReader, xmlReader.getLocalName());
                        }
                    });
                    default -> closeAndThrowException(xmlReader, xmlReader.getLocalName());
                }
            } catch (XMLStreamException e) {
                throw new UncheckedXmlStreamException(e);
            }
        });
        xmlReader.close();
        return new ModelDescription(parsingContext.name, parsingContext.parameters, parsingContext.variables);
    }

    private static void createModifiableParameterList(XMLStreamReader xmlReader, List<ModifiableParameter> parameters) {
        XmlUtil.readSubElements(xmlReader, elementName -> {
            try {
                if (elementName.equals("parameter")) {
                    if (!XmlUtil.readBooleanAttribute(xmlReader, "readOnly", true) &&
                            xmlReader.getAttributeValue(null, "defaultValue") == null) {
                        String name = xmlReader.getAttributeValue(null, "name");
                        ParameterType valueType = ParameterType.valueOf(xmlReader.getAttributeValue(null, "valueType"));
                        Cardinality cardinality = convertCardinality(xmlReader.getAttributeValue(null, "cardinality"));
                        parameters.add(new ModifiableParameter(name, valueType, cardinality));
                    }
                    XmlUtil.readEndElementOrThrow(xmlReader);
                } else {
                    closeAndThrowException(xmlReader, xmlReader.getLocalName());
                }
            } catch (XMLStreamException e) {
                throw new UncheckedXmlStreamException(e);
            }
        });
    }

    private static Cardinality convertCardinality(String cardinality) {
        return switch (cardinality) {
            case "1" -> Cardinality.ONE;
            case "*" -> Cardinality.ANY;
            default -> throw new IllegalStateException("Unexpected value: " + cardinality);
        };
    }

    private static void createVariableList(XMLStreamReader xmlReader, List<Variable> variables) {
        XmlUtil.readSubElements(xmlReader, elementName -> {
            try {
                if (elementName.equals("variable")) {
                    String name = xmlReader.getAttributeValue(null, "name");
                    ParameterType valueType = ParameterType.valueOf(xmlReader.getAttributeValue(null, "valueType"));
                    XmlUtil.readEndElementOrThrow(xmlReader);
                    variables.add(new Variable(name, valueType));
                } else {
                    closeAndThrowException(xmlReader, xmlReader.getLocalName());
                }
            } catch (XMLStreamException e) {
                throw new UncheckedXmlStreamException(e);
            }
        });
    }

    private static XMLInputFactory createXmlInputFactory() {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return factory;
    }

    private static void skipComments(XMLStreamReader xmlReader) throws XMLStreamException {
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
    }

    private static void closeAndThrowException(XMLStreamReader xmlReader, String unexpectedElement) {
        try {
            xmlReader.close();
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
        throw new PowsyblException("Unexpected element: " + unexpectedElement);
    }

    private static final class ParsingContext {
        private String name = "";
        private final List<ModifiableParameter> parameters = new ArrayList<>();
        private final List<Variable> variables = new ArrayList<>();
    }
}
