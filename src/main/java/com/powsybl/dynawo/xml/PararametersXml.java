/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.xml;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.commons.xml.XmlUtil;
import com.powsybl.dynawo.simulator.DynawoParametersDatabase;
import com.powsybl.dynawo.simulator.DynawoParametersDatabase.ParameterSet;
import com.powsybl.dynawo.simulator.DynawoParametersDatabase.ParameterType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class PararametersXml {

    private PararametersXml() {
    }

    public static DynawoParametersDatabase read(Path parametersFile) throws IOException {
        DynawoParametersDatabase parametersDatabase = new DynawoParametersDatabase();
        if (Files.exists(parametersFile)) {
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
        } else {
            throw new IOException(parametersFile + " not found");
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
                    ParameterSet parameterSet = parametersDatabase.new ParameterSet();
                    XmlUtil.readUntilEndElement("set", xmlReader, () -> {
                        String name = xmlReader.getAttributeValue(null, "name");
                        ParameterType type = ParameterType.valueOf(xmlReader.getAttributeValue(null, "type"));
                        if (xmlReader.getLocalName().equals("par")) {
                            String value = xmlReader.getAttributeValue(null, "value");
                            parameterSet.addParameter(name, type, value);
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
}
