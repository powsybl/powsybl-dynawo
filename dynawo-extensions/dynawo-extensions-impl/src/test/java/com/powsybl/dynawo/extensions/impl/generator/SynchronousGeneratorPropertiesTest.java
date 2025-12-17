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
import com.powsybl.dynawo.extensions.api.generator.SynchronousGeneratorProperties;
import com.powsybl.dynawo.extensions.api.generator.SynchronousGeneratorPropertiesAdder;
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
class SynchronousGeneratorPropertiesTest {

    private final Network network = EurostagTutorialExample1Factory.create();
    private final Generator generator = network.getGenerator("GEN");

    @Test
    void propsWithAllParameters() {
        SynchronousGeneratorProperties p = generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
            .withVoltageRegulator("voltageRegulator")
            .withGovernor("governor")
            .withNumberOfWindings(SynchronousGeneratorProperties.Windings.THREE_WINDINGS)
            .withAuxiliaries(true)
            .withAggregated(true)
            .withQlim(true)
            .withRpcl(RpclType.RPCL1)
            .withInternalTransformer(false)
            .withPss("pss")
            .withUva(SynchronousGeneratorProperties.Uva.LOCAL)
            .add();

        assertEquals("voltageRegulator", p.getVoltageRegulator());
        assertEquals("governor", p.getGovernor());
        assertEquals(SynchronousGeneratorProperties.Windings.THREE_WINDINGS, p.getNumberOfWindings());
        assertEquals("pss", p.getPss());
        assertEquals(SynchronousGeneratorProperties.Uva.LOCAL, p.getUva());
        assertTrue(p.isAggregated());
        assertTrue(p.isAuxiliaries());
        assertTrue(p.isQlim());
        assertTrue(p.isRpcl1());
        assertFalse(p.isRpcl2());
        assertFalse(p.isInternalTransformer());
    }

    @Test
    void defaultsKeptWhenOptionalsNotProvided() {
        SynchronousGeneratorProperties p = generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
                .withNumberOfWindings(SynchronousGeneratorProperties.Windings.THREE_WINDINGS)
                .add();

        assertEquals(SynchronousGeneratorProperties.Windings.THREE_WINDINGS, p.getNumberOfWindings());
        assertNull(p.getGovernor());
        assertNull(p.getVoltageRegulator());
        assertNull(p.getPss());
        assertFalse(p.isAuxiliaries());
        assertFalse(p.isInternalTransformer());
        assertFalse(p.isAggregated());
        assertFalse(p.isQlim());
        assertFalse(p.isRpcl1());
        assertFalse(p.isRpcl2());
        assertEquals(SynchronousGeneratorProperties.Uva.LOCAL, p.getUva());
    }

    @Test
    void governorWithoutVoltageRegulatorShouldFail() {
        SynchronousGeneratorPropertiesAdder p = generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
            .withNumberOfWindings(SynchronousGeneratorProperties.Windings.THREE_WINDINGS)
            .withGovernor("gov");
        assertThrows(PowsyblException.class, p::add);
    }

    @Test
    void voltageRegulatorWithoutGovernorShouldFail() {
        SynchronousGeneratorPropertiesAdder p = generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
            .withNumberOfWindings(SynchronousGeneratorProperties.Windings.THREE_WINDINGS)
            .withVoltageRegulator("vr");
        assertThrows(PowsyblException.class, p::add);
    }

    @Test
    void rpclCombinations() {
        SynchronousGeneratorProperties p = generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
                .withNumberOfWindings(SynchronousGeneratorProperties.Windings.THREE_WINDINGS)
                .withRpcl(RpclType.NONE)
                .add();

        assertFalse(p.isRpcl1());
        assertFalse(p.isRpcl2());

        p.setRpcl(RpclType.RPCL1);
        assertTrue(p.isRpcl1());
        assertFalse(p.isRpcl2());

        p.setRpcl(RpclType.RPCL2);
        assertFalse(p.isRpcl1());
        assertTrue(p.isRpcl2());
    }

    @Test
    void settersShouldOverrideValues() {
        SynchronousGeneratorProperties p = generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
                .withNumberOfWindings(SynchronousGeneratorProperties.Windings.FOUR_WINDINGS)
                .add();

        p.setGovernor("g");
        p.setVoltageRegulator("v");
        p.setPss("pss");
        p.setAuxiliaries(true);
        p.setInternalTransformer(true);
        p.setAggregated(true);
        p.setQlim(true);
        p.setNumberOfWindings(SynchronousGeneratorProperties.Windings.THREE_WINDINGS);
        p.setUva(SynchronousGeneratorProperties.Uva.DISTANT);
        p.setRpcl(RpclType.RPCL1);

        assertEquals("g", p.getGovernor());
        assertEquals("v", p.getVoltageRegulator());
        assertEquals("pss", p.getPss());
        assertTrue(p.isAuxiliaries());
        assertTrue(p.isInternalTransformer());
        assertTrue(p.isAggregated());
        assertTrue(p.isQlim());
        assertEquals(SynchronousGeneratorProperties.Windings.THREE_WINDINGS, p.getNumberOfWindings());
        assertEquals(SynchronousGeneratorProperties.Uva.DISTANT, p.getUva());
        assertTrue(p.isRpcl1());
        assertFalse(p.isRpcl2());
    }

    @Test
    void nullWindingsShouldThrowNPE() {
        SynchronousGeneratorPropertiesAdder p = generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
            .withNumberOfWindings(null);
        assertThrows(NullPointerException.class, p::add);
    }

    @Test
    void setNullWindingsShouldThrowNPE() {
        SynchronousGeneratorProperties p = generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
            .withNumberOfWindings(SynchronousGeneratorProperties.Windings.THREE_WINDINGS)
            .add();
        assertThrows(NullPointerException.class, () -> p.setNumberOfWindings(null));
    }

    @Test
    void variantsCloneTest() {
        String variant1 = "variant1";
        String variant2 = "variant2";
        String variant3 = "variant3";

        SynchronousGeneratorProperties ext = generator.newExtension(SynchronousGeneratorPropertiesAdder.class)
                .withNumberOfWindings(SynchronousGeneratorProperties.Windings.FOUR_WINDINGS)
                .withRpcl(RpclType.RPCL2)
                .add();

        assertNotNull(ext);

        // Testing variant cloning
        VariantManager variantManager = network.getVariantManager();
        variantManager.cloneVariant(INITIAL_VARIANT_ID, variant1);
        variantManager.cloneVariant(variant1, variant2);
        variantManager.setWorkingVariant(variant1);
        assertEquals(RpclType.RPCL2, ext.getRpcl());

        // Testing setting different values in the cloned variant and going back to the initial one
        ext.setRpcl(RpclType.RPCL1);
        assertEquals(RpclType.RPCL1, ext.getRpcl());
        variantManager.setWorkingVariant(INITIAL_VARIANT_ID);
        assertEquals(RpclType.RPCL2, ext.getRpcl());

        // Removes a variant then adds another variant to test variant recycling (hence calling allocateVariantArrayElement)
        variantManager.removeVariant(variant1);
        variantManager.cloneVariant(INITIAL_VARIANT_ID, List.of(variant1, variant3));
        variantManager.setWorkingVariant(variant1);
        assertEquals(RpclType.RPCL2, ext.getRpcl());
        variantManager.setWorkingVariant(variant3);
        assertEquals(RpclType.RPCL2, ext.getRpcl());

        // Test removing current variant
        variantManager.removeVariant(variant3);
        Exception e = assertThrows(PowsyblException.class, ext::getRpcl);
        assertEquals("Variant index not set", e.getMessage());
    }
}
