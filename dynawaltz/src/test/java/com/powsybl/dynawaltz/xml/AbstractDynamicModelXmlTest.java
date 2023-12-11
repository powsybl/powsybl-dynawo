/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.reporter.ReporterModel;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.powsybl.commons.test.ComparisonUtils.compareTxt;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractDynamicModelXmlTest extends AbstractSerDeTest {

    protected Network network;
    protected List<BlackBoxModel> dynamicModels = new ArrayList<>();
    protected List<BlackBoxModel> eventModels = new ArrayList<>();
    protected List<Curve> curves = new ArrayList<>();
    protected DynaWaltzContext context;
    protected ReporterModel reporter = new ReporterModel("testDyd", "Test DYD");

    @BeforeEach
    void setup() {
        setupNetwork();
        addDynamicModels();
        setupDynawaltzContext();
    }

    @AfterEach
    void clear() {
        dynamicModels.clear();
        eventModels.clear();
        curves.clear();
    }

    public void validate(String schemaDefinition, String expectedResourceName, Path xmlFile) throws SAXException, IOException {
        InputStream expected = Objects.requireNonNull(getClass().getResourceAsStream("/" + expectedResourceName));
        InputStream actual = Files.newInputStream(xmlFile);
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source xml = new StreamSource(Files.newInputStream(xmlFile));
        Source xsd = new StreamSource(getClass().getResourceAsStream("/" + schemaDefinition));
        Schema schema = factory.newSchema(xsd);
        Validator validator = schema.newValidator();
        validator.validate(xml);
        compareTxt(expected, actual);
    }

    void setupDynawaltzContext() {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load();
        context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, curves, parameters, dynawoParameters, reporter);
    }

    protected abstract void setupNetwork();

    protected abstract void addDynamicModels();

    protected void checkReporter(String report) {
        StringWriter sw = new StringWriter();
        reporter.export(sw);
        assertEquals(report, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
