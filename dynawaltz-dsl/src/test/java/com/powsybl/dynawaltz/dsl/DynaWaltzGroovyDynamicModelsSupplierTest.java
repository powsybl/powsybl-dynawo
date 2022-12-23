/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dsl;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.dsl.models.buses.BusGroovyExtension;
import com.powsybl.dynawaltz.dsl.models.generators.GeneratorModelGroovyExtension;
import com.powsybl.dynawaltz.dsl.models.loads.LoadAlphaBetaGroovyExtension;
import com.powsybl.dynawaltz.dsl.models.loads.LoadOneTransformerGroovyExtension;
import com.powsybl.dynawaltz.models.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.models.automatons.CurrentLimitAutomaton;
import com.powsybl.dynawaltz.dsl.automatons.CurrentLimitAutomatonGroovyExtension;
import com.powsybl.dynawaltz.models.buses.StandardBus;
import com.powsybl.dynawaltz.models.generators.*;
import com.powsybl.dynawaltz.models.loads.LoadAlphaBeta;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformer;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynaWaltzGroovyDynamicModelsSupplierTest {

    private FileSystem fileSystem;
    private Network network;

    @Before
    public void setup() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        network = createEurostagTutorialExample1WithMoreGens();

        Files.copy(getClass().getResourceAsStream("/dynamicModels.groovy"), fileSystem.getPath("/dynamicModels.groovy"));
        Files.copy(getClass().getResourceAsStream("/models.par"), fileSystem.getPath("/models.par"));
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void test() {

        List<DynamicModelGroovyExtension> extensions = GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME);
        assertEquals(7, extensions.size());
        extensions.forEach(this::validateExtension);

        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(fileSystem.getPath("/dynamicModels.groovy"), extensions);

        List<DynamicModel> dynamicModels = supplier.get(network);
        // One model for each load, one model for each generator and one omegaRef per generator
        int numLoads = network.getLoadCount();
        int numGenerators = network.getGeneratorCount();
        int numLines = network.getLineCount();
        long numBuses = network.getBusBreakerView().getBusStream().count();
        int expectedDynamicModelsSize = numLoads + numGenerators + numLines + (int) numBuses;
        assertEquals(expectedDynamicModelsSize, dynamicModels.size());
        dynamicModels.forEach(this::validateModel);
    }

    private static Network createEurostagTutorialExample1WithMoreGens() {
        Network network = EurostagTutorialExample1Factory.create(NetworkFactory.findDefault());

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
            .setTargetP(-1.3)
            .setTargetQ(0.9)
            .add();
        vlgen.newGenerator()
            .setId("GEN6")
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
            .setId("GEN7")
            .setBus(ngen.getId())
            .setConnectableBus(ngen.getId())
            .setMinP(-9999.99)
            .setMaxP(9999.99)
            .setVoltageRegulatorOn(true)
            .setTargetV(24.5)
            .setTargetP(-1.3)
            .setTargetQ(0.9)
            .add();
        VoltageLevel vlload  = network.getVoltageLevel("VLLOAD");
        Bus nload = vlload.getBusBreakerView().getBus("NLOAD");
        vlload.newLoad()
            .setId("LOAD2")
            .setBus(nload.getId())
            .setConnectableBus(nload.getId())
            .setP0(1.0)
            .setQ0(0.5)
            .add();
        return network;
    }

    private void validateExtension(DynamicModelGroovyExtension extension) {
        boolean isLoadAlphaBetaExtension = extension instanceof LoadAlphaBetaGroovyExtension;
        boolean isLoadOneTransformerExtension = extension instanceof LoadOneTransformerGroovyExtension;

        boolean isLoadExtension = isLoadAlphaBetaExtension || isLoadOneTransformerExtension;
        boolean isGeneratorExtension = extension instanceof GeneratorModelGroovyExtension;
        boolean isBusExtension = extension instanceof BusGroovyExtension;
        boolean isDynamicModelExtension = isLoadExtension || isGeneratorExtension || isBusExtension;

        boolean isCurrentLimitAutomatonExtension = extension instanceof CurrentLimitAutomatonGroovyExtension;
        boolean isAutomatonExtension = isCurrentLimitAutomatonExtension;

        assertTrue(isDynamicModelExtension || isAutomatonExtension);
    }

    private void validateModel(DynamicModel dynamicModel) {
        assertTrue(dynamicModel instanceof AbstractBlackBoxModel);
        AbstractBlackBoxModel blackBoxModel = (AbstractBlackBoxModel) dynamicModel;
        if (blackBoxModel instanceof LoadAlphaBeta) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId().orElse(null));
            assertEquals(identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("LAB", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Load);
        } else if (blackBoxModel instanceof LoadOneTransformer) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId().orElse(null));
            assertEquals(identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("LOT", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Load);
        } else if (blackBoxModel instanceof GeneratorSynchronous) {
            validateGeneratorSynchronous((GeneratorSynchronous) blackBoxModel);
        } else if (blackBoxModel instanceof GeneratorFictitious) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId().orElse(null));
            assertEquals("BBM_" + identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("GF", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Generator);
        } else if (blackBoxModel instanceof GeneratorPQ) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId().orElse(null));
            assertEquals("BBM_" + identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("GPQ", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Generator);
        } else if (blackBoxModel instanceof CurrentLimitAutomaton) {
            Identifiable<?> identifiable = network.getIdentifiable(((CurrentLimitAutomaton) blackBoxModel).getLineStaticId());
            assertEquals("BBM_" + identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("CLA", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Line);
        } else if (blackBoxModel instanceof StandardBus) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId().orElse(null));
            assertEquals("BBM_" + identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("SB", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Bus);
        }
    }

    private void validateGeneratorSynchronous(GeneratorSynchronous generatorSynchronous) {
        if (generatorSynchronous.getLib().equals("GeneratorSynchronousThreeWindingsProportionalRegulations")) {
            Identifiable<?> identifiable = network.getIdentifiable(generatorSynchronous.getStaticId().orElse(null));
            assertEquals("BBM_" + identifiable.getId(), generatorSynchronous.getDynamicModelId());
            assertEquals("GSTWPR", generatorSynchronous.getParameterSetId());
            assertTrue(identifiable instanceof Generator);
        } else if (generatorSynchronous.getLib().equals("GeneratorSynchronousFourWindingsProportionalRegulations")) {
            Identifiable<?> identifiable = network.getIdentifiable(generatorSynchronous.getStaticId().orElse(null));
            assertEquals("BBM_" + identifiable.getId(), generatorSynchronous.getDynamicModelId());
            assertEquals("GSFWPR", generatorSynchronous.getParameterSetId());
            assertTrue(identifiable instanceof Generator);
        } else if (generatorSynchronous.getLib().equals("GeneratorSynchronousFourWindingsProportionalRegulationsStepPm")) {
            Identifiable<?> identifiable = network.getIdentifiable(generatorSynchronous.getStaticId().orElse(null));
            assertEquals("BBM_" + identifiable.getId(), generatorSynchronous.getDynamicModelId());
            assertEquals("GSFWPRSP", generatorSynchronous.getParameterSetId());
            assertTrue(identifiable instanceof Generator);
        } else if (generatorSynchronous.getLib().equals("GeneratorSynchronousThreeWindings")) {
            Identifiable<?> identifiable = network.getIdentifiable(generatorSynchronous.getStaticId().orElse(null));
            assertEquals("BBM_" + identifiable.getId(), generatorSynchronous.getDynamicModelId());
            assertEquals("GSTW", generatorSynchronous.getParameterSetId());
        } else if (generatorSynchronous.getLib().equals("GeneratorSynchronousFourWindings")) {
            Identifiable<?> identifiable = network.getIdentifiable(generatorSynchronous.getStaticId().orElse(null));
            assertEquals("BBM_" + identifiable.getId(), generatorSynchronous.getDynamicModelId());
            assertEquals("GSFW", generatorSynchronous.getParameterSetId());
            assertTrue(identifiable instanceof Generator);
        }
    }
}
