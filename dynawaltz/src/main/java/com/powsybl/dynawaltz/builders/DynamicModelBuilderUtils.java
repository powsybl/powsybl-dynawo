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
import com.powsybl.dynawaltz.builders.automatons.*;
import com.powsybl.dynawaltz.builders.buses.InfiniteBusBuilder;
import com.powsybl.dynawaltz.builders.buses.StandardBusBuilder;
import com.powsybl.dynawaltz.builders.generators.*;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcPBuilder;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcVscBuilder;
import com.powsybl.dynawaltz.builders.lines.LineBuilder;
import com.powsybl.dynawaltz.builders.loads.*;
import com.powsybl.dynawaltz.builders.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawaltz.builders.transformers.TransformerFixedRatioBuilder;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynamicModelBuilderUtils {

    //TODO put builder in json ?
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
        CLAS_TWO_LEVELS("clasTwoLevels", CurrentLimitAutomatonTwoLevelBuilder::new),
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
}
