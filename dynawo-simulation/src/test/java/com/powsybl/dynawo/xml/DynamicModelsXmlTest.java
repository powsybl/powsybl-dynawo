/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.DynawoSimulationConstants;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.commons.DynawoVersion;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.lines.LineModel;
import com.powsybl.dynawo.models.loads.BaseLoad;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.iidm.network.Identifiable;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

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
                .Builder(network, dynamicModels)
                .outputVariables(outputVariables)
                .build();
        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", "dyd.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    @Test
    void writeDynamicModelWithLoadsAndOnlyOneFictitiousGenerator() throws SAXException, IOException {
        dynamicModels.clear();
        dynamicModels.add(BaseGeneratorBuilder.of(network)
                .staticId("GEN6")
                .parameterSetId("GF")
                .build());

        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .outputVariables(outputVariables)
                .build();

        DydXml.write(tmpDir, context.getSimulationDydData());
        validate("dyd.xsd", "dyd_fictitious.xml", tmpDir.resolve(DynawoSimulationConstants.DYD_FILENAME));
    }

    @Test
    void duplicateStaticId() {
        dynamicModels.clear();
        BaseLoad load1 = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId("LOAD")
                .parameterSetId("lab")
                .build();
        BaseLoad load2 = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId("LOAD")
                .parameterSetId("LAB")
                .build();
        dynamicModels.add(load1);
        dynamicModels.add(load2);
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();
        Assertions.assertThat(context.getBlackBoxDynamicModels()).containsExactly(load1);
    }

    @Test
    void duplicateDynamicId() {
        String duplicatedId = "LOAD";
        dynamicModels.clear();
        network.getTwoWindingsTransformer("NGEN_NHV1").newPhaseTapChanger()
                .setTapPosition(0)
                .beginStep().setR(1.0).setX(2.0).setG(3.0).setB(4.0).setAlpha(5.0).setRho(6.0).endStep()
                .add();
        BaseLoad load = BaseLoadBuilder.of(network, "LoadAlphaBeta")
                .staticId(duplicatedId)
                .parameterSetId("lab")
                .build();
        BlackBoxModel phaseShifter = PhaseShifterPAutomationSystemBuilder.of(network)
                .dynamicModelId(duplicatedId)
                .transformer("NGEN_NHV1")
                .parameterSetId("PS")
                .build();
        dynamicModels.add(load);
        dynamicModels.add(phaseShifter);
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .build();
        Assertions.assertThat(context.getBlackBoxDynamicModels()).containsExactly(load);
    }

    @Test
    void wrongDynawoVersionModel() {
        dynamicModels.clear();
        dynamicModels.add(BaseLoadBuilder.of(network, "ElectronicLoad")
                .staticId("LOAD")
                .parameterSetId("lab")
                .build());
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .eventModels(eventModels)
                .outputVariables(outputVariables)
                .currentVersion(new DynawoVersion(1, 2, 0))
                .build();
        Assertions.assertThat(context.getBlackBoxDynamicModels()).isEmpty();
    }

    @Test
    void testIncorrectModelException() {
        Identifiable<?> gen = network.getIdentifiable("GEN5");
        MacroConnectionsAdder adder = new MacroConnectionsAdder(
                id -> dynamicModels.stream()
                        .filter(dm -> dm.getDynamicModelId().equals(id))
                        .findFirst().orElse(null),
                id -> null,
                mc -> { },
                (mc, f) -> { },
                ReportNode.NO_OP
        );
        BlackBoxModel bbm = dynamicModels.getFirst();

        Exception e = assertThrows(PowsyblException.class, () -> adder.createMacroConnections(bbm, gen, LineModel.class, l -> List.of()));
        assertEquals("The model identified by the id GEN5 does not match the expected model (LineModel)", e.getMessage());
    }
}
