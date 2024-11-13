/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
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
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class DynamicModelsXmlTest extends DynawoTestUtil {

    @Test
    void writeDynamicModel() throws SAXException, IOException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load();
        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, new ArrayList<>(), outputVariables, parameters, dynawoParameters);

        DydXml.write(tmpDir, context);
        validate("dyd.xsd", "dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    @Test
    void writeDynamicModelWithLoadsAndOnlyOneFictitiousGenerator() throws SAXException, IOException {
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynawoSimulationParameters dynawoParameters = DynawoSimulationParameters.load();
        dynamicModels.clear();
        dynamicModels.add(BaseGeneratorBuilder.of(network)
                .dynamicModelId("BBM_GEN6")
                .staticId("GEN6")
                .parameterSetId("GF")
                .build());

        DynawoSimulationContext context = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, new ArrayList<>(), outputVariables, parameters, dynawoParameters);

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
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        DynawoSimulationContext context = new DynawoSimulationContext(network, workingVariantId, dynamicModels, eventModels, outputVariables, DynamicSimulationParameters.load(), DynawoSimulationParameters.load());
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
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        DynawoSimulationContext context = new DynawoSimulationContext(network, workingVariantId, dynamicModels, eventModels, outputVariables, DynamicSimulationParameters.load(), DynawoSimulationParameters.load());
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
        String workingVariantId = network.getVariantManager().getWorkingVariantId();
        DynawoSimulationContext context = new DynawoSimulationContext(network, workingVariantId, dynamicModels,
                eventModels, outputVariables, DynamicSimulationParameters.load(), DynawoSimulationParameters.load(),
                new DynawoVersion(1, 2, 0), ReportNode.NO_OP);
        Assertions.assertThat(context.getBlackBoxDynamicModels()).isEmpty();
    }

    @Test
    void testIncorrectModelException() {
        DynawoSimulationContext dc = new DynawoSimulationContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, eventModels, outputVariables, DynamicSimulationParameters.load(), DynawoSimulationParameters.load());
        Identifiable<?> gen = network.getIdentifiable("GEN5");
        Exception e = assertThrows(PowsyblException.class, () -> dc.getDynamicModel(gen, LineModel.class, true));
        assertEquals("The model identified by the static id GEN5 does not match the expected model (LineModel)", e.getMessage());
    }
}
