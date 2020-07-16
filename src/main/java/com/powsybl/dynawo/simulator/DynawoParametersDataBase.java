/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.xml.XmlUtil;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoParametersDataBase {

    public static DynawoParametersDataBase load() {
        return load(Paths.get("./models.par"));
    }

    public static DynawoParametersDataBase load(DynawoContext context) {
        return load(Paths.get(context.getDynawoParameters().getParametersFile()));
    }

    public static DynawoParametersDataBase load(Path parametersFile) {
        DynawoParametersDataBase parametersDataBase = new DynawoParametersDataBase();
        if (Files.exists(parametersFile)) {
            try (Reader reader = Files.newBufferedReader(parametersFile, StandardCharsets.UTF_8)) {
                XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(reader);
                try {
                    loadXML(xmlReader, parametersDataBase);
                } finally {
                    xmlReader.close();
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } catch (XMLStreamException e) {
                throw new UncheckedXmlStreamException(e);
            }
        }
        return parametersDataBase;
    }

    private static void loadXML(XMLStreamReader xmlReader, DynawoParametersDataBase parametersDataBase) {
        try {
            XmlUtil.readUntilEndElement("parametersSet", xmlReader, () -> {
                if (xmlReader.getLocalName().equals("set")) {
                    String parameterSetId = xmlReader.getAttributeValue(null, "id");
                    ParameterSet parameterSet = parametersDataBase.new ParameterSet();
                    XmlUtil.readUntilEndElement("set", xmlReader, () -> {
                        if (xmlReader.getLocalName().equals("par")) {
                            String name = xmlReader.getAttributeValue(null, "name");
                            String value = xmlReader.getAttributeValue(null, "value");
                            parameterSet.addParameter(name, value);
                        } else  if (xmlReader.getLocalName().equals("reference")) {
                            String name = xmlReader.getAttributeValue(null, "name");
                            String value = xmlReader.getAttributeValue(null, "origName");
                            parameterSet.addParameter(name, value);
                        }
                    });
                    parametersDataBase.addParameterSet(parameterSetId, parameterSet);
                }
            });
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    public class ParameterSet {

        public ParameterSet() {
            this.parameters = new HashMap<>();
        }

        public void addParameter(String name, String value) {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);

            parameters.put(name, value);
        }

        public String getParameter(String name) {
            return parameters.get(name);
        }

        private Map<String, String> parameters;
    }

    public DynawoParametersDataBase() {
        this.parameterSets = new HashMap<>();
    }

    public void addParameterSet(String parameterSetId, ParameterSet parameterSet) {
        parameterSets.put(parameterSetId, parameterSet);
    }

    public ParameterSet getParameterSet(String parameterSetId) {
        return parameterSets.get(parameterSetId);
    }

    private Map<String, ParameterSet> parameterSets;
}

