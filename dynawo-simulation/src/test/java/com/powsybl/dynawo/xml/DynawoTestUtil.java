/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.outputvariables.DynawoOutputVariablesBuilder;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawo.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.dynawo.models.loads.LoadOneTransformerBuilder;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.BeforeEach;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.powsybl.commons.test.ComparisonUtils.assertTxtEquals;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class DynawoTestUtil extends AbstractSerDeTest {

    protected Network network;
    protected List<BlackBoxModel> dynamicModels;
    protected List<BlackBoxModel> eventModels;
    protected List<OutputVariable> outputVariables;
    protected Map<String, String> versions;

    @BeforeEach
    void setup() {

        network = createEurostagTutorialExample1WithMoreLoads();

        outputVariables = new ArrayList<>();

        network.getLoadStream().forEach(b -> new DynawoOutputVariablesBuilder()
                .staticId(b.getId())
                .variables("load_PPu", "load_QPu")
                .outputType(OutputVariable.OutputType.FINAL_STATE)
                .add(outputVariables::add));

        network.getBusBreakerView().getBusStream().forEach(b -> new DynawoOutputVariablesBuilder()
                .staticId(b.getId())
                .variables("Upu_value")
                .outputType(OutputVariable.OutputType.CURVE)
                .add(outputVariables::add));

        // A curve is made up of the id of the dynamic model and the variable to plot.
        // The static id of the generator is used as the id of the dynamic model (dynamicModelId).
        network.getGeneratorStream().forEach(g -> new DynawoOutputVariablesBuilder()
                .dynamicModelId(g.getId())
                .variables("generator_omegaPu", "generator_PGen", "generator_UStatorPu", "voltageRegulator_UcEfdP", "voltageRegulator_EfdPu")
                .outputType(OutputVariable.OutputType.CURVE)
                .add(outputVariables::add));

        // Dynamic Models
        dynamicModels = new ArrayList<>();
        network.getLoadStream().forEach(l -> {
            if (l.getId().equals("LOAD2")) {
                dynamicModels.add(LoadOneTransformerBuilder.of(network, "LoadOneTransformer")
                        .equipment(l)
                        .parameterSetId("LOT")
                        .build());
            } else {
                dynamicModels.add(BaseLoadBuilder.of(network, "LoadAlphaBeta")
                        .equipment(l)
                        .parameterSetId("LAB")
                        .build());
            }
        });
        network.getGeneratorStream().forEach(g -> {
            if (g.getId().equals("GEN2")) {
                dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsProportionalRegulations")
                        .equipment(g)
                        .parameterSetId("GSFWPR")
                        .build());
            } else if (g.getId().equals("GEN3")) {
                dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindings")
                        .equipment(g)
                        .parameterSetId("GSFW")
                        .build());
            } else if (g.getId().equals("GEN4")) {
                dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousThreeWindings")
                        .equipment(g)
                        .parameterSetId("GSTW")
                        .build());
            } else if (g.getId().equals("GEN6")) {
                dynamicModels.add(BaseGeneratorBuilder.of(network)
                        .equipment(g)
                        .parameterSetId("GF")
                        .build());
            } else if (g.getId().equals("GEN7")) {
                dynamicModels.add(SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                        .equipment(g)
                        .parameterSetId("GPQ")
                        .build());
            } else {
                dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousThreeWindingsProportionalRegulations")
                        .equipment(g)
                        .parameterSetId("GSTWPR")
                        .build());
            }
        });

        // Events
        eventModels = new ArrayList<>();
        network.getLineStream().forEach(l -> eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId(l.getId())
                .startTime(5)
                .disconnectOnly(TwoSides.TWO)
                .build()));
        eventModels.add(EventDisconnectionBuilder.of(network)
                .staticId("GEN2")
                .startTime(1)
                .build());

        // Automatons
        network.getLineStream().filter(line -> !line.getId().equalsIgnoreCase("NHV1_NHV2_1"))
                .forEach(l -> dynamicModels.add(DynamicOverloadManagementSystemBuilder.of(network, "OverloadManagementSystem")
                        .dynamicModelId("CLA_" + l.getId())
                        .parameterSetId("CLA")
                        .controlledBranch(l.getId())
                        .iMeasurement(l.getId())
                        .iMeasurementSide(TwoSides.ONE)
                        .build()));
    }

    public void validate(String schemaDefinition, String expectedResourceName, Path xmlFile) throws SAXException, IOException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Source xml = new StreamSource(Files.newInputStream(xmlFile));
        Source xsd = new StreamSource(getClass().getResourceAsStream("/" + schemaDefinition));
        Schema schema = factory.newSchema(xsd);
        Validator validator = schema.newValidator();
        validator.validate(xml);
        assertTxtEquals(Objects.requireNonNull(getClass().getResourceAsStream("/" + expectedResourceName)), Files.newInputStream(xmlFile));
    }

    private static Network createEurostagTutorialExample1WithMoreLoads() {
        Network network = EurostagTutorialExample1Factory.createWithLFResults();

        VoltageLevel vlload = network.getVoltageLevel("VLLOAD");
        Bus nload = vlload.getBusBreakerView().getBus("NLOAD");
        vlload.newLoad()
            .setId("LOAD2")
            .setBus(nload.getId())
            .setConnectableBus(nload.getId())
            .setP0(1.0)
            .setQ0(0.5)
            .add();
        VoltageLevel vlgen = network.getVoltageLevel("VLGEN");
        Bus ngen = vlgen.getBusBreakerView().getBus("NGEN");
        vlgen.newGenerator()
            .setId("GEN2")
            .setBus(ngen.getId())
            .setConnectableBus(ngen.getId())
            .setMinP(-9999.99)
            .setMaxP(9999.99)
            .setVoltageRegulatorOn(true)
            .setTargetV(24.5)
            .setTargetP(1.0)
            .setTargetQ(0.5)
            .add();
        vlgen.newGenerator()
            .setId("GEN3")
            .setBus(ngen.getId())
            .setConnectableBus(ngen.getId())
            .setMinP(-9999.99)
            .setMaxP(9999.99)
            .setVoltageRegulatorOn(true)
            .setTargetV(24.5)
            .setTargetP(0.1)
            .setTargetQ(0.2)
            .add();
        vlgen.newGenerator()
            .setId("GEN4")
            .setBus(ngen.getId())
            .setConnectableBus(ngen.getId())
            .setMinP(-9999.99)
            .setMaxP(9999.99)
            .setVoltageRegulatorOn(true)
            .setTargetV(24.5)
            .setTargetP(-1.3)
            .setTargetQ(0.9)
            .add();
        vlgen.newGenerator()
            .setId("GEN5")
            .setBus(ngen.getId())
            .setConnectableBus(ngen.getId())
            .setMinP(-9999.99)
            .setMaxP(9999.99)
            .setVoltageRegulatorOn(true)
            .setTargetV(24.5)
            .setTargetP(-0.3)
            .setTargetQ(0.7)
            .add();
        vlgen.newGenerator()
            .setId("GEN6")
            .setBus(ngen.getId())
            .setConnectableBus(ngen.getId())
            .setMinP(-9999.99)
            .setMaxP(9999.99)
            .setVoltageRegulatorOn(true)
            .setTargetV(24.5)
            .setTargetP(-0.3)
            .setTargetQ(0.7)
            .add();
        vlgen.newGenerator()
            .setId("GEN7")
            .setBus(ngen.getId())
            .setConnectableBus(ngen.getId())
            .setMinP(-9999.99)
            .setMaxP(9999.99)
            .setVoltageRegulatorOn(true)
            .setTargetV(24.5)
            .setTargetP(-0.3)
            .setTargetQ(0.7)
            .add();
        VoltageLevel vlhv1 = network.getVoltageLevel("VLHV1");
        Bus nhv1 = vlhv1.getBusBreakerView().getBus("NHV1");
        vlhv1.newGenerator()
            .setId("NHV1_1")
            .setBus(nhv1.getId())
            .setConnectableBus(nhv1.getId())
            .setMinP(-9999.99)
            .setMaxP(9999.99)
            .setVoltageRegulatorOn(true)
            .setTargetV(24.5)
            .setTargetP(0.1)
            .setTargetQ(0.2)
            .add();
        return network;
    }
}
