/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.extensions.api.model.DynawoTwoLevelOverloadManagementSystemModel;
import com.powsybl.dynawo.extensions.api.model.DynawoTwoLevelOverloadManagementSystemModelAdder;
import com.powsybl.iidm.network.*;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.powsybl.iidm.network.VariantManagerConstants.INITIAL_VARIANT_ID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoTwoLevelOverloadManagementSystemModelTest {

    @Test
    void addExtension() {
        Network network = EurostagTutorialExample1Factory.create();
        TwoWindingsTransformer tfo = network.getTwoWindingsTransformer("NGEN_NHV1");
        DynawoTwoLevelOverloadManagementSystemModelAdder<TwoWindingsTransformer> adder = tfo.newExtension(DynawoTwoLevelOverloadManagementSystemModelAdder.class);
        adder.withDynamicModelId("TWOMS")
                .withModelName("TwoLevelOverloadManagementSystem")
                .withParameterSetId("twoms")
                .withIMeasurement1("NHV1_NHV2_1")
                .withIMeasurement1Side(TwoSides.ONE)
                .withIMeasurement2("NHV1_NHV2_2")
                .withIMeasurement2Side(TwoSides.TWO)
                .add();
        DynawoTwoLevelOverloadManagementSystemModel<TwoWindingsTransformer> info = tfo.getExtension(DynawoTwoLevelOverloadManagementSystemModel.class);
        assertEquals("TWOMS", info.getDynamicModelId());
        assertEquals("TwoLevelOverloadManagementSystem", info.getModelName());
        assertEquals("twoms", info.getParameterSetId());
        assertEquals("NHV1_NHV2_1", info.getIMeasurement1());
        assertEquals(TwoSides.ONE, info.getIMeasurement1Side());
        assertEquals("NHV1_NHV2_2", info.getIMeasurement2());
        assertEquals(TwoSides.TWO, info.getIMeasurement2Side());
    }

    @Test
    void variantsCloneTest() {
        String variant1 = "variant1";
        String variant2 = "variant2";
        String variant3 = "variant3";

        Network network = EurostagTutorialExample1Factory.create();
        Line line = network.getLine("NHV1_NHV2_1");
        DynawoTwoLevelOverloadManagementSystemModelAdder<Line> adder = line.newExtension(DynawoTwoLevelOverloadManagementSystemModelAdder.class);
        adder.withDynamicModelId("OMS")
                .withModelName("OverloadManagementSystem")
                .withParameterSetId("oms")
                .withIMeasurement1("NGEN_NHV1")
                .withIMeasurement1Side(TwoSides.ONE)
                .withIMeasurement2("NGEN_NHV2")
                .withIMeasurement2Side(TwoSides.TWO)
                .add();
        DynawoTwoLevelOverloadManagementSystemModel<Line> ext = line.getExtension(DynawoTwoLevelOverloadManagementSystemModel.class);
        assertNotNull(ext);

        // Testing variant cloning
        VariantManager variantManager = network.getVariantManager();
        variantManager.cloneVariant(INITIAL_VARIANT_ID, variant1);
        variantManager.cloneVariant(variant1, variant2);
        variantManager.setWorkingVariant(variant1);
        assertEquals(TwoSides.TWO, ext.getIMeasurement2Side());

        // Testing setting different values in the cloned variant and going back to the initial one
        ext.setIMeasurement2Side(TwoSides.ONE);
        assertEquals(TwoSides.ONE, ext.getIMeasurement2Side());
        variantManager.setWorkingVariant(INITIAL_VARIANT_ID);
        assertEquals(TwoSides.TWO, ext.getIMeasurement2Side());

        // Removes a variant then adds another variant to test variant recycling (hence calling allocateVariantArrayElement)
        variantManager.removeVariant(variant1);
        variantManager.cloneVariant(INITIAL_VARIANT_ID, List.of(variant1, variant3));
        variantManager.setWorkingVariant(variant1);
        assertEquals(TwoSides.TWO, ext.getIMeasurement2Side());
        variantManager.setWorkingVariant(variant3);
        assertEquals(TwoSides.TWO, ext.getIMeasurement2Side());

        // Test removing current variant
        variantManager.removeVariant(variant3);
        Exception e = assertThrows(PowsyblException.class, ext::getIMeasurement2Side);
        assertEquals("Variant index not set", e.getMessage());
    }
}

