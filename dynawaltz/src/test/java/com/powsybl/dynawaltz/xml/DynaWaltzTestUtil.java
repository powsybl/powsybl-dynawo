/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.test.AbstractSerDeTest;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynawaltz.DynaWaltzCurve;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawaltz.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawaltz.models.generators.GeneratorFictitiousBuilder;
import com.powsybl.dynawaltz.models.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawaltz.models.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawaltz.models.lines.LineBuilder;
import com.powsybl.dynawaltz.models.lines.StandardLine;
import com.powsybl.dynawaltz.models.loads.BaseLoadBuilder;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerBuilder;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.powsybl.commons.test.ComparisonUtils.assertTxtEquals;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class DynaWaltzTestUtil extends AbstractSerDeTest {

    protected Network network;
    protected List<BlackBoxModel> dynamicModels;
    protected List<BlackBoxModel> eventModels;
    protected List<Curve> curves;

    @BeforeEach
    void setup() {

        network = createEurostagTutorialExample1WithMoreLoads();

        curves = new ArrayList<>();
        network.getBusBreakerView().getBusStream().forEach(b -> curves.add(new DynaWaltzCurve("NETWORK", b.getId() + "_Upu_value")));

        // A curve is made up of the id of the dynamic model and the variable to plot.
        // The static id of the generator is used as the id of the dynamic model (dynamicModelId).
        network.getGeneratorStream().forEach(g -> {
            curves.add(new DynaWaltzCurve(g.getId(), "generator_omegaPu"));
            curves.add(new DynaWaltzCurve(g.getId(), "generator_PGen"));
            curves.add(new DynaWaltzCurve(g.getId(), "generator_UStatorPu"));
            curves.add(new DynaWaltzCurve(g.getId(), "voltageRegulator_UcEfdP"));
            curves.add(new DynaWaltzCurve(g.getId(), "voltageRegulator_EfdPu"));
        });

        // Dynamic Models
        dynamicModels = new ArrayList<>();
        network.getLoadStream().forEach(l -> {
            if (l.getId().equals("LOAD2")) {
                dynamicModels.add(LoadOneTransformerBuilder.of(network, "LoadOneTransformer")
                        .dynamicModelId("BBM_" + l.getId())
                        .staticId(l.getId())
                        .parameterSetId("LOT")
                        .build());
            } else {
                dynamicModels.add(BaseLoadBuilder.of(network, "LoadAlphaBeta")
                        .dynamicModelId("BBM_" + l.getId())
                        .staticId(l.getId())
                        .parameterSetId("LAB")
                        .build());
            }
        });
        network.getGeneratorStream().forEach(g -> {
            if (g.getId().equals("GEN2")) {
                dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindingsProportionalRegulations")
                        .dynamicModelId("BBM_" + g.getId())
                        .staticId(g.getId())
                        .parameterSetId("GSFWPR")
                        .build());
            } else if (g.getId().equals("GEN3")) {
                dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousFourWindings")
                        .dynamicModelId("BBM_" + g.getId())
                        .staticId(g.getId())
                        .parameterSetId("GSFW")
                        .build());
            } else if (g.getId().equals("GEN4")) {
                dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousThreeWindings")
                        .dynamicModelId("BBM_" + g.getId())
                        .staticId(g.getId())
                        .parameterSetId("GSTW")
                        .build());
            } else if (g.getId().equals("GEN6")) {
                dynamicModels.add(GeneratorFictitiousBuilder.of(network)
                        .dynamicModelId("BBM_" + g.getId())
                        .staticId(g.getId())
                        .parameterSetId("GF")
                        .build());
            } else if (g.getId().equals("GEN7")) {
                dynamicModels.add(SynchronizedGeneratorBuilder.of(network, "GeneratorPQ")
                        .dynamicModelId("BBM_" + g.getId())
                        .staticId(g.getId())
                        .parameterSetId("GPQ")
                        .build());
            } else {
                dynamicModels.add(SynchronousGeneratorBuilder.of(network, "GeneratorSynchronousThreeWindingsProportionalRegulations")
                        .dynamicModelId("BBM_" + g.getId())
                        .staticId(g.getId())
                        .parameterSetId("GSTWPR")
                        .build());
            }
        });

        StandardLine standardLine = LineBuilder.of(network)
                .dynamicModelId("Line_NHV1_NHV2_1")
                .staticId("NHV1_NHV2_1")
                .parameterSetId("SL")
                .build();
        dynamicModels.add(standardLine);

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
        network.getLineStream().filter(line -> !line.getId().equalsIgnoreCase(standardLine.getStaticId()))
                .forEach(l -> dynamicModels.add(DynamicOverloadManagementSystemBuilder.of(network, "OverloadManagementSystem")
                        .dynamicModelId("BBM_" + l.getId())
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
        Network network = EurostagTutorialExample1Factory.create(NetworkFactory.findDefault());

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
