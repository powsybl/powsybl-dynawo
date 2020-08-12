/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoParametersDatabase {

    public enum ParameterType {
        DOUBLE,
        INT,
        BOOL,
        STRING;
    }

    public static class Parameter {

        public Parameter(String name, ParameterType type, String value) {
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

    public static class ParameterSet {

        public ParameterSet() {
            this.parameters = new HashMap<>();
        }

        public void addParameter(String name, ParameterType type, String value) {
            parameters.put(name, new Parameter(name, type, value));
        }

        public Parameter getParameter(String name) {
            return parameters.get(name);
        }

        private final Map<String, Parameter> parameters;

    }

    public static DynawoParametersDatabase load(Path parametersFile) {
        return read(parametersFile);
    }

    private DynawoParametersDatabase() {
        this.parameterSets = new HashMap<>();
    }

    private void addParameterSet(String parameterSetId, ParameterSet parameterSet) {
        parameterSets.put(parameterSetId, parameterSet);
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

    public ParameterSet getParameterSet(String parameterSetId) {
        ParameterSet parameterSet = parameterSets.get(parameterSetId);
        if (parameterSet == null) {
            throw new IllegalArgumentException("ParameterSet not found: " + parameterSetId);
        }
        return parameterSet;
    }

    public Parameter getParameter(String parameterSetId, String parameterName) {
        ParameterSet parameterSet = getParameterSet(parameterSetId);

        Parameter parameter = parameterSet.getParameter(parameterName);
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

    private static DynawoParametersDatabase read(Path parametersFile) {
        Objects.requireNonNull(parametersFile);

        DynawoParametersDatabase parametersDatabase = new DynawoParametersDatabase();
        try (Reader reader = Files.newBufferedReader(parametersFile, StandardCharsets.UTF_8)) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            XMLStreamReader xmlReader = factory.createXMLStreamReader(reader);
            try {
                read(xmlReader, parametersDatabase);
            } finally {
                xmlReader.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
        return parametersDatabase;
    }

    private static void read(XMLStreamReader xmlReader, DynawoParametersDatabase parametersDatabase) {
        try {
            int state = xmlReader.next();
            while (state == XMLStreamConstants.COMMENT) {
                state = xmlReader.next();
            }
            XmlUtil.readUntilEndElement("parametersSet", xmlReader, () -> {
                if (xmlReader.getLocalName().equals("set")) {
                    String parameterSetId = xmlReader.getAttributeValue(null, "id");
                    ParameterSet parameterSet = new ParameterSet();
                    XmlUtil.readUntilEndElement("set", xmlReader, () -> {
                        String name = xmlReader.getAttributeValue(null, "name");
                        ParameterType type = ParameterType.valueOf(xmlReader.getAttributeValue(null, "type"));
                        if (xmlReader.getLocalName().equals("par")) {
                            String value = xmlReader.getAttributeValue(null, "value");
                            parameterSet.addParameter(name, type, value);
                        } else if (xmlReader.getLocalName().equals("reference")) {
                            // Not supported
                        } else {
                            throw new PowsyblException("Unexpected element: " + xmlReader.getLocalName());
                        }
                    });
                    parametersDatabase.addParameterSet(parameterSetId, parameterSet);
                } else {
                    throw new PowsyblException("Unexpected element: " + xmlReader.getLocalName());
                }
            });
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private final Map<String, ParameterSet> parameterSets;
}
