/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.buses.InfiniteBusBuilder;
import com.powsybl.dynawaltz.builders.buses.StandardBusBuilder;
import com.powsybl.dynawaltz.builders.generators.*;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcPBuilder;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcVscBuilder;
import com.powsybl.dynawaltz.builders.lines.LineBuilder;
import com.powsybl.dynawaltz.builders.loads.*;
import com.powsybl.dynawaltz.builders.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawaltz.builders.transformers.TransformerFixedRatioBuilder;
import com.powsybl.dynawaltz.models.automatons.currentLimits.CurrentLimitAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.currentLimits.CurrentLimitTwoLevelsAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterIAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.phaseshifters.PhaseShifterPAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.TapChangerBlockingAutomatonBuilder;
import com.powsybl.dynawaltz.models.automatons.UnderVoltageAutomatonBuilder;
import com.powsybl.iidm.network.Network;

import static com.powsybl.dynawaltz.builders.DynamicModelBuilderUtils.Categories.*;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynamicModelBuilderUtils {

    //TODO replace inner class
    public enum Categories implements BuilderCategory {
        // EQUIPMENTS
        BASE_LOADS("baseLoads", BaseLoadBuilder::new),
        LOADS_ONE_TRANSFORMER("loadsOneTransformer", LoadOneTransformerBuilder::new),
        LOADS_ONE_TRANSFORMER_TAP_CHANGER("loadsOneTransformerTapChanger", LoadOneTransformerTapChangerBuilder::new),
        LOADS_TWO_TRANSFORMERS("loadsTwoTransformers", LoadTwoTransformersBuilder::new),
        LOADS_TWO_TRANSFORMERS_TAP_CHANGERS("loadsTwoTransformersTapChanger", LoadTwoTransformersTapChangersBuilder::new),
        BASE_LINES("baseLines", LineBuilder::new),
        BASE_SVARCS("staticVarCompensators", BaseStaticVarCompensatorBuilder::new),
        HVDC_P("hvdcP", HvdcPBuilder::new),
        HVDC_VSC("hvdcVsc", HvdcVscBuilder::new),
        BASE_BUSES("baseBuses", StandardBusBuilder::new),
        INFINITE_BUSES("infiniteBuses", InfiniteBusBuilder::new),
        BASE_GENERATORS("baseGenerators", GeneratorFictitiousBuilder::new),
        SYNCHRONIZED_GENERATORS("synchronizedGenerators", SynchronizedGeneratorBuilder::new),
        SYNCHRONOUS_GENERATORS("synchronousGenerators", SynchronousGeneratorBuilder::new),
        WECC_GEN("wecc", WeccBuilder::new),
        GRID_FORMING_CONVERTER("gridFormingConverter", GridFormingConverterBuilder::new),
        TRANSFORMERS("transformers", TransformerFixedRatioBuilder::new),
        // AUTOMATONS
        CLAS("clas", CurrentLimitAutomatonBuilder::new),
        CLAS_TWO_LEVELS("clasTwoLevels", CurrentLimitTwoLevelsAutomatonBuilder::new),
        PHASE_SHIFTERS_I("phaseShiftersI", PhaseShifterIAutomatonBuilder::new),
        PHASE_SHIFTERS_P("phaseShiftersP", PhaseShifterPAutomatonBuilder::new),
        TAP_CHANGERS("tapChangers", TapChangerAutomatonBuilder::new),
        TCBS("tcbs", TapChangerBlockingAutomatonBuilder::new),
        UNDER_VOLTAGES("underVoltages", UnderVoltageAutomatonBuilder::new);

        // category name in models.json
        private final String categoryName;

        private final ModelBuilderConstructor constructor;

        Categories(String categoryName, ModelBuilderConstructor constructor) {
            this.categoryName = categoryName;
            this.constructor = constructor;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public ModelBuilderConstructor getConstructor() {
            return constructor;
        }
    }

    @FunctionalInterface
    public interface ModelBuilderConstructor {
        ModelBuilder<DynamicModel> createBuilder(Network network, ModelConfig modelConfig, Reporter reporter);
    }

    private DynamicModelBuilderUtils() {
    }

    public static CurrentLimitAutomatonBuilder newCurrentLimitAutomatonBuilder(Network network, String lib, Reporter reporter) {
        return new CurrentLimitAutomatonBuilder(network,
                ModelConfigsSingleton.getInstance().getModelConfig(CLAS.getCategoryName(), lib),
                reporter);
    }

    public static CurrentLimitAutomatonBuilder newCurrentLimitAutomatonBuilder(Network network, String lib) {
        return newCurrentLimitAutomatonBuilder(network, lib, Reporter.NO_OP);
    }

    public static CurrentLimitTwoLevelsAutomatonBuilder newCurrentLimitTwoLevelsAutomatonBuilder(Network network, String lib, Reporter reporter) {
        return new CurrentLimitTwoLevelsAutomatonBuilder(network,
                ModelConfigsSingleton.getInstance().getModelConfig(CLAS_TWO_LEVELS.getCategoryName(), lib),
                reporter);
    }

    public static CurrentLimitTwoLevelsAutomatonBuilder newCurrentLimitTwoLevelsAutomatonBuilder(Network network, String lib) {
        return newCurrentLimitTwoLevelsAutomatonBuilder(network, lib, Reporter.NO_OP);
    }

    public static PhaseShifterIAutomatonBuilder newPhaseShifterIAutomatonBuilder(Network network, Reporter reporter) {
        return new PhaseShifterIAutomatonBuilder(network,
                ModelConfigsSingleton.getInstance().getFirstModelConfig(PHASE_SHIFTERS_I.getCategoryName()),
                reporter);
    }

    public static PhaseShifterIAutomatonBuilder newPhaseShifterIAutomatonBuilder(Network network) {
        return newPhaseShifterIAutomatonBuilder(network, Reporter.NO_OP);
    }

    public static PhaseShifterPAutomatonBuilder newPhaseShifterPAutomatonBuilder(Network network, Reporter reporter) {
        return new PhaseShifterPAutomatonBuilder(network,
                ModelConfigsSingleton.getInstance().getFirstModelConfig(PHASE_SHIFTERS_P.getCategoryName()),
                reporter);
    }

    public static PhaseShifterPAutomatonBuilder newPhaseShifterPAutomatonBuilder(Network network) {
        return newPhaseShifterPAutomatonBuilder(network, Reporter.NO_OP);
    }

    public static TapChangerAutomatonBuilder newTapChangerAutomatonBuilder(Network network, Reporter reporter) {
        return new TapChangerAutomatonBuilder(network,
                ModelConfigsSingleton.getInstance().getFirstModelConfig(TAP_CHANGERS.getCategoryName()),
                reporter);
    }

    public static TapChangerAutomatonBuilder newTapChangerAutomatonBuilder(Network network) {
        return newTapChangerAutomatonBuilder(network, Reporter.NO_OP);
    }

    public static TapChangerBlockingAutomatonBuilder newTapChangerBlockingAutomatonBuilder(Network network, Reporter reporter) {
        return new TapChangerBlockingAutomatonBuilder(network,
                ModelConfigsSingleton.getInstance().getFirstModelConfig(TCBS.getCategoryName()),
                reporter);
    }

    public static TapChangerBlockingAutomatonBuilder newTapChangerBlockingAutomatonBuilder(Network network) {
        return newTapChangerBlockingAutomatonBuilder(network, Reporter.NO_OP);
    }

    public static UnderVoltageAutomatonBuilder newUnderVoltageAutomatonBuilder(Network network, Reporter reporter) {
        return new UnderVoltageAutomatonBuilder(network,
                ModelConfigsSingleton.getInstance().getFirstModelConfig(UNDER_VOLTAGES.getCategoryName()),
                reporter);
    }

    public static UnderVoltageAutomatonBuilder newUnderVoltageAutomatonBuilder(Network network) {
        return newUnderVoltageAutomatonBuilder(network, Reporter.NO_OP);
    }

    //TODO add lib methods to every automatons ?
}
