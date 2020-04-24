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

import org.junit.Before;
import org.xml.sax.SAXException;

import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.commons.config.InMemoryPlatformConfig;
import com.powsybl.commons.config.MapModuleConfig;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoTestUtil extends AbstractConverterTest {

    protected InMemoryPlatformConfig platformConfig;

    @Before
    public void setUpDynawo() throws IOException {
        int startTime = 1;
        int stopTime = 100;
        String parametersFile = "/home/user/parametersFile";
        String networkParametersFile = "/home/user/networkParametersFile";
        String networkParametersId = "1";
        SolverType solverType = SolverType.IDA;
        String solverParametersFile = "/home/user/solverParametersFile";
        String solverParametersId = "1";

        platformConfig = new InMemoryPlatformConfig(fileSystem);
        MapModuleConfig moduleConfig = platformConfig.createModuleConfig("dynamic-simulation-default-parameters");
        moduleConfig.setStringProperty("startTime", Integer.toString(startTime));
        moduleConfig.setStringProperty("stopTime", Integer.toString(stopTime));
        moduleConfig = platformConfig.createModuleConfig("dynawo-default-parameters");
        moduleConfig.setStringProperty("parametersFile", parametersFile);
        moduleConfig.setStringProperty("network.parametersFile", networkParametersFile);
        moduleConfig.setStringProperty("network.ParametersId", networkParametersId);
        moduleConfig.setStringProperty("solver.type", solverType.toString());
        moduleConfig.setStringProperty("solver.parametersFile", solverParametersFile);
        moduleConfig.setStringProperty("solver.parametersId", solverParametersId);
    }

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
