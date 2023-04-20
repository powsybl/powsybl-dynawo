/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.dynawaltz.parameters.Reference;
import com.powsybl.dynawaltz.xml.ParametersXml;
import com.powsybl.dynawaltz.xml.XmlStreamWriterFactory;

import javax.xml.XMLConstants;
import javax.xml.stream.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.powsybl.dynawaltz.ParametersSet.ParameterType.DOUBLE;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class ParametersSet {

    public void write(Path parametersPath) throws IOException, XMLStreamException {
        try (Writer writer = Files.newBufferedWriter(parametersPath, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix("", DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, "parametersSet");
                xmlWriter.writeNamespace("", DYN_URI);
                for (Map.Entry<String, Set> e : parameterSets.entrySet()) {
                    writeSet(xmlWriter, e.getKey(), e.getValue());
                }
                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }

    private void writeSet(XMLStreamWriter xmlWriter, String setId, Set set) throws XMLStreamException {
        xmlWriter.writeStartElement(DYN_URI, "set");
        xmlWriter.writeAttribute("id", setId);
        for (Parameter par : set.getParameters().values()) {
            ParametersXml.writeParameter(xmlWriter, par.getType(), par.getName(), par.getValue());
        }
        for (Reference par : set.getReferences()) {
            ParametersXml.writeReference(xmlWriter, par.getType(), par.getName(), par.getOrigData(), par.getOrigName());
        }
        xmlWriter.writeEndElement();
    }

    public enum ParameterType {
        DOUBLE,
        INT,
        BOOL,
        STRING;
    }

    public static class Parameter {

        public Parameter(@JsonProperty("name") String name,
                         @JsonProperty("type") ParameterType type,
                         @JsonProperty("value") String value) {
            this.name = Objects.requireNonNull(name);
            this.type = Objects.requireNonNull(type);
            this.value = Objects.requireNonNull(value);
        }

        public String getName() {
            return name;
        }

        public ParameterType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        private final String name;
        private final ParameterType type;
        private final String value;
    }

    public static class Set {

        public void addParameter(String name, ParameterType type, String value) {
            parameters.put(name, new Parameter(name, type, value));
        }

        public void addReference(String name, ParameterType type, String origData, String origName) {
            references.add(new Reference(name, type, origData, origName));
        }

        public Map<String, Parameter> getParameters() {
            return parameters;
        }

        public List<Reference> getReferences() {
            return references;
        }

        public Parameter getParameter(String name) {
            return parameters.get(name);
        }
        private final Map<String, Parameter> parameters = new LinkedHashMap<>();

        private final List<Reference> references = new ArrayList<>();
    }

    private final Map<String, Set> parameterSets;

    private ParametersSet() {
        this.parameterSets = new LinkedHashMap<>();
    }

    public Map<String, Set> getParameterSets() {
        return parameterSets;
    }

    private void addParameterSet(String parameterSetId, Set set) {
        parameterSets.put(parameterSetId, set);
    }

    public boolean getBool(String parameterSetId, String parameterName) {
        Parameter parameter = getParameter(parameterSetId, parameterName, ParameterType.BOOL);
        return Boolean.parseBoolean(parameter.getValue());
    }

    public double getDouble(String parameterSetId, String parameterName) {
        Parameter parameter = getParameter(parameterSetId, parameterName, ParameterType.DOUBLE);
        return Double.parseDouble(parameter.getValue());
    }

    public int getInt(String parameterSetId, String parameterName) {
        Parameter parameter = getParameter(parameterSetId, parameterName, ParameterType.INT);
        return Integer.parseInt(parameter.getValue());
    }

    public String getString(String parameterSetId, String parameterName) {
        Parameter parameter = getParameter(parameterSetId, parameterName, ParameterType.STRING);
        return parameter.getValue();
    }

    public Set getParameterSet(String parameterSetId) {
        Set set = parameterSets.get(parameterSetId);
        if (set == null) {
            throw new IllegalArgumentException("ParameterSet not found: " + parameterSetId);
        }
        return set;
    }

    public Parameter getParameter(String parameterSetId, String parameterName) {
        Set set = getParameterSet(parameterSetId);

        Parameter parameter = set.getParameter(parameterName);
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter not found: " + parameterSetId + "." + parameterName);
        }
        return parameter;
    }

    private Parameter getParameter(String parameterSetId, String parameterName, ParameterType type) {
        Parameter parameter = getParameter(parameterSetId, parameterName);
        if (parameter.getType() != type) {
            throw new PowsyblException("Invalid parameter type: " + parameter.getType() + " (" + type + " expected)");
        }
        return parameter;
    }

    public static ParametersSet load(InputStream parametersFile) {
        ParametersSet parametersDatabase = new ParametersSet();
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            XMLStreamReader xmlReader = factory.createXMLStreamReader(parametersFile);
            read(xmlReader, parametersDatabase);
            xmlReader.close();
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
        return parametersDatabase;
    }

    public static ParametersSet load(Path parametersFile) {
        ParametersSet parametersDatabase = new ParametersSet();
        try (Reader reader = Files.newBufferedReader(parametersFile, StandardCharsets.UTF_8)) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            XMLStreamReader xmlReader = factory.createXMLStreamReader(reader);
            read(xmlReader, parametersDatabase);
            xmlReader.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
        return parametersDatabase;
    }

    private static void read(XMLStreamReader xmlReader, ParametersSet parametersDatabase) throws XMLStreamException {
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        XmlUtil.readUntilEndElement("parametersSet", xmlReader, () -> {
            if (xmlReader.getLocalName().equals("set")) {
                String parameterSetId = xmlReader.getAttributeValue(null, "id");
                Set set = new Set();
                XmlUtil.readUntilEndElement("set", xmlReader, () -> {
                    String name = xmlReader.getAttributeValue(null, "name");
                    ParameterType type = ParameterType.valueOf(xmlReader.getAttributeValue(null, "type"));
                    if (xmlReader.getLocalName().equals("par")) {
                        String value = xmlReader.getAttributeValue(null, "value");
                        set.addParameter(name, type, value);
                    } else if (xmlReader.getLocalName().equals("reference")) {
                        String origData = xmlReader.getAttributeValue(null, "origData");
                        String origName = xmlReader.getAttributeValue(null, "origName");
                        set.addReference(name, type, origData, origName);
                    } else {
                        throw new PowsyblException("Unexpected element: " + xmlReader.getLocalName());
                    }
                });
                parametersDatabase.addParameterSet(parameterSetId, set);
            } else {
                throw new PowsyblException("Unexpected element: " + xmlReader.getLocalName());
            }
        });
    }
}
