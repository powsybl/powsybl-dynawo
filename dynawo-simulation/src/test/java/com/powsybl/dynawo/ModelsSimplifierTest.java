/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.models.generators.BaseGenerator;
import com.powsybl.dynawo.models.generators.BaseGeneratorBuilder;
import com.powsybl.dynawo.models.loads.BaseLoadBuilder;
import com.powsybl.dynawo.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ModelsSimplifierTest {

    @Test
    void loadRemovalSimplifiers() {
        List<ModelsRemovalSimplifier> simplifiers = Lists.newArrayList(ServiceLoader.load(ModelsRemovalSimplifier.class));
        assertEquals(1, simplifiers.size());
    }

    @Test
    void loadSubstitutionSimplifiers() {
        List<ModelsSubstitutionSimplifier> simplifiers = Lists.newArrayList(ServiceLoader.load(ModelsSubstitutionSimplifier.class));
        assertEquals(1, simplifiers.size());
    }

    @Test
    void simplifyModels() {
        Network network = EurostagTutorialExample1Factory.create();
        List<BlackBoxModel> dynamicModels = List.of(
                BaseGeneratorBuilder.of(network, "GeneratorPVFixed")
                        .staticId("GEN")
                        .parameterSetId("GPV")
                        .build(),
                BaseLoadBuilder.of(network, "LoadAlphaBeta")
                        .staticId("LOAD")
                        .parameterSetId("LOAD")
                        .build(),
                TransformerFixedRatioBuilder.of(network)
                        .staticId("NGEN_NHV1")
                        .parameterSetId("TR")
                        .build());
        DynawoSimulationContext context = new DynawoSimulationContext
                .Builder(network, dynamicModels)
                .dynawoParameters(DynawoSimulationParameters.load().setUseModelSimplifiers(true))
                .build();
        assertEquals(2, context.getBlackBoxDynamicModels().size());
        assertFalse(context.getBlackBoxDynamicModels().stream().anyMatch(bbm -> bbm.getDynamicModelId().equalsIgnoreCase("LOAD")));
        assertTrue(context.getBlackBoxDynamicModels().stream().anyMatch(bbm -> bbm.getDynamicModelId().equalsIgnoreCase("GEN") && bbm.getLib().equalsIgnoreCase("GeneratorFictitious")));
    }

    @AutoService(ModelsRemovalSimplifier.class)
    public static class ModelsSimplifierFilter implements ModelsRemovalSimplifier {
        @Override
        public Predicate<BlackBoxModel> getModelRemovalPredicate(ReportNode reportNode) {
            return m -> !m.getDynamicModelId().equalsIgnoreCase("LOAD");
        }
    }

    @AutoService(ModelsSubstitutionSimplifier.class)
    public static class ModelsSimplifierSubstitution implements ModelsSubstitutionSimplifier {
        @Override
        public Function<BlackBoxModel, BlackBoxModel> getModelSubstitutionFunction(Network network, DynawoSimulationParameters dynawoSimulationParameters, ReportNode reportNode) {
            return m -> {
                if ("GEN".equalsIgnoreCase(m.getDynamicModelId()) && m instanceof BaseGenerator gen) {
                    return BaseGeneratorBuilder.of(network, "GeneratorFictitious")
                            .staticId(gen.getDynamicModelId())
                            .parameterSetId("G")
                            .build();
                }
                return m;
            };
        }
    }
}
