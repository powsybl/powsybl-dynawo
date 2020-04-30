/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import com.powsybl.commons.AbstractConverterTest;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoTestUtil extends AbstractConverterTest {

    public void validate(Path xmlFile, String name) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source xml = new StreamSource(Files.newInputStream(xmlFile));
        Source xsd = new StreamSource(getClass().getResourceAsStream("/" + name + ".xsd"));
        Schema schema = factory.newSchema(xsd);
        Validator validator = schema.newValidator();
        validator.validate(xml);
        compareXml(getClass().getResourceAsStream("/" + name + ".xml"), Files.newInputStream(xmlFile));
    }
}
