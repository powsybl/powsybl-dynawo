/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.extensions.api.model.DynawoUnderVoltageModel;
import com.powsybl.dynawo.extensions.api.model.DynawoUnderVoltageModelAdder;
import com.powsybl.iidm.network.Generator;
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
class DynawoUnderVoltageModelTest {

    @Test
    void addExtension() {
        Network network = EurostagTutorialExample1Factory.create();
        Generator gen = network.getGenerator("GEN");
        gen.newExtension(DynawoUnderVoltageModelAdder.class)
                .withDynamicModelId("UVA")
                .withModelName("UnderVoltage")
                .withParameterSetId("uva")
                .add();
        DynawoUnderVoltageModel info = gen.getExtension(DynawoUnderVoltageModel.class);
        assertEquals("UVA", info.getDynamicModelId());
        assertEquals("UnderVoltage", info.getModelName());
        assertEquals("uva", info.getParameterSetId());
    }

    @Test
    void variantsCloneTest() {
        String variant1 = "variant1";
        String variant2 = "variant2";
        String variant3 = "variant3";

        Network network = EurostagTutorialExample1Factory.create();
        Generator generator = network.getGenerator("GEN");
        generator.newExtension(DynawoUnderVoltageModelAdder.class)
                .withModelName("UnderVoltage")
                .withDynamicModelId("UVA")
                .withParameterSetId("uva")
                .add();
        DynawoUnderVoltageModel ext = generator.getExtension(DynawoUnderVoltageModel.class);
        assertNotNull(ext);

        // Testing variant cloning
        VariantManager variantManager = network.getVariantManager();
        variantManager.cloneVariant(INITIAL_VARIANT_ID, variant1);
        variantManager.cloneVariant(variant1, variant2);
        variantManager.setWorkingVariant(variant1);
        assertEquals("uva", ext.getParameterSetId());

        // Testing setting different values in the cloned variant and going back to the initial one
        ext.setParameterSetId("uva2");
        assertEquals("uva2", ext.getParameterSetId());
        variantManager.setWorkingVariant(INITIAL_VARIANT_ID);
        assertEquals("uva", ext.getParameterSetId());

        // Removes a variant then adds another variant to test variant recycling (hence calling allocateVariantArrayElement)
        variantManager.removeVariant(variant1);
        variantManager.cloneVariant(INITIAL_VARIANT_ID, List.of(variant1, variant3));
        variantManager.setWorkingVariant(variant1);
        assertEquals("uva", ext.getParameterSetId());
        variantManager.setWorkingVariant(variant3);
        assertEquals("uva", ext.getParameterSetId());

        // Test removing current variant
        variantManager.removeVariant(variant3);
        Exception e = assertThrows(PowsyblException.class, ext::getParameterSetId);
        assertEquals("Variant index not set", e.getMessage());
    }
}

