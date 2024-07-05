/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dsl;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.CurvesSupplier;
import com.powsybl.dynamicsimulation.groovy.CurveGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyCurvesSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawo.curves.DynawoCurve;
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
class CurvesSupplierTest extends AbstractModelSupplierTest {

    private FileSystem fileSystem;
    private Network network;

    @BeforeEach
    void setup() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        network = EurostagTutorialExample1Factory.create();

        Files.copy(Objects.requireNonNull(getClass().getResourceAsStream("/curves.groovy")), fileSystem.getPath("/curves.groovy"));
    }

    @AfterEach
    void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    void test() {
        List<CurveGroovyExtension> extensions = validateGroovyExtension();
        CurvesSupplier supplier = new GroovyCurvesSupplier(fileSystem.getPath("/curves.groovy"), extensions);
        List<Curve> curves = supplier.get(network);
        assertEquals(11, curves.size());
        curves.forEach(this::validateCurve);
    }

    private List<CurveGroovyExtension> validateGroovyExtension() {
        List<CurveGroovyExtension> extensions = GroovyExtension.find(CurveGroovyExtension.class, DynawoSimulationProvider.NAME);
        assertEquals(1, extensions.size());
        assertInstanceOf(DynawoCurveGroovyExtension.class, extensions.get(0));
        return extensions;
    }

    private void validateCurve(Curve curve) {
        assertEquals(DynawoCurve.class, curve.getClass());
        DynawoCurve curveImpl = (DynawoCurve) curve;
        if (curveImpl.getModelId().equals("NETWORK")) {
            assertTrue(Arrays.asList("NGEN_Upu_value", "NHV1_Upu_value", "NHV2_Upu_value", "NLOAD_Upu_value").contains(curveImpl.getVariable()));
        } else if (network.getIdentifiable(curveImpl.getModelId()) instanceof Generator) {
            assertTrue(Arrays.asList("generator_omegaPu", "generator_PGen", "generator_UStatorPU", "voltageRegulator_UcEfdP", "voltageRegulator_EfdPu").contains(curveImpl.getVariable()));
        } else if (network.getIdentifiable(curveImpl.getModelId()) instanceof Load) {
            assertTrue(Arrays.asList("load_PPu", "load_QPu").contains(curveImpl.getVariable()));
        }
    }
}
