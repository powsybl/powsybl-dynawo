/**
 * Copyright (c) 2023, RTE (http://www.rte-france.com/)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.builders.automatons.*;
import com.powsybl.dynawaltz.builders.buses.InfiniteBusBuilder;
import com.powsybl.dynawaltz.builders.buses.StandardBusBuilder;
import com.powsybl.dynawaltz.builders.events.*;
import com.powsybl.dynawaltz.builders.generators.*;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcPBuilder;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcVscBuilder;
import com.powsybl.dynawaltz.builders.lines.LineBuilder;
import com.powsybl.dynawaltz.builders.loads.*;
import com.powsybl.dynawaltz.builders.svarcs.BaseStaticVarCompensatorBuilder;
import com.powsybl.dynawaltz.builders.transformers.TransformerFixedRatioBuilder;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public final class DynamicModelBuilderUtils {

    private static final Map<IdentifiableType, BiFunction<Network, ModelConfig, EquipmentModelBuilder<? extends EquipmentModelBuilder<? extends EquipmentModelBuilder<?>>>>> BUILDERS = Map.of(IdentifiableType.LOAD, BaseLoadBuilder::new);

    //TODO charger a partir d'un slurper java utilisable par groovy
    private static final Map<String, ModelConfig> SVARCS = Map.of(
            "StaticVarCompensator", new ModelConfig("StaticVarCompensator", "Controllable"),
            "StaticVarCompensatorPV", new ModelConfig("StaticVarCompensatorPV"));
    private static final Map<String, ModelConfig> LOADS = Map.of(
            "LoadAlphaBeta", new ModelConfig("LoadAlphaBeta", "Controllable"),
            "LoadPQ", new ModelConfig("LoadPQ"));

    private static final Map<String, Map<String, ModelConfig>> LIBS = Map.of("loads", LOADS,
            "staticVarCompensators", SVARCS);

    //TODO mettre les catégories dans les builders directement ?
    public enum Categories {
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

        private final String categoryName;

        private final ModelBuilderConstructorFull constructor;

        Categories(String categoryName, ModelBuilderConstructorFull constructor) {
            this.categoryName = categoryName;
            this.constructor = constructor;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public ModelBuilderConstructorFull getConstructor() {
            return constructor;
        }
    }

    @FunctionalInterface
    public interface ModelBuilderConstructor {
        ModelBuilder<DynamicModel> getConstructor(Network network);
    }

    @FunctionalInterface
    public interface ModelBuilderConstructorFull {
        ModelBuilder<DynamicModel> createBuilder(Network network, ModelConfig modelConfig, Reporter reporter);
    }

    @FunctionalInterface
    public interface EventModelBuilderConstructor {
        ModelBuilder<EventModel> createBuilder(Network network, Reporter reporter);
    }

    private DynamicModelBuilderUtils() {
    }

    public static EquipmentModelBuilder<? extends EquipmentModelBuilder<?>> getBuilder(IdentifiableType type, Network network, String lib) {
        if (BUILDERS.containsKey(type)) {
            ModelConfig modelConfig = new ModelConfig(lib);
            return BUILDERS.get(type).apply(network, modelConfig);
        }
        return null;
    }

    public static Collection<ModelConfig> getEquipmentConfigList(Categories category) {
        return LIBS.get(category.getCategoryName()).values();
    }

    public static Set<String> getBaseLoadLibs() {
        return getLibs(Categories.BASE_LOADS);
    }

    public static Set<String> getBaseStaticVarCompensatorLibs() {
        return getLibs(Categories.BASE_SVARCS);
    }

    public static Set<String> getTransformerLibs() {
        return getLibs(Categories.TRANSFORMERS);
    }

    //Loads
    public static BaseLoadBuilder getBaseLoadBuilder(Network network, String lib) {
        return new BaseLoadBuilder(network, getEquipmentConfig(Categories.BASE_LOADS, lib));
    }

    //TODO charger lib des models et en dur :D -> passer a full json pour plus simplicité -> sauvegarde des info à un seul endroit
    // -> sauvardger la lib dans le builder ? -> on garde un seul emlplamcent de donnée par contre il faut creer un builder pour recup la donnée
    // champ public static dans le builder et recup par les extension groovy -> disponible pour python si besoin
    // mettre en place une fonction generique pour recup les lib a partir de la class builder ?
    // gerer cas des bibli privé plus tard
    public static Set<String> getLoadTwoTransformersTapChangersLibs() {
        return LOADS.keySet();
    }

    public static BaseLoadBuilder getBaseSvarcBuilder(Network network, String lib) {
        return new BaseLoadBuilder(network, getEquipmentConfig(Categories.BASE_SVARCS, lib));
    }

    public static TransformerFixedRatioBuilder getTransformerBuilder(Network network, String lib) {
        return new TransformerFixedRatioBuilder(network, getEquipmentConfig(Categories.TRANSFORMERS, lib));
    }

    public static Pair<ModelBuilderConstructorFull, Collection<ModelConfig>> getBuildersConstructors(Categories category) {
        return Pair.of(category.getConstructor(), getEquipmentConfigList(category));
    }

    public static List<Pair<ModelBuilderConstructorFull, Collection<ModelConfig>>> getAllBuildersConstructors() {
        return Arrays.stream(Categories.values()).map(c -> Pair.of(c.getConstructor(), getEquipmentConfigList(c))).toList();
    }

    public static List<ModelCategory> getModelCategories() {
        return ModelConfigLoader.createCategories();
    }

    public static List<EventModelCategory> getEventModelCategories() {
        return List.of(new EventModelCategory(EventActivePowerVariationBuilder.TAG, EventActivePowerVariationBuilder::new),
                new EventModelCategory(EventDisconnectionBuilder.TAG, EventDisconnectionBuilder::new),
                new EventModelCategory(NodeFaultEventBuilder.TAG, NodeFaultEventBuilder::new));
    }

    private static ModelConfig getEquipmentConfig(Categories category, String lib) {
        ModelConfig modelConfig = LIBS.get(category.getCategoryName()).getOrDefault(lib, null);
        if (modelConfig == null) {
            throw new PowsyblException("Dynamic model " + lib + "not found in category " + category.getCategoryName());
        }
        return modelConfig;
    }

    private static Set<String> getLibs(Categories category) {
        return LIBS.get(category.getCategoryName()).keySet();
    }
}
