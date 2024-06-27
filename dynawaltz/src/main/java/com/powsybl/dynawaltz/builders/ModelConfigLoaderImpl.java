/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.google.auto.service.AutoService;
import com.powsybl.dynawaltz.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.overloadmanagments.DynamicTwoLevelsOverloadManagementSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
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
            new BuilderConfig(DynamicOverloadManagementSystemBuilder.CATEGORY, DynamicOverloadManagementSystemBuilder::of, DynamicOverloadManagementSystemBuilder::getSupportedLibs),
            new BuilderConfig(DynamicTwoLevelsOverloadManagementSystemBuilder.CATEGORY, DynamicTwoLevelsOverloadManagementSystemBuilder::of, DynamicTwoLevelsOverloadManagementSystemBuilder::getSupportedLibs),
            new BuilderConfig(TapChangerAutomationSystemBuilder.CATEGORY, TapChangerAutomationSystemBuilder::of, TapChangerAutomationSystemBuilder::getSupportedLibs),
            new BuilderConfig(TapChangerBlockingAutomationSystemBuilder.CATEGORY, TapChangerBlockingAutomationSystemBuilder::of, TapChangerBlockingAutomationSystemBuilder::getSupportedLibs),
            new BuilderConfig(UnderVoltageAutomationSystemBuilder.CATEGORY, UnderVoltageAutomationSystemBuilder::of, UnderVoltageAutomationSystemBuilder::getSupportedLibs),
            new BuilderConfig(PhaseShifterPAutomationSystemBuilder.CATEGORY, PhaseShifterPAutomationSystemBuilder::of, PhaseShifterPAutomationSystemBuilder::getSupportedLibs),
            new BuilderConfig(PhaseShifterIAutomationSystemBuilder.CATEGORY, PhaseShifterIAutomationSystemBuilder::of, PhaseShifterIAutomationSystemBuilder::getSupportedLibs),
            new BuilderConfig(StandardBusBuilder.CATEGORY, StandardBusBuilder::of, StandardBusBuilder::getSupportedLibs),
            new BuilderConfig(InfiniteBusBuilder.CATEGORY, InfiniteBusBuilder::of, InfiniteBusBuilder::getSupportedLibs),
            new BuilderConfig(TransformerFixedRatioBuilder.CATEGORY, TransformerFixedRatioBuilder::of, TransformerFixedRatioBuilder::getSupportedLibs),
            new BuilderConfig(LineBuilder.CATEGORY, LineBuilder::of, LineBuilder::getSupportedLibs),
            new BuilderConfig(HvdcVscBuilder.CATEGORY, HvdcVscBuilder::of, HvdcVscBuilder::getSupportedLibs),
            new BuilderConfig(HvdcPBuilder.CATEGORY, HvdcPBuilder::of, HvdcPBuilder::getSupportedLibs),
            new BuilderConfig(BaseLoadBuilder.CATEGORY, BaseLoadBuilder::of, BaseLoadBuilder::getSupportedLibs),
            new BuilderConfig(LoadOneTransformerBuilder.CATEGORY, LoadOneTransformerBuilder::of, LoadOneTransformerBuilder::getSupportedLibs),
            new BuilderConfig(LoadOneTransformerTapChangerBuilder.CATEGORY, LoadOneTransformerTapChangerBuilder::of, LoadOneTransformerTapChangerBuilder::getSupportedLibs),
            new BuilderConfig(LoadTwoTransformersBuilder.CATEGORY, LoadTwoTransformersBuilder::of, LoadTwoTransformersBuilder::getSupportedLibs),
            new BuilderConfig(LoadTwoTransformersTapChangersBuilder.CATEGORY, LoadTwoTransformersTapChangersBuilder::of, LoadTwoTransformersTapChangersBuilder::getSupportedLibs),
            new BuilderConfig(BaseStaticVarCompensatorBuilder.CATEGORY, BaseStaticVarCompensatorBuilder::of, BaseStaticVarCompensatorBuilder::getSupportedLibs),
            new BuilderConfig(GeneratorFictitiousBuilder.CATEGORY, GeneratorFictitiousBuilder::of, GeneratorFictitiousBuilder::getSupportedLibs),
            new BuilderConfig(SynchronizedGeneratorBuilder.CATEGORY, SynchronizedGeneratorBuilder::of, SynchronizedGeneratorBuilder::getSupportedLibs),
            new BuilderConfig(SynchronousGeneratorBuilder.CATEGORY, SynchronousGeneratorBuilder::of, SynchronousGeneratorBuilder::getSupportedLibs),
            new BuilderConfig(WeccBuilder.CATEGORY, WeccBuilder::of, WeccBuilder::getSupportedLibs),
            new BuilderConfig(GridFormingConverterBuilder.CATEGORY, GridFormingConverterBuilder::of, GridFormingConverterBuilder::getSupportedLibs));

    @Override
    public String getModelConfigFileName() {
        return MODEL_CONFIG_FILENAME;
    }

    @Override
    public Stream<BuilderConfig> loadBuilderConfigs() {
        return BUILDER_CONFIGS;
    }
}
