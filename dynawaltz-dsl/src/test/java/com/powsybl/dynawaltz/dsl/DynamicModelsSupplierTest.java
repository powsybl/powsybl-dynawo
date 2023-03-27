/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.dsl;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.DynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.DynamicModelGroovyExtension;
import com.powsybl.dynamicsimulation.groovy.GroovyDynamicModelsSupplier;
import com.powsybl.dynamicsimulation.groovy.GroovyExtension;
import com.powsybl.dynawaltz.DynaWaltzProvider;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatio;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicModelsSupplierTest {

    private static final String FOLDER_NAME = "/dynamicModels/";

    private static final List<DynamicModelGroovyExtension> EXTENSIONS = GroovyExtension.find(DynamicModelGroovyExtension.class, DynaWaltzProvider.NAME);

    @Test
    void testTransformer() {
        DynamicModelsSupplier supplier = new GroovyDynamicModelsSupplier(getResourceAsStream("transformer.groovy"), EXTENSIONS);
        List<DynamicModel> dynamicModels = supplier.get(EurostagTutorialExample1Factory.create());
        assertEquals(1, dynamicModels.size());
        assertTrue(dynamicModels.get(0) instanceof TransformerFixedRatio);
        assertBlackBoxModel((TransformerFixedRatio) dynamicModels.get(0), "BBM_NGEN_NHV1", "NGEN_NHV1", "TFR", "TransformerFixedRatio");
    }

    void assertBlackBoxModel(BlackBoxModel bbm, String dynamicId, String staticId, String parameterId, String lib) {
        assertEquals(dynamicId, bbm.getDynamicModelId());
        assertEquals(staticId, bbm.getStaticId().orElseThrow());
        assertEquals(parameterId, bbm.getParameterSetId());
        assertEquals(lib, bbm.getLib());
    }

    protected static InputStream getResourceAsStream(String name) {
        return Objects.requireNonNull(DynaWaltzGroovyDynamicModelsSupplierTest.class.getResourceAsStream(FOLDER_NAME + name));
    }
}
