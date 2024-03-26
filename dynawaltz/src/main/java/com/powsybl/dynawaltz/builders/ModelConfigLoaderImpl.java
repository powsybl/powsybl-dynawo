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
import com.powsybl.dynawaltz.models.automatons.currentlimits.CurrentLimitAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.currentlimits.CurrentLimitTwoLevelsAutomatonBuilder;
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
            new BuilderConfig("CLA", CurrentLimitAutomatonBuilder::of, CurrentLimitAutomatonBuilder::getSupportedLibs),
            new BuilderConfig("CLA_TWO_LEVElS", CurrentLimitTwoLevelsAutomatonBuilder::of, CurrentLimitTwoLevelsAutomatonBuilder::getSupportedLibs),
            new BuilderConfig("TAP_CHANGER", TapChangerAutomatonBuilder::of, TapChangerAutomatonBuilder::getSupportedLibs),
            new BuilderConfig("TCB", TapChangerBlockingAutomatonBuilder::of, TapChangerBlockingAutomatonBuilder::getSupportedLibs),
            new BuilderConfig("UNDER_VOLTAGE", UnderVoltageAutomatonBuilder::of, UnderVoltageAutomatonBuilder::getSupportedLibs),
            new BuilderConfig("PHASE_SHIFTER_P", PhaseShifterPAutomatonBuilder::of, PhaseShifterPAutomatonBuilder::getSupportedLibs),
            new BuilderConfig("PHASE_SHIFTER_I", PhaseShifterIAutomatonBuilder::of, PhaseShifterIAutomatonBuilder::getSupportedLibs),
            new BuilderConfig("BUS", StandardBusBuilder::of, StandardBusBuilder::getSupportedLibs),
            new BuilderConfig("INFINITE_BUS", InfiniteBusBuilder::of, InfiniteBusBuilder::getSupportedLibs),
            new BuilderConfig("TRANSFORMER", TransformerFixedRatioBuilder::of, TransformerFixedRatioBuilder::getSupportedLibs),
            new BuilderConfig("BASE_LINE", LineBuilder::of, LineBuilder::getSupportedLibs),
            new BuilderConfig("HVDC_VSC", HvdcVscBuilder::of, HvdcVscBuilder::getSupportedLibs),
            new BuilderConfig("HVDC_P", HvdcPBuilder::of, HvdcPBuilder::getSupportedLibs),
            new BuilderConfig("BASE_LOAD", BaseLoadBuilder::of, BaseLoadBuilder::getSupportedLibs),
            new BuilderConfig("LOT", LoadOneTransformerBuilder::of, LoadOneTransformerBuilder::getSupportedLibs),
            new BuilderConfig("LOTTC", LoadOneTransformerTapChangerBuilder::of, LoadOneTransformerTapChangerBuilder::getSupportedLibs),
            new BuilderConfig("LTT", LoadTwoTransformersBuilder::of, LoadTwoTransformersBuilder::getSupportedLibs),
            new BuilderConfig("LTTTC", LoadTwoTransformersTapChangersBuilder::of, LoadTwoTransformersTapChangersBuilder::getSupportedLibs),
            new BuilderConfig("BASE_SVC", BaseStaticVarCompensatorBuilder::of, BaseStaticVarCompensatorBuilder::getSupportedLibs),
            new BuilderConfig("GENERATOR_FICTITIOUS", GeneratorFictitiousBuilder::of, GeneratorFictitiousBuilder::getSupportedLibs),
            new BuilderConfig("SYNCHRONIZED_GENERATOR", SynchronizedGeneratorBuilder::of, SynchronizedGeneratorBuilder::getSupportedLibs),
            new BuilderConfig("SYNCHRONOUS_GENERATOR", SynchronousGeneratorBuilder::of, SynchronousGeneratorBuilder::getSupportedLibs),
            new BuilderConfig("WECC", WeccBuilder::of, WeccBuilder::getSupportedLibs),
            new BuilderConfig("GRID_FORMING", GridFormingConverterBuilder::of, GridFormingConverterBuilder::getSupportedLibs));

    @Override
    public String getModelConfigFileName() {
        return MODEL_CONFIG_FILENAME;
    }

    @Override
    public Stream<BuilderConfig> loadBuilderConfigs() {
        return BUILDER_CONFIGS;
    }
}
