/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawo.automatons.CurrentLimitAutomaton;
import com.powsybl.dynawo.automatons.CurrentLimitAutomatonGroovyExtension;
import com.powsybl.dynawo.dynamicmodels.AbstractBlackBoxModel;
import com.powsybl.dynawo.dynamicmodels.GeneratorSynchronousFourWindingsProportionalRegulations;
import com.powsybl.dynawo.dynamicmodels.LoadAlphaBeta;
import com.powsybl.dynawo.dynamicmodels.OmegaRef;
import com.powsybl.dynawo.dynamicmodels.GeneratorSynchronousFourWindingsProportionalRegulationsGroovyExtension;
import com.powsybl.dynawo.dynamicmodels.GeneratorSynchronousThreeWindingsProportionalRegulations;
import com.powsybl.dynawo.dynamicmodels.GeneratorSynchronousThreeWindingsProportionalRegulationsGroovyExtension;
import com.powsybl.dynawo.dynamicmodels.LoadAlphaBetaGroovyExtension;
import com.powsybl.dynawo.dynamicmodels.OmegaRefGroovyExtension;
import com.powsybl.dynawo.xml.DynawoTestUtil;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.NetworkFactory;
import com.powsybl.iidm.network.VoltageLevel;
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
public class DynawoGroovyDynamicModelsSupplierTest {

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

        List<DynamicModelGroovyExtension> extensions = GroovyExtension.find(DynamicModelGroovyExtension.class, DynawoProvider.NAME);
        assertEquals(5, extensions.size());
        extensions.forEach(DynawoGroovyDynamicModelsSupplierTest::validateExtension);

        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(fileSystem.getPath("/dynamicModels.groovy"), extensions);

        List<DynamicModel> dynamicModels = supplier.get(network);
        // One model for each load, one model for each generator and one omegaRef per generator
        int numLoads = network.getLoadCount();
        int numGenerators = network.getGeneratorCount();
        int numOmegaRefs = numGenerators;
        int numLines = network.getLineCount();
        int expectedDynamicModelsSize =  numLoads + numGenerators + numOmegaRefs + numLines;
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
        return network;
    }

    private static boolean validateExtension(DynamicModelGroovyExtension extension) {
        boolean isLoadExtension = extension instanceof LoadAlphaBetaGroovyExtension;
        boolean isThreeWindingsGeneratorExtension = extension instanceof GeneratorSynchronousThreeWindingsProportionalRegulationsGroovyExtension;
        boolean isFourWindingsGeneratorExtension = extension instanceof GeneratorSynchronousFourWindingsProportionalRegulationsGroovyExtension;
        boolean isOmegaRefExtension = extension instanceof OmegaRefGroovyExtension;
        boolean isDynamicModelExtension = isLoadExtension || isThreeWindingsGeneratorExtension || isFourWindingsGeneratorExtension || isOmegaRefExtension;

        boolean isCurrentLimitAutomatonExtension = extension instanceof CurrentLimitAutomatonGroovyExtension;
        boolean isAutomatonExtension = isCurrentLimitAutomatonExtension;

        return isDynamicModelExtension || isAutomatonExtension;
    }

    private void validateModel(DynamicModel dynamicModel) {
        assertTrue(dynamicModel instanceof AbstractBlackBoxModel);
        AbstractBlackBoxModel blackBoxModel = (AbstractBlackBoxModel) dynamicModel;
        if (blackBoxModel instanceof LoadAlphaBeta) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId());
            assertEquals(identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("LAB", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Load);
        } else if (blackBoxModel instanceof GeneratorSynchronousThreeWindingsProportionalRegulations) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId());
            assertEquals("BBM_" + identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("GSTWPR", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Generator);
        } else if (blackBoxModel instanceof GeneratorSynchronousFourWindingsProportionalRegulations) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId());
            assertEquals("BBM_" + identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("GSFWPR", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Generator);
        } else if (blackBoxModel instanceof OmegaRef) {
            OmegaRef omegaRef = (OmegaRef) blackBoxModel;
            assertEquals("OMEGA_REF_" + omegaRef.getGeneratorDynamicModelId(), blackBoxModel.getDynamicModelId());
            DynawoTestUtil.assertThrows(UnsupportedOperationException.class, blackBoxModel::getStaticId);
            assertEquals("OMEGA_REF", blackBoxModel.getParameterSetId());
        } else if (blackBoxModel instanceof CurrentLimitAutomaton) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId());
            assertEquals("BBM_" + identifiable.getId(), blackBoxModel.getDynamicModelId());
            assertEquals("CLA", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Line);
        }
    }
}
