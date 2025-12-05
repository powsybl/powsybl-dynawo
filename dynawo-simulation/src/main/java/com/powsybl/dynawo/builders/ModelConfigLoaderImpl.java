/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.builders;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auto.service.AutoService;
import com.powsybl.commons.PowsyblException;
import com.powsybl.dynawo.models.automationsystems.TapChangerAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.TapChangerBlockingAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.UnderVoltageAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.HighVoltageRideThroughAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.overloadmanagments.DynamicTwoLevelOverloadManagementSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterBlockingIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterIAutomationSystemBuilder;
import com.powsybl.dynawo.models.automationsystems.phaseshifters.PhaseShifterPAutomationSystemBuilder;
import com.powsybl.dynawo.models.buses.InfiniteBusBuilder;
import com.powsybl.dynawo.models.buses.StandardBusBuilder;
import com.powsybl.dynawo.models.events.EventActivePowerVariationBuilder;
import com.powsybl.dynawo.models.events.EventDisconnectionBuilder;
import com.powsybl.dynawo.models.events.EventReactivePowerVariationBuilder;
import com.powsybl.dynawo.models.events.NodeFaultEventBuilder;
import com.powsybl.dynawo.models.generators.*;
import com.powsybl.dynawo.models.loads.*;
import com.powsybl.dynawo.models.hvdc.HvdcPBuilder;
import com.powsybl.dynawo.models.hvdc.HvdcVscBuilder;
import com.powsybl.dynawo.models.lines.LineBuilder;
import com.powsybl.dynawo.models.shunts.BaseShuntBuilder;
import com.powsybl.dynawo.models.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawo.models.transformers.TransformerFixedRatioBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
@AutoService(ModelConfigLoader.class)
public final class ModelConfigLoaderImpl implements ModelConfigLoader {

    private static final String MODEL_CONFIG_FILENAME = "models.json";

    private static final Stream<BuilderConfig> BUILDER_CONFIGS = Stream.of(
            new BuilderConfig(DynamicOverloadManagementSystemBuilder.CATEGORY, DynamicOverloadManagementSystemBuilder::of, DynamicOverloadManagementSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(DynamicTwoLevelOverloadManagementSystemBuilder.CATEGORY, DynamicTwoLevelOverloadManagementSystemBuilder::of, DynamicTwoLevelOverloadManagementSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(TapChangerAutomationSystemBuilder.CATEGORY, TapChangerAutomationSystemBuilder::of, TapChangerAutomationSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(TapChangerBlockingAutomationSystemBuilder.CATEGORY, TapChangerBlockingAutomationSystemBuilder::of, TapChangerBlockingAutomationSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(UnderVoltageAutomationSystemBuilder.CATEGORY, UnderVoltageAutomationSystemBuilder::of, UnderVoltageAutomationSystemBuilder::getSupportedModelInfos),
            new BuilderConfig(HighVoltageRideThroughAutomationSystemBuilder.CATEGORY, HighVoltageRideThroughAutomationSystemBuilder::of, HighVoltageRideThroughAutomationSystemBuilder::getSupportedModelInfos),
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
            new BuilderConfig(BaseGeneratorBuilder.CATEGORY, BaseGeneratorBuilder::of, BaseGeneratorBuilder::getSupportedModelInfos),
            new BuilderConfig(GeneratorAlphaBetaBuilder.CATEGORY, GeneratorAlphaBetaBuilder::of, GeneratorAlphaBetaBuilder::getSupportedModelInfos),
            new BuilderConfig(DynGridFormingVirtualSynchronousMachineBuilder.CATEGORY, DynGridFormingVirtualSynchronousMachineBuilder::of, DynGridFormingVirtualSynchronousMachineBuilder::getSupportedModelInfos),
            new BuilderConfig(DynGridFollowingBuilder.CATEGORY, DynGridFollowingBuilder::of, DynGridFollowingBuilder::getSupportedModelInfos),
            new BuilderConfig(SynchronizedGeneratorBuilder.CATEGORY, SynchronizedGeneratorBuilder::of, SynchronizedGeneratorBuilder::getSupportedModelInfos),
            new BuilderConfig(SynchronousGeneratorBuilder.CATEGORY, SynchronousGeneratorBuilder::of, SynchronousGeneratorBuilder::getSupportedModelInfos),
            new BuilderConfig(WeccBuilder.CATEGORY, WeccBuilder::of, WeccBuilder::getSupportedModelInfos),
            new BuilderConfig(GridFormingConverterBuilder.CATEGORY, GridFormingConverterBuilder::of, GridFormingConverterBuilder::getSupportedModelInfos),
            new BuilderConfig(SignalNGeneratorBuilder.CATEGORY, SignalNGeneratorBuilder::of, SignalNGeneratorBuilder::getSupportedModelInfos));

    private static final Stream<EventBuilderConfig> EVENT_BUILDER_CONFIGS = Stream.of(
            new EventBuilderConfig(EventActivePowerVariationBuilder::of, EventActivePowerVariationBuilder.getModelInfo()),
            new EventBuilderConfig(EventReactivePowerVariationBuilder::of, EventReactivePowerVariationBuilder.getModelInfo()),
            new EventBuilderConfig(EventDisconnectionBuilder::of, EventDisconnectionBuilder.getModelInfo()),
            new EventBuilderConfig(NodeFaultEventBuilder::of, NodeFaultEventBuilder.getModelInfo()));

    @Override
    public Map<String, ModelConfigs> loadModelConfigs() {
        try {
            ObjectMapper objectMapper = ModelConfigLoader.getModelConfigObjectMapper();
            return objectMapper.readValue(Objects.requireNonNull(
                            ModelConfigLoader.class.getClassLoader().getResource(MODEL_CONFIG_FILENAME)).openStream(),
                    new TypeReference<>() {
                    });
        } catch (IOException e) {
            throw new PowsyblException("Dynamic models configuration file not found");
        }
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
