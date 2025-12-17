/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.generator;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.extensions.api.generator.RpclType;
import com.powsybl.dynawo.extensions.api.generator.SynchronizedGeneratorProperties;
import com.powsybl.dynawo.extensions.api.generator.SynchronizedGeneratorPropertiesAdder;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManager;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.powsybl.iidm.network.VariantManagerConstants.INITIAL_VARIANT_ID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */

class SynchronizedGeneratorPropertiesTest {

    private final Network network = EurostagTutorialExample1Factory.create();
    private final Generator generator = network.getGenerator("GEN");

    @Test
    void minimalProps() {
        SynchronizedGeneratorProperties result = generator.newExtension(SynchronizedGeneratorPropertiesAdder.class)
                .withType("type")
                .withRpcl2(true)
                .add();
        assertEquals("type", result.getType());
        assertTrue(result.isRpcl2());
    }

    @Test
    void settersAndHasRpcl2Variants() {
        SynchronizedGeneratorProperties p = generator.newExtension(SynchronizedGeneratorPropertiesAdder.class)
                        .withType("t1")
                        .withRpcl2(true)
                        .add();

        assertEquals("t1", p.getType());
        assertTrue(p.isRpcl2());

        p.setType("t2");
        assertEquals("t2", p.getType());

        p.setRpcl(RpclType.NONE);
        assertEquals(RpclType.NONE, p.getRpcl());
    }

    @Test
     void setTypeWithNullThrowsNPE() {
        SynchronizedGeneratorPropertiesAdder p = generator.newExtension(SynchronizedGeneratorPropertiesAdder.class)
                .withType(null)
                .withRpcl2(true);
        assertThrows(NullPointerException.class, p::add);
    }

    @Test
    void variantsCloneTest() {
        String variant1 = "variant1";
        String variant2 = "variant2";
        String variant3 = "variant3";

        SynchronizedGeneratorProperties ext = generator.newExtension(SynchronizedGeneratorPropertiesAdder.class)
                .withType("type")
                .withRpcl2(true)
                .add();

        assertNotNull(ext);

        // Testing variant cloning
        VariantManager variantManager = network.getVariantManager();
        variantManager.cloneVariant(INITIAL_VARIANT_ID, variant1);
        variantManager.cloneVariant(variant1, variant2);
        variantManager.setWorkingVariant(variant1);
        assertEquals("type", ext.getType());

        // Testing setting different values in the cloned variant and going back to the initial one
        ext.setType("type2");
        assertEquals("type2", ext.getType());
        variantManager.setWorkingVariant(INITIAL_VARIANT_ID);
        assertEquals("type", ext.getType());

        // Removes a variant then adds another variant to test variant recycling (hence calling allocateVariantArrayElement)
        variantManager.removeVariant(variant1);
        variantManager.cloneVariant(INITIAL_VARIANT_ID, List.of(variant1, variant3));
        variantManager.setWorkingVariant(variant1);
        assertEquals("type", ext.getType());
        variantManager.setWorkingVariant(variant3);
        assertEquals("type", ext.getType());

        // Test removing current variant
        variantManager.removeVariant(variant3);
        Exception e = assertThrows(PowsyblException.class, ext::getType);
        assertEquals("Variant index not set", e.getMessage());
    }
}

