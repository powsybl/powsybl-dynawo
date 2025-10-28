/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.simplifiers;

import com.google.auto.service.AutoService;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.models.generators.BaseGenerator;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawo.simplifiers.*;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.SvcTestCaseFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ModelsSimplifierTest {

    private static ModelSimplifiers modelSimplifiers;

    @BeforeAll
    static void beforeAll() {
        modelSimplifiers = new ModelSimplifiers();
    }

    @Test
    void loadRemovalSimplifiers() {
        List<ModelsRemovalSimplifier> simplifiers = modelSimplifiers.getModelsRemovalSimplifiers();
        assertEquals(4, simplifiers.size());
        ModelSimplifierInfo info = simplifiers.getFirst().getSimplifierInfo();
        ModelSimplifierInfo expected = new ModelSimplifierInfo("Filter", "Filter Test", SimplifierType.REMOVAL);
        assertEquals(expected, info);
    }

    @Test
    void loadSubstitutionSimplifiers() {
        List<ModelsSubstitutionSimplifier> simplifiers = modelSimplifiers.getModelsSubstitutionSimplifiers();
        assertEquals(2, simplifiers.size());
        ModelSimplifierInfo info = simplifiers.getFirst().getSimplifierInfo();
        ModelSimplifierInfo expected = new ModelSimplifierInfo("Substitution", "Substitution Test", SimplifierType.SUBSTITUTION);
        assertEquals(expected, info);
    }

    @Test
    void simplifyModels() {
        Network network = SvcTestCaseFactory.create();
        List<BlackBoxModel> dynamicModels = List.of(
                BaseGeneratorBuilder.of(network, "GeneratorPVFixed")
                        .staticId("G1")
                        .parameterSetId("GPV")
                        .build(),
                BaseLoadBuilder.of(network, "LoadAlphaBeta")
                        .staticId("L2")
                        .parameterSetId("LOAD")
                        .build(),
                BaseStaticVarCompensatorBuilder.of(network, "StaticVarCompensator")
                        .staticId("SVC2")
                        .parameterSetId("svc")
                        .build());
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .dynawoParameters(DynawoSimulationParameters.load()
                        .addModelSimplifier("Filter")
                        .addModelSimplifier("Substitution"))
                .build();
        assertEquals(2, context.getBlackBoxDynamicModels().size());
        assertFalse(context.getBlackBoxDynamicModels().stream().anyMatch(bbm -> bbm.getDynamicModelId().equalsIgnoreCase("L2")));
        assertTrue(context.getBlackBoxDynamicModels().stream().anyMatch(bbm -> bbm.getDynamicModelId().equalsIgnoreCase("G1") && bbm.getLib().equalsIgnoreCase("GeneratorFictitious")));
    }

    @AutoService(ModelsRemovalSimplifier.class)
    public static class ModelsSimplifierFilter implements ModelsRemovalSimplifier {

        @Override
        public Predicate<BlackBoxModel> getModelRemovalPredicate(ReportNode reportNode) {
            return m -> !m.getDynamicModelId().equalsIgnoreCase("L2");
        }

        @Override
        public ModelSimplifierInfo getSimplifierInfo() {
            return new ModelSimplifierInfo("Filter", "Filter Test", SIMPLIFIER_TYPE);
        }
    }

    @AutoService(ModelsSubstitutionSimplifier.class)
    public static class ModelsSimplifierSubstitution implements ModelsSubstitutionSimplifier {

        @Override
        public Function<BlackBoxModel, BlackBoxModel> getModelSubstitutionFunction(Network network, DynawoSimulationParameters dynawoSimulationParameters, ReportNode reportNode) {
            return m -> {
                if ("G1".equalsIgnoreCase(m.getDynamicModelId()) && m instanceof BaseGenerator gen) {
                    return BaseGeneratorBuilder.of(network, "GeneratorFictitious")
                            .staticId(gen.getDynamicModelId())
                            .parameterSetId("G")
                            .build();
                }
                return m;
            };
        }

        @Override
        public ModelSimplifierInfo getSimplifierInfo() {
            return new ModelSimplifierInfo("Substitution", "Substitution Test", SIMPLIFIER_TYPE);
        }
    }

    @AutoService(ModelsRemovalSimplifier.class)
    public static class ModelsSimplifierFilter2 implements ModelsRemovalSimplifier {

        @Override
        public Predicate<BlackBoxModel> getModelRemovalPredicate(ReportNode reportNode) {
            return m -> true;
        }

        @Override
        public ModelSimplifierInfo getSimplifierInfo() {
            return new ModelSimplifierInfo("Filter 2", "Filter 2 Test", SIMPLIFIER_TYPE);
        }
    }

    @AutoService(ModelsSubstitutionSimplifier.class)
    public static class ModelsSimplifierSubstitution2 implements ModelsSubstitutionSimplifier {

        @Override
        public Function<BlackBoxModel, BlackBoxModel> getModelSubstitutionFunction(Network network, DynawoSimulationParameters dynawoSimulationParameters, ReportNode reportNode) {
            return Function.identity();
        }

        @Override
        public ModelSimplifierInfo getSimplifierInfo() {
            return new ModelSimplifierInfo("Substitution 2", "Substitution Test 2", SIMPLIFIER_TYPE);
        }
    }
}
