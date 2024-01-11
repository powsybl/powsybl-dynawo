/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.service.AutoService;
import com.powsybl.dynawaltz.builders.buses.InfiniteBusBuilder;
import com.powsybl.dynawaltz.builders.buses.StandardBusBuilder;
import com.powsybl.dynawaltz.builders.generators.GeneratorFictitiousBuilder;
import com.powsybl.dynawaltz.builders.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawaltz.builders.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawaltz.builders.generators.WeccBuilder;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcPBuilder;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcVscBuilder;
import com.powsybl.dynawaltz.builders.lines.LineBuilder;
import com.powsybl.dynawaltz.builders.loads.*;
import com.powsybl.dynawaltz.builders.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawaltz.builders.transformers.TransformerFixedRatioBuilder;
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.UnderVoltageAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.currentLimits.CurrentLimitAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.currentLimits.CurrentLimitTwoLevelsAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomatonBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ModelConfigLoader.class)
public final class ModelConfigLoaderImpl implements ModelConfigLoader {

    private static final String MODEL_CONFIG_FILENAME = "models.json";

    private static final Stream<BuilderConfig> BUILDER_CONFIGS = Stream.of(
            new BuilderConfig(CurrentLimitAutomatonBuilder::of, CurrentLimitAutomatonBuilder::getSupportedLibs),
            new BuilderConfig(CurrentLimitTwoLevelsAutomatonBuilder::of, CurrentLimitTwoLevelsAutomatonBuilder::getSupportedLibs),
            new BuilderConfig(TapChangerAutomatonBuilder::of, TapChangerAutomatonBuilder::getSupportedLibs),
            new BuilderConfig(TapChangerBlockingAutomatonBuilder::of, TapChangerBlockingAutomatonBuilder::getSupportedLibs),
            new BuilderConfig(UnderVoltageAutomatonBuilder::of, UnderVoltageAutomatonBuilder::getSupportedLibs),
            new BuilderConfig(PhaseShifterPAutomatonBuilder::of, PhaseShifterPAutomatonBuilder::getSupportedLibs),
            new BuilderConfig(PhaseShifterIAutomatonBuilder::of, PhaseShifterIAutomatonBuilder::getSupportedLibs),
            new BuilderConfig(StandardBusBuilder::of, StandardBusBuilder::getSupportedLibs),
            new BuilderConfig(InfiniteBusBuilder::of, InfiniteBusBuilder::getSupportedLibs),
            new BuilderConfig(TransformerFixedRatioBuilder::of, TransformerFixedRatioBuilder::getSupportedLibs),
            new BuilderConfig(LineBuilder::of, LineBuilder::getSupportedLibs),
            new BuilderConfig(HvdcVscBuilder::of, HvdcVscBuilder::getSupportedLibs),
            new BuilderConfig(HvdcPBuilder::of, HvdcPBuilder::getSupportedLibs),
            new BuilderConfig(BaseLoadBuilder::of, BaseLoadBuilder::getSupportedLibs),
            new BuilderConfig(LoadOneTransformerBuilder::of, LoadOneTransformerBuilder::getSupportedLibs),
            new BuilderConfig(LoadOneTransformerTapChangerBuilder::of, LoadOneTransformerTapChangerBuilder::getSupportedLibs),
            new BuilderConfig(LoadTwoTransformersBuilder::of, LoadTwoTransformersBuilder::getSupportedLibs),
            new BuilderConfig(LoadTwoTransformersTapChangersBuilder::of, LoadTwoTransformersTapChangersBuilder::getSupportedLibs),
            new BuilderConfig(BaseStaticVarCompensatorBuilder::of, BaseStaticVarCompensatorBuilder::getSupportedLibs),
            new BuilderConfig(GeneratorFictitiousBuilder::of, GeneratorFictitiousBuilder::getSupportedLibs),
            new BuilderConfig(SynchronizedGeneratorBuilder::of, SynchronizedGeneratorBuilder::getSupportedLibs),
            new BuilderConfig(SynchronousGeneratorBuilder::of, SynchronousGeneratorBuilder::getSupportedLibs),
            new BuilderConfig(WeccBuilder::of, WeccBuilder::getSupportedLibs),
            new BuilderConfig(GeneratorFictitiousBuilder::of, GeneratorFictitiousBuilder::getSupportedLibs));

    public ModelConfigLoaderImpl() {
    }

    @Override
    public Map<String, List<ModelConfig>> loadModelConfigs() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(ModelConfigLoaderImpl.class.getClassLoader().getResource(MODEL_CONFIG_FILENAME), new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Stream<BuilderConfig> loadBuilderConfigs() {
        return BUILDER_CONFIGS;
    }
}
