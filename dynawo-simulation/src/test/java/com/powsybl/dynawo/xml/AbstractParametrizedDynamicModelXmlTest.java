/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.commons.test.PowsyblTestReportResourceBundle;
import com.powsybl.commons.test.TestUtil;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.FinalStepConfig;
import com.powsybl.dynawo.commons.PowsyblDynawoReportResourceBundle;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;
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

import static com.powsybl.commons.test.ComparisonUtils.assertTxtEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractParametrizedDynamicModelXmlTest extends AbstractSerDeTest {

    protected Network network;
    protected List<BlackBoxModel> dynamicModels = new ArrayList<>();
    protected List<BlackBoxModel> eventModels = new ArrayList<>();
    protected List<OutputVariable> outputVariables = new ArrayList<>();
    protected DynawoSimulationContext context;
    protected ReportNode reportNode = ReportNode.newRootReportNode()
            .withResourceBundles(PowsyblDynawoReportResourceBundle.BASE_NAME,
                    PowsyblTestReportResourceBundle.TEST_BASE_NAME)
            .withMessageTemplate("testDyd")
            .build();

    public void validate(String schemaDefinition, String expectedResourceName, Path xmlFile) throws SAXException, IOException {
        InputStream expected = Objects.requireNonNull(getClass().getResourceAsStream("/" + expectedResourceName));
        InputStream actual = Files.newInputStream(xmlFile);
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source xml = new StreamSource(Files.newInputStream(xmlFile));
        Source xsd = new StreamSource(getClass().getResourceAsStream("/" + schemaDefinition));
        Schema schema = factory.newSchema(xsd);
        Validator validator = schema.newValidator();
        validator.validate(xml);
        assertTxtEquals(expected, actual);
    }

    void setupDynawoContext() {
        setupDynawoContext(null);
    }

    protected void setupDynawoContext(FinalStepConfig finalStepConfig) {
        DynawoSimulationContext.Builder builder = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .reportNode(reportNode);
        if (finalStepConfig != null) {
            builder.finalStepConfig(finalStepConfig);
        }
        context = builder.build();
    }

    protected void checkReport(String report) throws IOException {
        StringWriter sw = new StringWriter();
        reportNode.print(sw);
        assertEquals(report, TestUtil.normalizeLineSeparator(sw.toString()));
    }
}
