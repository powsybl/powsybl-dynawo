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
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Before;
import org.xml.sax.SAXException;

import com.powsybl.commons.AbstractConverterTest;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.dyd.DYNModelOmegaRef;
import com.powsybl.dynawo.dyd.GeneratorSynchronousFourWindingsProportionalRegulations;
import com.powsybl.dynawo.dyd.LoadAlphaBeta;
import com.powsybl.dynawo.simulator.DynawoCurve;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoTestUtil extends AbstractConverterTest {

    protected Network network;
    protected List<DynamicModel> dynamicModels;
    protected List<Curve> curves;

    @Before
    public void setup() throws IOException {

        network = createEurostagTutorialExample1WithMoreLoadsAndGenerators();

        curves = new ArrayList<>();
        network.getBusBreakerView().getBusStream().forEach(b -> curves.add(new DynawoCurve("NETWORK", b.getId() + "_Upu_value")));
        // A curve is made up of the id of the dynamic model and the variable to plot.
        // The static id of the generator is used as the id of the dynamic model (modelId).
        network.getGeneratorStream().forEach(g -> {
            curves.add(new DynawoCurve(g.getId(), "generator_omegaPu"));
            curves.add(new DynawoCurve(g.getId(), "generator_PGen"));
            curves.add(new DynawoCurve(g.getId(), "generator_UStatorPu"));
            curves.add(new DynawoCurve(g.getId(), "voltageRegulator_UcEfdP"));
            curves.add(new DynawoCurve(g.getId(), "voltageRegulator_EfdPu"));
        });

        dynamicModels = new ArrayList<>();
        List<String> generators = new ArrayList<>();
        network.getLoadStream().forEach(l -> {
            dynamicModels.add(new LoadAlphaBeta("BBM_" + l.getId(), l.getId(), "default"));
        });
        network.getGeneratorStream().forEach(g -> {
            dynamicModels.add(new GeneratorSynchronousFourWindingsProportionalRegulations("BBM_" + g.getId(), g.getId(), "default"));
            generators.add("BBM_" + g.getId());
        });
        dynamicModels.add(new DYNModelOmegaRef("OMEGA_REF", generators));
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

    private static Network createEurostagTutorialExample1WithMoreLoadsAndGenerators() {
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
        VoltageLevel vlgen  = network.getVoltageLevel("VLGEN");
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
        return network;
    }
}
