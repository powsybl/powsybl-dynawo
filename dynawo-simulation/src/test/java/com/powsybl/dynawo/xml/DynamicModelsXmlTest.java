/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.lines.LineModel;
import com.powsybl.dynawo.models.loads.BaseLoad;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.iidm.network.Identifiable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynamicModelsXmlTest extends DynawoTestUtil {

    @Test
    void writeDynamicModel() throws SAXException, IOException {
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder<>(network, dynamicModels)
                .outputVariables(outputVariables)
                .build();
        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    @Test
    void writeDynamicModelWithLoadsAndOnlyOneFictitiousGenerator() throws SAXException, IOException {
        dynamicModels.clear();
        dynamicModels.add(BaseGeneratorBuilder.of(network)
                .dynamicModelId("BBM_GEN6")
                .staticId("GEN6")
                .parameterSetId("GF")
                .build());

        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder<>(network, dynamicModels)
                .outputVariables(outputVariables)
                .build();

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "dyd_fictitious.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    @Test
    void duplicateStaticId() {
        dynamicModels.clear();
        BaseLoad load1 = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .dynamicModelId("BBM_LOAD")
                .staticId("LOAD")
                .parameterSetId("lab")
                .build();
        BaseLoad load2 = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .dynamicModelId("BBM_LOAD2")
                .staticId("LOAD")
                .parameterSetId("LAB")
                .build();
        dynamicModels.add(load1);
        dynamicModels.add(load2);
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder<>(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();
        Assertions.assertThat(context.getBlackBoxDynamicModels()).containsExactly(load1);
    }

    @Test
    void duplicateDynamicId() {
        dynamicModels.clear();
        BaseLoad load1 = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .dynamicModelId("BBM_LOAD")
                .staticId("LOAD")
                .parameterSetId("lab")
                .build();
        BaseLoad load2 = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .dynamicModelId("BBM_LOAD")
                .staticId("LOAD2")
                .parameterSetId("LAB")
                .build();
        dynamicModels.add(load1);
        dynamicModels.add(load2);
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder<>(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();
        Assertions.assertThat(context.getBlackBoxDynamicModels()).containsExactly(load1);
    }

    @Test
    void wrongDynawoVersionModel() {
        dynamicModels.clear();
        dynamicModels.add(BaseLoadBuilder.of(network, "ElectronicLoad")
                .dynamicModelId("BBM_L")
                .staticId("LOAD")
                .parameterSetId("lab")
                .build());
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder<>(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .currentVersion(new DynawoVersion(1, 2, 0))
                .build();
        Assertions.assertThat(context.getBlackBoxDynamicModels()).isEmpty();
    }

    @Test
    void testIncorrectModelException() {
        DynawoSimulationContext dc = new DynawoSimulationContext
                .Builder<>(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();
        Identifiable<?> gen = network.getIdentifiable("GEN5");
        Exception e = assertThrows(PowsyblException.class, () -> dc.getDynamicModel(gen, LineModel.class, true));
        assertEquals("The model identified by the static id GEN5 does not match the expected model (LineModel)", e.getMessage());
    }
}
