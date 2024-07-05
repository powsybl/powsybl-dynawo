/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.parameters.Parameter;
import com.powsybl.dynawo.parameters.ParameterType;
import com.powsybl.dynawo.parameters.ParametersSet;
import com.powsybl.dynawo.parameters.Reference;

import javax.xml.XMLConstants;
import javax.xml.stream.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public final class ParametersXml {

    public static final String PARAMETERS_SET_ELEMENT_NAME = "parametersSet";

    private ParametersXml() {
    }

    public static List<ParametersSet> load(InputStream parametersFile) {
        try {
            XMLStreamReader xmlReader = createXmlInputFactory().createXMLStreamReader(parametersFile);
            return readAndClose(xmlReader);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public static ParametersSet load(InputStream parametersFile, String parameterSetId) {
        try {
            XMLStreamReader xmlReader = createXmlInputFactory().createXMLStreamReader(parametersFile);
            ParametersSet parametersSet = readOneSetAndClose(xmlReader, parameterSetId);
            if (parametersSet == null) {
                throw new PowsyblException("Could not find parameters set with id='" + parameterSetId + "' in given input stream");
            }
            return parametersSet;
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public static List<ParametersSet> load(Path parametersFile) {
        try (Reader reader = Files.newBufferedReader(parametersFile, StandardCharsets.UTF_8)) {
            XMLStreamReader xmlReader = createXmlInputFactory().createXMLStreamReader(reader);
            return readAndClose(xmlReader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public static ParametersSet load(Path parametersFile, String parameterSetId) {
        try (Reader reader = Files.newBufferedReader(parametersFile, StandardCharsets.UTF_8)) {
            XMLStreamReader xmlReader = createXmlInputFactory().createXMLStreamReader(reader);
            ParametersSet parametersSet = readOneSetAndClose(xmlReader, parameterSetId);
            if (parametersSet == null) {
                throw new PowsyblException("Could not find parameters set with id='" + parameterSetId + "' in file '" + parametersFile + "'");
            }
            return parametersSet;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static List<ParametersSet> readAndClose(XMLStreamReader xmlReader) throws XMLStreamException {
        List<ParametersSet> parametersSets = new ArrayList<>();
        skipComments(xmlReader);
        XmlUtil.readSubElements(xmlReader, elementName -> {
            if (!elementName.equals("set")) {
                closeAndThrowException(xmlReader, xmlReader.getLocalName());
            }
            String parameterSetIdRead = xmlReader.getAttributeValue(null, "id");
            parametersSets.add(createParametersSet(xmlReader, parameterSetIdRead));
        });
        xmlReader.close();
        return parametersSets;
    }

    private static ParametersSet readOneSetAndClose(XMLStreamReader xmlReader, String parameterSetId) throws XMLStreamException {
        AtomicReference<ParametersSet> parametersSet = new AtomicReference<>();
        skipComments(xmlReader);
        XmlUtil.readSubElements(xmlReader, elementName -> {
            if (parametersSet.get() != null) {
                return;
            }
            if (!elementName.equals("set")) {
                closeAndThrowException(xmlReader, xmlReader.getLocalName());
            }
            String idSetRead = xmlReader.getAttributeValue(null, "id");
            if (idSetRead.equals(parameterSetId)) {
                parametersSet.set(createParametersSet(xmlReader, parameterSetId));
            } else {
                XmlUtil.skipSubElements(xmlReader);
            }
        });
        xmlReader.close();
        return parametersSet.get();
    }

    private static ParametersSet createParametersSet(XMLStreamReader xmlReader, String parameterSetId) {
        ParametersSet parametersSet = new ParametersSet(parameterSetId);
        XmlUtil.readSubElements(xmlReader, elementName -> {
            try {
                String name = xmlReader.getAttributeValue(null, "name");
                ParameterType type = ParameterType.valueOf(xmlReader.getAttributeValue(null, "type"));
                switch (elementName) {
                    case "par" -> {
                        String value = xmlReader.getAttributeValue(null, "value");
                        XmlUtil.readEndElementOrThrow(xmlReader);
                        parametersSet.addParameter(name, type, value);
                    }
                    case "reference" -> {
                        String origData = xmlReader.getAttributeValue(null, "origData");
                        String origName = xmlReader.getAttributeValue(null, "origName");
                        String componentId = xmlReader.getAttributeValue(null, "componentId");
                        XmlUtil.readEndElementOrThrow(xmlReader);
                        parametersSet.addReference(name, type, origData, origName, componentId);
                    }
                    default -> closeAndThrowException(xmlReader, xmlReader.getLocalName());
                }
            } catch (XMLStreamException e) {
                throw new UncheckedXmlStreamException(e);
            }

        });
        return parametersSet;
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

    public static void write(Path workingDir, DynawoSimulationContext context) {
        Objects.requireNonNull(workingDir);

        write(context.getDynamicModelsParameters(), context.getSimulationParFile(), workingDir, DYN_PREFIX);
        DynawoSimulationParameters parameters = context.getDynawoSimulationParameters();
        write(parameters.getModelParameters(), DynawoSimulationParameters.MODELS_OUTPUT_PARAMETERS_FILE, workingDir, "");
        write(List.of(parameters.getNetworkParameters()), DynawoSimulationParameters.NETWORK_OUTPUT_PARAMETERS_FILE, workingDir, "");
        write(List.of(parameters.getSolverParameters()), DynawoSimulationParameters.SOLVER_OUTPUT_PARAMETERS_FILE, workingDir, "");
    }

    public static void write(Collection<ParametersSet> parametersSets, String filename, Path workingDir, String dynPrefix) {
        Path parametersPath = workingDir.resolve(filename);
        try (Writer writer = Files.newBufferedWriter(parametersPath, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(dynPrefix, DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, PARAMETERS_SET_ELEMENT_NAME);
                xmlWriter.writeNamespace(dynPrefix, DYN_URI);
                for (ParametersSet parametersSet : parametersSets) {
                    writeParametersSet(xmlWriter, parametersSet);
                }
                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeParametersSet(XMLStreamWriter xmlWriter, ParametersSet parametersSet) throws XMLStreamException {
        xmlWriter.writeStartElement(DYN_URI, "set");
        xmlWriter.writeAttribute("id", parametersSet.getId());
        for (Parameter par : parametersSet.getParameters().values()) {
            ParametersXml.writeParameter(xmlWriter, par.type(), par.name(), par.value());
        }
        for (Reference par : parametersSet.getReferences()) {
            ParametersXml.writeReference(xmlWriter, par.type(), par.name(), par.origData(), par.origName(), par.componentId());
        }
        xmlWriter.writeEndElement();
    }

    public static void writeParameter(XMLStreamWriter writer, ParameterType type, String name, String value) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "par");
        writer.writeAttribute("type", type.toString());
        writer.writeAttribute("name", name);
        writer.writeAttribute("value", value);
    }

    public static void writeReference(XMLStreamWriter writer, ParameterType type, String name, String origData, String origName, String componentId) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "reference");
        writer.writeAttribute("type", type.toString());
        writer.writeAttribute("name", name);
        writer.writeAttribute("origData", origData);
        writer.writeAttribute("origName", origName);
        if (componentId != null) {
            writer.writeAttribute("componentId", componentId);
        }
    }
}
