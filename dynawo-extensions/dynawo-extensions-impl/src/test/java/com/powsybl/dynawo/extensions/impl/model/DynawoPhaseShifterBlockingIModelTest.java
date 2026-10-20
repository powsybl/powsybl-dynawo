/**
 * Copyright (c) 2026, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.extensions.impl.model;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterBlockingIModel;
import com.powsybl.dynawo.extensions.api.model.DynawoPhaseShifterBlockingIModelAdder;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoWindingsTransformer;
import com.powsybl.iidm.network.VariantManager;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.powsybl.iidm.network.VariantManagerConstants.INITIAL_VARIANT_ID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynawoPhaseShifterBlockingIModelTest {

    @Test
    void addExtension() {
        Network network = EurostagTutorialExample1Factory.create();
        TwoWindingsTransformer tfo = network.getTwoWindingsTransformer("NGEN_NHV1");
        tfo.newExtension(DynawoPhaseShifterBlockingIModelAdder.class)
                .withDynamicModelId("PSBI")
                .withModelName("PhaseShifterBlockingI")
                .withParameterSetId("psbi")
                .withPhaseShifterId("PSI")
                .add();
        DynawoPhaseShifterBlockingIModel info = tfo.getExtension(DynawoPhaseShifterBlockingIModel.class);
        assertEquals("PSBI", info.getDynamicModelId());
        assertEquals("PhaseShifterBlockingI", info.getModelName());
        assertEquals("psbi", info.getParameterSetId());
        assertEquals("PSI", info.getPhaseShifterId());
    }

    @Test
    void variantsCloneTest() {
        String variant1 = "variant1";
        String variant2 = "variant2";
        String variant3 = "variant3";

        Network network = EurostagTutorialExample1Factory.create();
        TwoWindingsTransformer tfo = network.getTwoWindingsTransformer("NGEN_NHV1");
        tfo.newExtension(DynawoPhaseShifterBlockingIModelAdder.class)
                .withModelName("PhaseShifterBlockingI")
                .withDynamicModelId("PSBI")
                .withParameterSetId("psi")
                .withPhaseShifterId("PSI")
                .add();
        DynawoPhaseShifterBlockingIModel ext = tfo.getExtension(DynawoPhaseShifterBlockingIModel.class);
        assertNotNull(ext);

        // Testing variant cloning
        VariantManager variantManager = network.getVariantManager();
        variantManager.cloneVariant(INITIAL_VARIANT_ID, variant1);
        variantManager.cloneVariant(variant1, variant2);
        variantManager.setWorkingVariant(variant1);
        assertEquals("PSI", ext.getPhaseShifterId());

        // Testing setting different values in the cloned variant and going back to the initial one
        ext.setPhaseShifterId("PSI2");
        assertEquals("PSI2", ext.getPhaseShifterId());
        variantManager.setWorkingVariant(INITIAL_VARIANT_ID);
        assertEquals("PSI", ext.getPhaseShifterId());

        // Removes a variant then adds another variant to test variant recycling (hence calling allocateVariantArrayElement)
        variantManager.removeVariant(variant1);
        variantManager.cloneVariant(INITIAL_VARIANT_ID, List.of(variant1, variant3));
        variantManager.setWorkingVariant(variant1);
        assertEquals("PSI", ext.getPhaseShifterId());
        variantManager.setWorkingVariant(variant3);
        assertEquals("PSI", ext.getPhaseShifterId());

        // Test removing current variant
        variantManager.removeVariant(variant3);
        Exception e = assertThrows(PowsyblException.class, ext::getParameterSetId);
        assertEquals("Variant index not set", e.getMessage());
    }
}

