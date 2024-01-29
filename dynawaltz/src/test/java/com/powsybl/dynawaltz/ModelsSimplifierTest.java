/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawaltz.models.generators.AbstractGenerator;
import com.powsybl.dynawaltz.models.generators.GeneratorFictitiousBuilder;
import com.powsybl.dynawaltz.models.loads.BaseLoadBuilder;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatioBuilder;
import com.powsybl.dynawaltz.models.BlackBoxModel;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
class ModelsSimplifierTest {

    @Test
    void loadSimplifiers() {
        List<ModelsSimplifier> simplifiers = Lists.newArrayList(ServiceLoader.load(ModelsSimplifier.class));
        assertEquals(2, simplifiers.size());
    }

    @Test
    void simplifyModels() {
        Network network = EurostagTutorialExample1Factory.create();
        DynamicSimulationParameters parameters = DynamicSimulationParameters.load();
        DynaWaltzParameters dynawoParameters = DynaWaltzParameters.load().setUseModelSimplifiers(true);
        List<BlackBoxModel> dynamicModels = List.of(
                GeneratorFictitiousBuilder.of(network)
                        .dynamicModelId("BBM_GEN")
                        .staticId("GEN")
                        .parameterSetId("GPV")
                        .build(),
                BaseLoadBuilder.of(network, "LoadAlphaBeta")
                        .dynamicModelId("BBM_LOAD")
                        .staticId("LOAD")
                        .parameterSetId("LOAD")
                        .build(),
                TransformerFixedRatioBuilder.of(network)
                        .dynamicModelId("BBM_NGEN_NHV1")
                        .staticId("NGEN_NHV1")
                        .parameterSetId("TR")
                        .build());
        DynaWaltzContext context = new DynaWaltzContext(network, network.getVariantManager().getWorkingVariantId(), dynamicModels, Collections.emptyList(), Collections.emptyList(), parameters, dynawoParameters);
        assertEquals(2, context.getBlackBoxDynamicModels().size());
        assertFalse(context.getBlackBoxDynamicModelStream().anyMatch(bbm -> bbm.getDynamicModelId().equalsIgnoreCase("BBM_LOAD")));
        assertTrue(context.getBlackBoxDynamicModelStream().anyMatch(bbm -> bbm.getDynamicModelId().equalsIgnoreCase("newModel")));
    }

    @AutoService(ModelsSimplifier.class)
    public static class ModelsSimplifierFilter implements ModelsSimplifier {
        @Override
        public Stream<BlackBoxModel> simplifyModels(Stream<BlackBoxModel> models, Network network, DynaWaltzParameters dynaWaltzParameters, Reporter reporter) {
            return models.filter(m -> !m.getDynamicModelId().equalsIgnoreCase("BBM_LOAD"));
        }
    }

    @AutoService(ModelsSimplifier.class)
    public static class ModelsSimplifierSubstitution implements ModelsSimplifier {
        @Override
        public Stream<BlackBoxModel> simplifyModels(Stream<BlackBoxModel> models, Network network, DynaWaltzParameters dynaWaltzParameters, Reporter reporter) {
            return models.map(m -> {
                if ("BBM_GEN".equalsIgnoreCase(m.getDynamicModelId()) && m instanceof AbstractGenerator gen) {
                    return GeneratorFictitiousBuilder.of(network)
                            .dynamicModelId("newModel")
                            .staticId(gen.getStaticId())
                            .parameterSetId("G")
                            .build();
                }
                return m;
            });
        }
    }
}
