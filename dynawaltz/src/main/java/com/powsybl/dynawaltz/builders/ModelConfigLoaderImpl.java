/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.google.auto.service.AutoService;
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.UnderVoltageAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.currentLimits.CurrentLimitAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.currentLimits.CurrentLimitTwoLevelsAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomatonBuilder;
import com.powsybl.dynawaltz.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawaltz.models.buses.StandardBusBuilder;
import com.powsybl.dynawaltz.models.generators.*;
import com.powsybl.dynawaltz.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawaltz.models.lines.LineBuilder;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.dynawaltz.models.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawaltz.models.transformers.TransformerFixedRatioBuilder;

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
            new BuilderConfig(GridFormingConverterBuilder::of, GridFormingConverterBuilder::getSupportedLibs),
            new BuilderConfig(GeneratorFictitiousBuilder::of, GeneratorFictitiousBuilder::getSupportedLibs));

    public ModelConfigLoaderImpl() {
    }

    @Override
    public String getModelConfigFileName() {
        return MODEL_CONFIG_FILENAME;
    }

    @Override
    public Stream<BuilderConfig> loadBuilderConfigs() {
        return BUILDER_CONFIGS;
    }
}
