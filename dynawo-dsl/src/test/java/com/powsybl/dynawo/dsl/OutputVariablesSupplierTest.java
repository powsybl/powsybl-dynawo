/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynamicsimulation.OutputVariablesSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyOutputVariablesSupplier;
import com.powsybl.dynamicsimulation.groovy.OutputVariableGroovyExtension;
import com.powsybl.dynawo.outputvariables.DynawoOutputVariable;
import com.powsybl.dynawo.DynawoSimulationProvider;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
class OutputVariablesSupplierTest extends AbstractModelSupplierTest {

    private FileSystem fileSystem;
    private Network network;

    @BeforeEach
    void setup() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        network = EurostagTutorialExample1Factory.create();

        Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/outputVariables.groovy")), fileSystem.getPath("/outputVariables.groovy"));
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void test() {
        List<OutputVariableGroovyExtension> extensions = validateGroovyExtension();
        OutputVariablesSupplier supplier = new GroovyOutputVariablesSupplier(fileSystem.getPath("/outputVariables.groovy"), extensions);
        List<OutputVariable> outputVariables = supplier.get(network);
        assertEquals(11, outputVariables.size());
        outputVariables.forEach(this::validateOutputVariable);
    }

    private List<OutputVariableGroovyExtension> validateGroovyExtension() {
        List<OutputVariableGroovyExtension> extensions = GroovyExtension.find(OutputVariableGroovyExtension.class, DynawoSimulationProvider.NAME);
        assertEquals(1, extensions.size());
        assertInstanceOf(DynawoOutputVariableGroovyExtension.class, extensions.getFirst());
        return extensions;
    }

    private void validateOutputVariable(OutputVariable outputVariable) {
        assertEquals(DynawoOutputVariable.class, outputVariable.getClass());
        if (outputVariable.getModelId().equals("NETWORK")) {
            assertEquals(OutputVariable.OutputType.CURVE, outputVariable.getOutputType());
            assertTrue(Arrays.asList("NGEN_Upu_value", "NHV1_Upu_value", "NHV2_Upu_value", "NLOAD_Upu_value").contains(outputVariable.getVariableName()));
        } else if (network.getIdentifiable(outputVariable.getModelId()) instanceof Generator) {
            assertEquals(OutputVariable.OutputType.FINAL_STATE, outputVariable.getOutputType());
            assertTrue(Arrays.asList("generator_omegaPu", "generator_PGen", "generator_UStatorPU", "voltageRegulator_UcEfdP", "voltageRegulator_EfdPu").contains(outputVariable.getVariableName()));
        } else if (network.getIdentifiable(outputVariable.getModelId()) instanceof Load) {
            assertEquals(OutputVariable.OutputType.CURVE, outputVariable.getOutputType());
            assertTrue(Arrays.asList("load_PPu", "load_QPu").contains(outputVariable.getVariableName()));
        }
    }
}
