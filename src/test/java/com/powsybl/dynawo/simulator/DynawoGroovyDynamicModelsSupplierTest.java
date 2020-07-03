/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawo.dyd.AbstractBlackBoxModel;
import com.powsybl.dynawo.dyd.DYNModelOmegaRef;
import com.powsybl.dynawo.dyd.GeneratorSynchronousFourWindingsProportionalRegulations;
import com.powsybl.dynawo.dyd.LoadAlphaBeta;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Identifiable;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoGroovyDynamicModelsSupplierTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private FileSystem fileSystem;
    private Network network;

    @Before
    public void setup() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        network = EurostagTutorialExample1Factory.create();

        Files.copy(getClass().getResourceAsStream("/dynamicModels.groovy"), fileSystem.getPath("/dynamicModels.groovy"));
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void test() {

        List<DynamicModelGroovyExtension> extensions = GroovyExtension.find(DynamicModelGroovyExtension.class, "dynawo");
        assertEquals(3, extensions.size());
        assertTrue(validateExtension(extensions.get(0)));
        assertTrue(validateExtension(extensions.get(1)));
        assertTrue(validateExtension(extensions.get(2)));

        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(fileSystem.getPath("/dynamicModels.groovy"), extensions);

        List<DynamicModel> dynamicModels = supplier.get(network);
        // One model for each load, one model for each generator and one omegaRef per generator
        int numLoads = network.getLoadCount();
        int numGenerators = network.getGeneratorCount();
        int numOmegaRefs = numGenerators;
        int expectedDynamicModelsSize =  numLoads + numGenerators + numOmegaRefs;
        assertEquals(expectedDynamicModelsSize, dynamicModels.size());
        dynamicModels.forEach(this::validateModel);
    }

    private boolean validateExtension(DynamicModelGroovyExtension extension) {
        boolean isLoadExtension = extension instanceof LoadAlphaBetaGroovyExtension;
        boolean isGeneratorExtension = extension instanceof GeneratorSynchronousFourWindingsProportionalRegulationsGroovyExtension;
        boolean isOmegaRefExtension = extension instanceof DYNModelOmegaRefGroovyExtension;

        return isLoadExtension || isGeneratorExtension || isOmegaRefExtension;
    }

    private void validateModel(DynamicModel dynamicModel) {
        assertTrue(dynamicModel instanceof AbstractBlackBoxModel);
        AbstractBlackBoxModel blackBoxModel = (AbstractBlackBoxModel) dynamicModel;
        if (blackBoxModel instanceof LoadAlphaBeta) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId());
            assertEquals(identifiable.getId(), blackBoxModel.getId());
            assertEquals("default", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Load);
        } else if (blackBoxModel instanceof GeneratorSynchronousFourWindingsProportionalRegulations) {
            Identifiable<?> identifiable = network.getIdentifiable(blackBoxModel.getStaticId());
            assertEquals("BBM_" + identifiable.getId(), blackBoxModel.getId());
            assertEquals("default", blackBoxModel.getParameterSetId());
            assertTrue(identifiable instanceof Generator);
        } else if (blackBoxModel instanceof DYNModelOmegaRef) {
            assertEquals("OMEGA_REF", blackBoxModel.getId());
            assertEquals("", blackBoxModel.getStaticId());
            assertEquals("OMEGA_REF", blackBoxModel.getParameterSetId());
        }
    }
}
