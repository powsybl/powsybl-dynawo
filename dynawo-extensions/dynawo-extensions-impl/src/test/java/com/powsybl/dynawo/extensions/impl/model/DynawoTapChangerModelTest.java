/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.commons.TransformerSide;
import com.powsybl.dynawo.extensions.api.model.DynawoTapChangerModel;
import com.powsybl.dynawo.extensions.api.model.DynawoTapChangerModelAdder;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManager;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.powsybl.iidm.network.VariantManagerConstants.INITIAL_VARIANT_ID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoTapChangerModelTest {

    @Test
    void addExtension() {
        Network network = EurostagTutorialExample1Factory.create();
        Load load = network.getLoad("LOAD");
        load.newExtension(DynawoTapChangerModelAdder.class)
                .setDynamicModelId("TC")
                .setModelName("TapChangerAutomationSystem")
                .setParameterSetId("tc")
                .setSide(TransformerSide.LOW_VOLTAGE)
                .add();
        DynawoTapChangerModel info = load.getExtension(DynawoTapChangerModel.class);
        assertEquals("TC", info.getDynamicModelId());
        assertEquals("TapChangerAutomationSystem", info.getModelName());
        assertEquals("tc", info.getParameterSetId());
        assertEquals(TransformerSide.LOW_VOLTAGE, info.getSide());
    }

    @Test
    void variantsCloneTest() {
        String variant1 = "variant1";
        String variant2 = "variant2";
        String variant3 = "variant3";

        Network network = EurostagTutorialExample1Factory.create();
        Load load = network.getLoad("LOAD");
        load.newExtension(DynawoTapChangerModelAdder.class)
                .setModelName("TapChangerAutomationSystem")
                .setDynamicModelId("TC")
                .setParameterSetId("tc")
                .setSide(TransformerSide.LOW_VOLTAGE)
                .add();
        DynawoTapChangerModel ext = load.getExtension(DynawoTapChangerModel.class);
        assertNotNull(ext);

        // Testing variant cloning
        VariantManager variantManager = network.getVariantManager();
        variantManager.cloneVariant(INITIAL_VARIANT_ID, variant1);
        variantManager.cloneVariant(variant1, variant2);
        variantManager.setWorkingVariant(variant1);
        assertEquals(TransformerSide.LOW_VOLTAGE, ext.getSide());

        // Testing setting different values in the cloned variant and going back to the initial one
        ext.setSide(TransformerSide.HIGH_VOLTAGE);
        assertEquals(TransformerSide.HIGH_VOLTAGE, ext.getSide());
        variantManager.setWorkingVariant(INITIAL_VARIANT_ID);
        assertEquals(TransformerSide.LOW_VOLTAGE, ext.getSide());

        // Removes a variant then adds another variant to test variant recycling (hence calling allocateVariantArrayElement)
        variantManager.removeVariant(variant1);
        variantManager.cloneVariant(INITIAL_VARIANT_ID, List.of(variant1, variant3));
        variantManager.setWorkingVariant(variant1);
        assertEquals(TransformerSide.LOW_VOLTAGE, ext.getSide());
        variantManager.setWorkingVariant(variant3);
        assertEquals(TransformerSide.LOW_VOLTAGE, ext.getSide());

        // Test removing current variant
        variantManager.removeVariant(variant3);
        Exception e = assertThrows(PowsyblException.class, ext::getParameterSetId);
        assertEquals("Variant index not set", e.getMessage());
    }
}

