/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.suppliers;

import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.outputvariables.DynawoOutputVariablesBuilder;
import com.powsybl.dynawo.suppliers.outputvariables.OutputVariablesJsonDeserializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoOutputVariablesJsonDeserializerTest {

    @Test
    void testCurvesSupplier() throws IOException {
        try (InputStream is = getClass().getResourceAsStream("/suppliers/outputVariables.json")) {
            List<OutputVariable> curves = new SupplierJsonDeserializer<>(new OutputVariablesJsonDeserializer()).deserialize(is);
            assertThat(curves).usingRecursiveFieldByFieldElementComparatorOnFields()
                    .containsExactlyInAnyOrderElementsOf(getExpectedCurves());
        }
    }

    private static List<OutputVariable> getExpectedCurves() {
        List<OutputVariable> curves = new ArrayList<>();
        new DynawoOutputVariablesBuilder().dynamicModelId("BBM_GEN").variables("voltageRegulator_EfdPu").add(curves::add);
        new DynawoOutputVariablesBuilder().staticId("BUS").variables("Upu_value").add(curves::add);
        new DynawoOutputVariablesBuilder().dynamicModelId("BBM_GEN2").variables("generator_omegaPu", "generator_PGen", "generator_UStatorPU").add(curves::add);
        new DynawoOutputVariablesBuilder().staticId("LOAD").variables("load_PPu", "load_QPu").outputType(OutputVariable.OutputType.CURVE).add(curves::add);
        new DynawoOutputVariablesBuilder().dynamicModelId("BBM_GEN").variables("generator_PGen").outputType(OutputVariable.OutputType.FSV).add(curves::add);
        new DynawoOutputVariablesBuilder().staticId("LOAD").variables("load_QPu").outputType(OutputVariable.OutputType.FSV).add(curves::add);
        return curves;
    }
}
