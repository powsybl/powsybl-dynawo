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
import com.powsybl.dynawaltz.builders.buses.InfiniteBusBuilder;
import com.powsybl.dynawaltz.builders.generators.GridFormingConverterBuilder;
import com.powsybl.dynawaltz.builders.generators.SynchronizedGeneratorBuilder;
import com.powsybl.dynawaltz.builders.generators.SynchronousGeneratorBuilder;
import com.powsybl.dynawaltz.builders.generators.WeccBuilder;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcPBuilder;
import com.powsybl.dynawaltz.builders.hvdcs.HvdcVscBuilder;
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

    private static final Map<IdentifiableType, BiFunction<Network, EquipmentConfig, EquipmentModelBuilder<? extends EquipmentModelBuilder<? extends EquipmentModelBuilder<?>>>>> BUILDERS = Map.of(IdentifiableType.LOAD, BaseLoadBuilder::new);

    //TODO charger a partir d'un slurper java utilisable par groovy
    private static final Map<String, EquipmentConfig> SVARCS = Map.of(
            "StaticVarCompensator", new EquipmentConfig("StaticVarCompensator", "Controllable"),
            "StaticVarCompensatorPV", new EquipmentConfig("StaticVarCompensatorPV"));
    private static final Map<String, EquipmentConfig> LOADS = Map.of(
            "LoadAlphaBeta", new EquipmentConfig("LoadAlphaBeta", "Controllable"),
            "LoadPQ", new EquipmentConfig("LoadPQ"));

    private static final Map<String, Map<String, EquipmentConfig>> LIBS = Map.of("loads", LOADS,
            "staticVarCompensators", SVARCS);

    //TODO mettre les catégories dans les builders directement ?
    public enum Categories {
        BASE_LOADS("baseLoads", BaseLoadBuilder::new),
        BASE_SVARCS("staticVarCompensators", BaseStaticVarCompensatorBuilder::new),
        HVDC_P("hvdcP", HvdcPBuilder::new),
        HVDC_VSC("hvdcVsc", HvdcVscBuilder::new),
        INFINITE_BUSES("infiniteBuses", InfiniteBusBuilder::new),
        SYNCHRONIZED_GENERATORS("synchronizedGenerators", SynchronizedGeneratorBuilder::new),
        SYNCHRONOUS_GENERATORS("synchronousGenerators", SynchronousGeneratorBuilder::new),
        WECC_GEN("wecc", WeccBuilder::new),
        GRID_FORMING_CONVERTER("gridFormingConverter", GridFormingConverterBuilder::new),
        TRANSFORMERS("transformers", TransformerFixedRatioBuilder::new);

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
        ModelBuilder<DynamicModel> createBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter);
    }

    private DynamicModelBuilderUtils() {
    }

    public static EquipmentModelBuilder<? extends EquipmentModelBuilder<?>> getBuilder(IdentifiableType type, Network network, String lib) {
        if (BUILDERS.containsKey(type)) {
            EquipmentConfig equipmentConfig = new EquipmentConfig(lib);
            return BUILDERS.get(type).apply(network, equipmentConfig);
        }
        return null;
    }

    public static Collection<EquipmentConfig> getEquipmentConfigList(Categories category) {
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

    public static LoadOneTransformerBuilder getLoadOneTransformerBuilder(Network network) {
        return new LoadOneTransformerBuilder(network);
    }

    public static LoadOneTransformerTapChangerBuilder getLoadOneTransformerTapChangerBuilder(Network network) {
        return new LoadOneTransformerTapChangerBuilder(network);
    }

    public static LoadTwoTransformersBuilder getLoadTwoTransformersBuilder(Network network) {
        return new LoadTwoTransformersBuilder(network);
    }

    //TODO charger lib des models et en dur :D -> passer a full json pour plus simplicité -> sauvegarde des info à un seul endroit
    // -> sauvardger la lib dans le builder ? -> on garde un seul emlplamcent de donnée par contre il faut creer un builder pour recup la donnée
    // champ public static dans le builder et recup par les extension groovy -> disponible pour python si besoin
    // mettre en place une fonction generique pour recup les lib a partir de la class builder ?
    // gerer cas des bibli privé plus tard
    public static Set<String> getLoadTwoTransformersTapChangersLibs() {
        return LOADS.keySet();
    }

    public static LoadTwoTransformersTapChangersBuilder getLoadTwoTransformersTapChangersBuilder(Network network) {
        return new LoadTwoTransformersTapChangersBuilder(network);
    }

    public static BaseLoadBuilder getBaseSvarcBuilder(Network network, String lib) {
        return new BaseLoadBuilder(network, getEquipmentConfig(Categories.BASE_SVARCS, lib));
    }

    public static TransformerFixedRatioBuilder getTransformerBuilder(Network network, String lib) {
        return new TransformerFixedRatioBuilder(network, getEquipmentConfig(Categories.TRANSFORMERS, lib));
    }

    public static Pair<ModelBuilderConstructorFull, Collection<EquipmentConfig>> getBuildersConstructors(Categories category) {
        return Pair.of(category.getConstructor(), getEquipmentConfigList(category));
    }

    public static List<Pair<ModelBuilderConstructorFull, Collection<EquipmentConfig>>> getAllBuildersConstructors() {
        return Arrays.stream(Categories.values()).map(c -> Pair.of(c.getConstructor(), getEquipmentConfigList(c))).toList();
    }

    private static EquipmentConfig getEquipmentConfig(Categories category, String lib) {
        EquipmentConfig equipmentConfig = LIBS.get(category.getCategoryName()).getOrDefault(lib, null);
        if (equipmentConfig == null) {
            throw new PowsyblException("Dynamic model " + lib + "not found in category " + category.getCategoryName());
        }
        return equipmentConfig;
    }

    private static Set<String> getLibs(Categories category) {
        return LIBS.get(category.getCategoryName()).keySet();
    }
}
