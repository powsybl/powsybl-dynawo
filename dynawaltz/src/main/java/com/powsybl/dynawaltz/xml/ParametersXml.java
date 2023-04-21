/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.parameters.*;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.parameters.Set;

import javax.xml.XMLConstants;
import javax.xml.stream.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class ParametersXml {

    private ParametersXml() {
    }

    public static Map<String, Set> load(InputStream parametersFile) {
        Map<String, Set> setsMap = new HashMap<>();
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            XMLStreamReader xmlReader = factory.createXMLStreamReader(parametersFile);
            read(xmlReader, setsMap);
            xmlReader.close();
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
        return setsMap;
    }

    public static Map<String, Set> load(Path parametersFile) {
        Map<String, Set> setsMap = new HashMap<>();
        try (Reader reader = Files.newBufferedReader(parametersFile, StandardCharsets.UTF_8)) {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            XMLStreamReader xmlReader = factory.createXMLStreamReader(reader);
            read(xmlReader, setsMap);
            xmlReader.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
        return setsMap;
    }

    private static void read(XMLStreamReader xmlReader, Map<String, Set> parametersSets) throws XMLStreamException {
        int state = xmlReader.next();
        while (state == XMLStreamConstants.COMMENT) {
            state = xmlReader.next();
        }
        com.powsybl.commons.xml.XmlUtil.readUntilEndElement("parametersSet", xmlReader, () -> {
            if (xmlReader.getLocalName().equals("set")) {
                String parameterSetId = xmlReader.getAttributeValue(null, "id");
                Set set = new Set(parameterSetId);
                com.powsybl.commons.xml.XmlUtil.readUntilEndElement("set", xmlReader, () -> {
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
                parametersSets.put(parameterSetId, set);
            } else {
                throw new PowsyblException("Unexpected element: " + xmlReader.getLocalName());
            }
        });
    }

    public static void write(Path workingDir, DynaWaltzContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);

        DynaWaltzParameters parameters = context.getDynaWaltzParameters();
        write(parameters.getModelParameters(), DynaWaltzParameters.MODELS_OUTPUT_PARAMETERS_FILE, workingDir);
        write(List.of(parameters.getNetworkParameters()), DynaWaltzParameters.NETWORK_OUTPUT_PARAMETERS_FILE, workingDir);
        write(List.of(parameters.getSolverParameters()), DynaWaltzParameters.SOLVER_OUTPUT_PARAMETERS_FILE, workingDir);

        // Write parameterSet that needs to be generated (OmegaRef...)
        Path file = workingDir.resolve(context.getSimulationParFile());
        XmlUtil.write(file, context, "parametersSet", ParametersXml::write);
    }

    private static void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        for (BlackBoxModel model : context.getBlackBoxModels()) {
            model.writeParameters(writer, context);
        }
    }

    private static void write(Collection<Set> parametersSets, String filename, Path workingDir) throws IOException, XMLStreamException {
        Path parametersPath = workingDir.resolve(filename);
        try (Writer writer = Files.newBufferedWriter(parametersPath, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = XmlStreamWriterFactory.newInstance(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix("", DYN_URI);
                xmlWriter.writeStartElement(DYN_URI, "parametersSet");
                xmlWriter.writeNamespace("", DYN_URI);
                for (Set set : parametersSets) {
                    writeSet(xmlWriter, set);
                }
                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }

    private static void writeSet(XMLStreamWriter xmlWriter, Set set) throws XMLStreamException {
        xmlWriter.writeStartElement(DYN_URI, "set");
        xmlWriter.writeAttribute("id", set.getId());
        for (Parameter par : set.getParameters().values()) {
            ParametersXml.writeParameter(xmlWriter, par.getType(), par.getName(), par.getValue());
        }
        for (Reference par : set.getReferences()) {
            ParametersXml.writeReference(xmlWriter, par.getType(), par.getName(), par.getOrigData(), par.getOrigName());
        }
        xmlWriter.writeEndElement();
    }

    public static void writeParameter(XMLStreamWriter writer, ParameterType type, String name, String value) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "par");
        writer.writeAttribute("type", type.toString());
        writer.writeAttribute("name", name);
        writer.writeAttribute("value", value);
    }

    public static void writeReference(XMLStreamWriter writer, ParameterType type, String name, String origData, String origName) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "reference");
        writer.writeAttribute("type", type.toString());
        writer.writeAttribute("name", name);
        writer.writeAttribute("origData", origData);
        writer.writeAttribute("origName", origName);
    }
}
