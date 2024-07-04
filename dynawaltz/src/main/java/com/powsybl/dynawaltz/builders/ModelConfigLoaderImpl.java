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
import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterBlockingIAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
import com.powsybl.dynawaltz.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawaltz.models.buses.StandardBusBuilder;
import com.powsybl.dynawaltz.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawaltz.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawaltz.models.events.NodeFaultEventBuilder;
import com.powsybl.dynawaltz.models.generators.*;
import com.powsybl.dynawaltz.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawaltz.models.lines.LineBuilder;
import com.powsybl.dynawaltz.models.loads.*;
import com.powsybl.dynawaltz.models.shunts.BaseShuntBuilder;
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
            new BuilderConfig(DynamicOverloadManagementSystemBuilder.CATEGORY, DynamicOverloadManagementSystemBuilder::of, DynamicOverloadManagementSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(DynamicTwoLevelsOverloadManagementSystemBuilder.CATEGORY, DynamicTwoLevelsOverloadManagementSystemBuilder::of, DynamicTwoLevelsOverloadManagementSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(TapChangerAutomationSystemBuilder.CATEGORY, TapChangerAutomationSystemBuilder::of, TapChangerAutomationSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(TapChangerBlockingAutomationSystemBuilder.CATEGORY, TapChangerBlockingAutomationSystemBuilder::of, TapChangerBlockingAutomationSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(UnderVoltageAutomationSystemBuilder.CATEGORY, UnderVoltageAutomationSystemBuilder::of, UnderVoltageAutomationSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(PhaseShifterPAutomationSystemBuilder.CATEGORY, PhaseShifterPAutomationSystemBuilder::of, PhaseShifterPAutomationSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(PhaseShifterIAutomationSystemBuilder.CATEGORY, PhaseShifterIAutomationSystemBuilder::of, PhaseShifterIAutomationSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(PhaseShifterBlockingIAutomationSystemBuilder.CATEGORY, PhaseShifterBlockingIAutomationSystemBuilder::of, PhaseShifterBlockingIAutomationSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(StandardBusBuilder.CATEGORY, StandardBusBuilder::of, StandardBusBuilder::getSupportedModelInfos),
            new BuilderConfig(InfiniteBusBuilder.CATEGORY, InfiniteBusBuilder::of, InfiniteBusBuilder::getSupportedModelInfos),
            new BuilderConfig(TransformerFixedRatioBuilder.CATEGORY, TransformerFixedRatioBuilder::of, TransformerFixedRatioBuilder::getSupportedModelInfos),
            new BuilderConfig(LineBuilder.CATEGORY, LineBuilder::of, LineBuilder::getSupportedModelInfos),
            new BuilderConfig(HvdcVscBuilder.CATEGORY, HvdcVscBuilder::of, HvdcVscBuilder::getSupportedModelInfos),
            new BuilderConfig(HvdcPBuilder.CATEGORY, HvdcPBuilder::of, HvdcPBuilder::getSupportedModelInfos),
            new BuilderConfig(BaseLoadBuilder.CATEGORY, BaseLoadBuilder::of, BaseLoadBuilder::getSupportedModelInfos),
            new BuilderConfig(LoadOneTransformerBuilder.CATEGORY, LoadOneTransformerBuilder::of, LoadOneTransformerBuilder::getSupportedModelInfos),
            new BuilderConfig(LoadOneTransformerTapChangerBuilder.CATEGORY, LoadOneTransformerTapChangerBuilder::of, LoadOneTransformerTapChangerBuilder::getSupportedModelInfos),
            new BuilderConfig(LoadTwoTransformersBuilder.CATEGORY, LoadTwoTransformersBuilder::of, LoadTwoTransformersBuilder::getSupportedModelInfos),
            new BuilderConfig(LoadTwoTransformersTapChangersBuilder.CATEGORY, LoadTwoTransformersTapChangersBuilder::of, LoadTwoTransformersTapChangersBuilder::getSupportedModelInfos),
            new BuilderConfig(BaseShuntBuilder.CATEGORY, BaseShuntBuilder::of, BaseShuntBuilder::getSupportedModelInfos),
            new BuilderConfig(BaseStaticVarCompensatorBuilder.CATEGORY, BaseStaticVarCompensatorBuilder::of, BaseStaticVarCompensatorBuilder::getSupportedModelInfos),
            new BuilderConfig(GeneratorFictitiousBuilder.CATEGORY, GeneratorFictitiousBuilder::of, GeneratorFictitiousBuilder::getSupportedModelInfos),
            new BuilderConfig(SynchronizedGeneratorBuilder.CATEGORY, SynchronizedGeneratorBuilder::of, SynchronizedGeneratorBuilder::getSupportedModelInfos),
            new BuilderConfig(SynchronousGeneratorBuilder.CATEGORY, SynchronousGeneratorBuilder::of, SynchronousGeneratorBuilder::getSupportedModelInfos),
            new BuilderConfig(WeccBuilder.CATEGORY, WeccBuilder::of, WeccBuilder::getSupportedModelInfos),
            new BuilderConfig(GridFormingConverterBuilder.CATEGORY, GridFormingConverterBuilder::of, GridFormingConverterBuilder::getSupportedModelInfos));

    private static final Stream<EventBuilderConfig> EVENT_BUILDER_CONFIGS = Stream.of(
            new EventBuilderConfig(EventActivePowerVariationBuilder::of, EventActivePowerVariationBuilder.TAG, EventActivePowerVariationBuilder.getInfo()),
            new EventBuilderConfig(EventDisconnectionBuilder::of, EventDisconnectionBuilder.TAG, EventDisconnectionBuilder.getInfo()),
            new EventBuilderConfig(NodeFaultEventBuilder::of, NodeFaultEventBuilder.TAG, NodeFaultEventBuilder.getInfo()));

    @Override
    public String getModelConfigFileName() {
        return MODEL_CONFIG_FILENAME;
    }

    @Override
    public Stream<BuilderConfig> loadBuilderConfigs() {
        return BUILDER_CONFIGS;
    }

    @Override
    public Stream<EventBuilderConfig> loadEventBuilderConfigs() {
        return EVENT_BUILDER_CONFIGS;
    }
}
