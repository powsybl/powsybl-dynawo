/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.generator.connection;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.extensions.api.generator.connection.GeneratorConnectionLevel;
import com.powsybl.dynawo.extensions.api.generator.connection.GeneratorConnectionLevelAdder;
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

class GeneratorConnectionLevelImplTest {
    private final Network network = EurostagTutorialExample1Factory.create();
    private final Generator generator = network.getGenerator("GEN");

    @Test
    void extensionIsAttachedToGenerator() {
        GeneratorConnectionLevel ext = generator.newExtension(GeneratorConnectionLevelAdder.class)
                .withLevel(GeneratorConnectionLevel.GeneratorConnectionLevelType.DSO)
                .add();

        assertNotNull(ext);
        assertEquals(GeneratorConnectionLevel.GeneratorConnectionLevelType.DSO, ext.getLevel());
    }

    @Test
    void addingWithoutLevelThrows() {
        GeneratorConnectionLevelAdder ext = generator.newExtension(GeneratorConnectionLevelAdder.class);
        assertThrows(NullPointerException.class, ext::add);
    }

    @Test
    void settingLevelAtNullThrowsNPE() {
        GeneratorConnectionLevelAdder ext = generator.newExtension(GeneratorConnectionLevelAdder.class).withLevel(null);
        assertThrows(NullPointerException.class, ext::add);
    }

    @Test
    void addingTwiceReplacesExistingExtension() {
        generator.newExtension(GeneratorConnectionLevelAdder.class)
                .withLevel(GeneratorConnectionLevel.GeneratorConnectionLevelType.DSO)
                .add();

        generator.newExtension(GeneratorConnectionLevelAdder.class)
                .withLevel(GeneratorConnectionLevel.GeneratorConnectionLevelType.TSO)
                .add();

        GeneratorConnectionLevel ext = generator.getExtension(GeneratorConnectionLevel.class);
        assertNotNull(ext);
        assertEquals(GeneratorConnectionLevel.GeneratorConnectionLevelType.TSO, ext.getLevel());
    }

    @Test
        void variantsCloneTest() {
        String variant1 = "variant1";
        String variant2 = "variant2";
        String variant3 = "variant3";

        GeneratorConnectionLevel ext = generator.newExtension(GeneratorConnectionLevelAdder.class)
                .withLevel(GeneratorConnectionLevel.GeneratorConnectionLevelType.DSO)
                .add();
        assertNotNull(ext);

        // Testing variant cloning
        VariantManager variantManager = network.getVariantManager();
        variantManager.cloneVariant(INITIAL_VARIANT_ID, variant1);
        variantManager.cloneVariant(variant1, variant2);
        variantManager.setWorkingVariant(variant1);
        assertEquals(GeneratorConnectionLevel.GeneratorConnectionLevelType.DSO, ext.getLevel());

        // Testing setting different values in the cloned variant and going back to the initial one
        ext.setLevel(GeneratorConnectionLevel.GeneratorConnectionLevelType.TSO);
        assertEquals(GeneratorConnectionLevel.GeneratorConnectionLevelType.TSO, ext.getLevel());
        variantManager.setWorkingVariant(INITIAL_VARIANT_ID);
        assertEquals(GeneratorConnectionLevel.GeneratorConnectionLevelType.DSO, ext.getLevel());

        // Removes a variant then adds another variant to test variant recycling (hence calling allocateVariantArrayElement)
        variantManager.removeVariant(variant1);
        variantManager.cloneVariant(INITIAL_VARIANT_ID, List.of(variant1, variant3));
        variantManager.setWorkingVariant(variant1);
        assertEquals(GeneratorConnectionLevel.GeneratorConnectionLevelType.DSO, ext.getLevel());
        variantManager.setWorkingVariant(variant3);
        assertEquals(GeneratorConnectionLevel.GeneratorConnectionLevelType.DSO, ext.getLevel());

        // Test removing current variant
        variantManager.removeVariant(variant3);
        Exception e = assertThrows(PowsyblException.class, ext::getLevel);
        assertEquals("Variant index not set", e.getMessage());
    }
}
