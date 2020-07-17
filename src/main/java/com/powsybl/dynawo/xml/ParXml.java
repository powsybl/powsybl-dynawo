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

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynawo.simulator.DynawoParametersDataBase;
import com.powsybl.dynawo.simulator.DynawoParametersDataBase.ParameterSet;
import com.powsybl.dynawo.simulator.DynawoParametersDataBase.ParameterType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class ParXml {

    private ParXml() {
    }

    public static DynawoParametersDataBase read(Path parametersFile) {
        DynawoParametersDataBase parametersDataBase = new DynawoParametersDataBase();
        if (Files.exists(parametersFile)) {
            try (Reader reader = Files.newBufferedReader(parametersFile, StandardCharsets.UTF_8)) {
                XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(reader);
                try {
                    read(xmlReader, parametersDataBase);
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

    private static void read(XMLStreamReader xmlReader, DynawoParametersDataBase parametersDataBase) {
        try {
            XmlUtil.readUntilEndElement("parametersSet", xmlReader, () -> {
                if (xmlReader.getLocalName().equals("set")) {
                    String parameterSetId = xmlReader.getAttributeValue(null, "id");
                    ParameterSet parameterSet = parametersDataBase.new ParameterSet();
                    XmlUtil.readUntilEndElement("set", xmlReader, () -> {
                        String name = xmlReader.getAttributeValue(null, "name");
                        ParameterType type = ParameterType.valueOf(xmlReader.getAttributeValue(null, "type"));
                        if (xmlReader.getLocalName().equals("par")) {
                            String value = xmlReader.getAttributeValue(null, "value");
                            parameterSet.addParameter(name, type, value);
                        } else if (xmlReader.getLocalName().equals("reference")) {
                            String origData = xmlReader.getAttributeValue(null, "origData");
                            String origName = xmlReader.getAttributeValue(null, "origName");
                            parameterSet.addReference(name, type, origData, origName);
                        }
                    });
                    parametersDataBase.addParameterSet(parameterSetId, parameterSet);
                }
            });
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
