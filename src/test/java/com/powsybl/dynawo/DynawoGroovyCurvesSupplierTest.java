/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.powsybl.dsl.DslException;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynamicsimulation.CurvesSupplier;
import com.powsybl.dynamicsimulation.groovy.CurveGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyCurvesSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class DynawoGroovyCurvesSupplierTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private FileSystem fileSystem;
    private Network network;

    @Before
    public void setup() throws IOException {
        fileSystem = Jimfs.newFileSystem(Configuration.unix());
        network = EurostagTutorialExample1Factory.create();

        Files.copy(getClass().getResourceAsStream("/powsybl-inputs/curves.groovy"), fileSystem.getPath("/curves.groovy"));
        Files.copy(getClass().getResourceAsStream("/powsybl-inputs/curves_dynamicModelId_staticId.groovy"), fileSystem.getPath("/curves_dynamicModelId_staticId.groovy"));
        Files.copy(getClass().getResourceAsStream("/powsybl-inputs/curves_variable.groovy"), fileSystem.getPath("/curves_variable.groovy"));
        Files.copy(getClass().getResourceAsStream("/powsybl-inputs/curves_variables.groovy"), fileSystem.getPath("/curves_variables.groovy"));
    }

    @After
    public void tearDown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void test() {

        List<CurveGroovyExtension> extensions = GroovyExtension.find(CurveGroovyExtension.class, DynawoProvider.NAME);
        assertEquals(1, extensions.size());
        assertTrue(extensions.get(0) instanceof DynawoCurveGroovyExtension);

        CurvesSupplier supplier = new GroovyCurvesSupplier(fileSystem.getPath("/curves.groovy"), extensions);

        List<Curve> curves = supplier.get(network);
        assertEquals(11, curves.size());
        curves.forEach(this::validateCurve);
    }

    @Test
    public void testModelIdStaticIdDefined() {

        exception.expect(DslException.class);
        exception.expectMessage("Both staticId and dynamicModelId are defined");

        List<CurveGroovyExtension> extensions = GroovyExtension.find(CurveGroovyExtension.class, DynawoProvider.NAME);
        assertEquals(1, extensions.size());
        assertTrue(extensions.get(0) instanceof DynawoCurveGroovyExtension);

        CurvesSupplier supplier = new GroovyCurvesSupplier(fileSystem.getPath("/curves_dynamicModelId_staticId.groovy"), extensions);
        supplier.get(network);
    }

    @Test
    public void testVariableNotDefined() {

        exception.expect(DslException.class);
        exception.expectMessage("'variables' field is not set");

        List<CurveGroovyExtension> extensions = GroovyExtension.find(CurveGroovyExtension.class, DynawoProvider.NAME);
        assertEquals(1, extensions.size());
        assertTrue(extensions.get(0) instanceof DynawoCurveGroovyExtension);

        CurvesSupplier supplier = new GroovyCurvesSupplier(fileSystem.getPath("/curves_variable.groovy"), extensions);
        supplier.get(network);
    }

    @Test
    public void testVariablesNotDefined() {

        exception.expect(DslException.class);
        exception.expectMessage("'variables' field is not set");

        List<CurveGroovyExtension> extensions = GroovyExtension.find(CurveGroovyExtension.class, DynawoProvider.NAME);
        assertEquals(1, extensions.size());
        assertTrue(extensions.get(0) instanceof DynawoCurveGroovyExtension);

        CurvesSupplier supplier = new GroovyCurvesSupplier(fileSystem.getPath("/curves_variables.groovy"), extensions);
        supplier.get(network);
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
