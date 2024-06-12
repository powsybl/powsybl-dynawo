/**
 * Copyright (c) 2024, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.suppliers;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.Curve;
import com.powsybl.dynawaltz.curves.DynawoCurvesBuilder;
import com.powsybl.dynawaltz.suppliers.curves.DynawoCurveSupplier;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoCurvesSupplierTest {

    @Test
    void testCurvesSupplier() throws IOException {

        Network network = EurostagTutorialExample1Factory.create();
        try (InputStream is = getClass().getResourceAsStream("/suppliers/curves.json")) {
            DynawoCurveSupplier dynawoCurveSupplier = new DynawoCurveSupplier(is);
            List<Curve> curves = dynawoCurveSupplier.get(network, ReportNode.NO_OP);
            assertThat(curves).usingRecursiveFieldByFieldElementComparatorOnFields()
                    .containsExactlyInAnyOrderElementsOf(getExpectedCurves());
        }
    }

    private static List<Curve> getExpectedCurves() {
        List<Curve> curves = new ArrayList<>();
        new DynawoCurvesBuilder().dynamicModelId("BBM_GEN").variable("voltageRegulator_EfdPu").add(curves::add);
        new DynawoCurvesBuilder().staticId("BUS").variables("Upu_value").add(curves::add);
        new DynawoCurvesBuilder().dynamicModelId("BBM_GEN2").variables("generator_omegaPu", "generator_PGen", "generator_UStatorPU").add(curves::add);
        new DynawoCurvesBuilder().staticId("LOAD").variables("load_PPu", "load_QPu").add(curves::add);
        return curves;
    }
}
