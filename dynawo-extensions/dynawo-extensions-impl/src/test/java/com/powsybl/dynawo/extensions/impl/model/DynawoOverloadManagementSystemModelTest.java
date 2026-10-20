/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.extensions.api.model.DynawoOverloadManagementSystemModel;
import com.powsybl.dynawo.extensions.api.model.DynawoOverloadManagementSystemModelAdder;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.powsybl.iidm.network.VariantManagerConstants.INITIAL_VARIANT_ID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoOverloadManagementSystemModelTest {

    @Test
    void addExtension() {
        Network network = EurostagTutorialExample1Factory.create();
        TwoWindingsTransformer tfo = network.getTwoWindingsTransformer("NGEN_NHV1");
        DynawoOverloadManagementSystemModelAdder<TwoWindingsTransformer> adder = tfo.newExtension(DynawoOverloadManagementSystemModelAdder.class);
        adder.withDynamicModelId("OMS")
                .withModelName("OverloadManagementSystem")
                .withParameterSetId("oms")
                .withIMeasurement("NHV1_NHV2_1")
                .withIMeasurementSide(TwoSides.ONE)
                .add();
        DynawoOverloadManagementSystemModel<TwoWindingsTransformer> info = tfo.getExtension(DynawoOverloadManagementSystemModel.class);
        assertEquals("OMS", info.getDynamicModelId());
        assertEquals("OverloadManagementSystem", info.getModelName());
        assertEquals("oms", info.getParameterSetId());
        assertEquals("NHV1_NHV2_1", info.getIMeasurement());
        assertEquals(TwoSides.ONE, info.getIMeasurementSide());
    }

    @Test
    void variantsCloneTest() {
        String variant1 = "variant1";
        String variant2 = "variant2";
        String variant3 = "variant3";

        Network network = EurostagTutorialExample1Factory.create();
        Line line = network.getLine("NHV1_NHV2_1");
        DynawoOverloadManagementSystemModelAdder<Line> adder = line.newExtension(DynawoOverloadManagementSystemModelAdder.class);
        adder.withDynamicModelId("OMS")
                .withModelName("OverloadManagementSystem")
                .withParameterSetId("oms")
                .withIMeasurement("NGEN_NHV1")
                .withIMeasurementSide(TwoSides.ONE)
                .add();
        DynawoOverloadManagementSystemModel<Line> ext = line.getExtension(DynawoOverloadManagementSystemModel.class);
        assertNotNull(ext);

        // Testing variant cloning
        VariantManager variantManager = network.getVariantManager();
        variantManager.cloneVariant(INITIAL_VARIANT_ID, variant1);
        variantManager.cloneVariant(variant1, variant2);
        variantManager.setWorkingVariant(variant1);
        assertEquals(TwoSides.ONE, ext.getIMeasurementSide());

        // Testing setting different values in the cloned variant and going back to the initial one
        ext.setIMeasurementSide(TwoSides.TWO);
        assertEquals(TwoSides.TWO, ext.getIMeasurementSide());
        variantManager.setWorkingVariant(INITIAL_VARIANT_ID);
        assertEquals(TwoSides.ONE, ext.getIMeasurementSide());

        // Removes a variant then adds another variant to test variant recycling (hence calling allocateVariantArrayElement)
        variantManager.removeVariant(variant1);
        variantManager.cloneVariant(INITIAL_VARIANT_ID, List.of(variant1, variant3));
        variantManager.setWorkingVariant(variant1);
        assertEquals(TwoSides.ONE, ext.getIMeasurementSide());
        variantManager.setWorkingVariant(variant3);
        assertEquals(TwoSides.ONE, ext.getIMeasurementSide());

        // Test removing current variant
        variantManager.removeVariant(variant3);
        Exception e = assertThrows(PowsyblException.class, ext::getIMeasurementSide);
        assertEquals("Variant index not set", e.getMessage());
    }
}

