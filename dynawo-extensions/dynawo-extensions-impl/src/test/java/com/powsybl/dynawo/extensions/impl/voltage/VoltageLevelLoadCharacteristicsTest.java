/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.voltage;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.extensions.api.voltage.VoltageLevelLoadCharacteristics;
import com.powsybl.dynawo.extensions.api.voltage.VoltageLevelLoadCharacteristicsAdder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.VariantManager;
import com.powsybl.iidm.network.VoltageLevel;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.powsybl.iidm.network.VariantManagerConstants.INITIAL_VARIANT_ID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Riad Benradi {@literal <riad.benradi at rte-france.com>}
 */

class VoltageLevelLoadCharacteristicsTest {

    private final Network network = EurostagTutorialExample1Factory.create();
    private final VoltageLevel vl = network.getVoltageLevel("VLHV1");

    @Test
    void addAndGetCharacteristic() {

        assertNotNull(vl);

        VoltageLevelLoadCharacteristics ext = vl.newExtension(VoltageLevelLoadCharacteristicsAdder.class)
                .withCharacteristic(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL)
                .add();

        assertNotNull(ext);
        assertEquals(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL, ext.getCharacteristic());

        ext.setCharacteristic(VoltageLevelLoadCharacteristics.Type.CONSTANT);
        assertEquals(VoltageLevelLoadCharacteristics.Type.CONSTANT, ext.getCharacteristic());
    }

    @Test
    void constructorShouldThrowNPEWhenCharacteristicIsNull() {
        VoltageLevelLoadCharacteristicsAdder vlAdder = vl.newExtension(VoltageLevelLoadCharacteristicsAdder.class);
        assertThrows(NullPointerException.class, () -> vlAdder.withCharacteristic(null));
    }

    @Test
    void getCharacteristicReturnsInitialValue() {
        VoltageLevelLoadCharacteristics ext = vl.newExtension(VoltageLevelLoadCharacteristicsAdder.class)
                .withCharacteristic(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL)
                .add();

        assertEquals(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL, ext.getCharacteristic());
    }

    @Test
    void setCharacteristicUpdatesValue() {
        VoltageLevelLoadCharacteristics ext = vl.newExtension(VoltageLevelLoadCharacteristicsAdder.class)
                .withCharacteristic(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL)
                .add();

        ext.setCharacteristic(VoltageLevelLoadCharacteristics.Type.CONSTANT);

        assertEquals(VoltageLevelLoadCharacteristics.Type.CONSTANT, ext.getCharacteristic());
    }

    @Test
    void variantsCloneTest() {
        String variant1 = "variant1";
        String variant2 = "variant2";
        String variant3 = "variant3";

        VoltageLevelLoadCharacteristics ext = vl.newExtension(VoltageLevelLoadCharacteristicsAdder.class)
                .withCharacteristic(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL)
                .add();
        assertNotNull(ext);

        // Testing variant cloning
        VariantManager variantManager = network.getVariantManager();
        variantManager.cloneVariant(INITIAL_VARIANT_ID, variant1);
        variantManager.cloneVariant(variant1, variant2);
        variantManager.setWorkingVariant(variant1);
        assertEquals(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL, ext.getCharacteristic());

        // Testing setting different values in the cloned variant and going back to the initial one
        ext.setCharacteristic(VoltageLevelLoadCharacteristics.Type.CONSTANT);
        assertEquals(VoltageLevelLoadCharacteristics.Type.CONSTANT, ext.getCharacteristic());
        variantManager.setWorkingVariant(INITIAL_VARIANT_ID);
        assertEquals(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL, ext.getCharacteristic());

        // Removes a variant then adds another variant to test variant recycling (hence calling allocateVariantArrayElement)
        variantManager.removeVariant(variant1);
        variantManager.cloneVariant(INITIAL_VARIANT_ID, List.of(variant1, variant3));
        variantManager.setWorkingVariant(variant1);
        assertEquals(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL, ext.getCharacteristic());
        variantManager.setWorkingVariant(variant3);
        assertEquals(VoltageLevelLoadCharacteristics.Type.INDUSTRIAL, ext.getCharacteristic());

        // Test removing current variant
        variantManager.removeVariant(variant3);
        Exception e = assertThrows(PowsyblException.class, ext::getCharacteristic);
        assertEquals("Variant index not set", e.getMessage());
    }
}
