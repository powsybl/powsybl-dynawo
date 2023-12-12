package com.powsybl.dynawaltz.builders;

import com.powsybl.commons.PowsyblException;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.loads.*;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public final class DynamicModelBuilderUtils {

    private static final Map<IdentifiableType, BiFunction<Network, EquipmentConfig, EquipmentModelBuilder<DynamicModel, ? extends EquipmentModelBuilder<DynamicModel, ? extends  EquipmentModelBuilder>>>> builders = Map.of(IdentifiableType.LOAD, BaseLoadBuilder::new);

    //TODO charger a partir d'un slurper java utilisable par groovy
    private static final Map<String, EquipmentConfig> svarcs = Map.of(
            "StaticVarCompensator", new EquipmentConfig("StaticVarCompensator", "Controllable"),
            "StaticVarCompensatorPV", new EquipmentConfig("StaticVarCompensatorPV"));
    private static final Map<String, EquipmentConfig> loads = Map.of(
            "LoadAlphaBeta", new EquipmentConfig("LoadAlphaBeta", "Controllable"),
            "LoadPQ", new EquipmentConfig("LoadPQ"));

    private static final Map<String, Map<String, EquipmentConfig>> libs = Map.of("loads", loads,
            "staticVarCompensators", svarcs);

    private enum Categories {
        BASE_LOADS("baseLoads"),
        BASE_SVARCS("staticVarCompensators"),
        TRANSFORMERS("transformers");

        private final String categoryName;

        Categories(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getCategoryName() {
            return categoryName;
        }
    }

    private DynamicModelBuilderUtils() {
    }

    public static EquipmentModelBuilder<DynamicModel, ? extends EquipmentModelBuilder> getBuilder(IdentifiableType type, Network network, String lib) {
        if (builders.containsKey(type)) {
            EquipmentConfig equipmentConfig = new EquipmentConfig(lib);
            return builders.get(type).apply(network, equipmentConfig);
        }
        return null;
    }

    public static Collection<EquipmentConfig> getEquipmentConfigList(Categories category) {
        return libs.get(category.getCategoryName()).values();
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
        return loads.keySet();
    }

    public static LoadTwoTransformersTapChangersBuilder getLoadTwoTransformersTapChangersBuilder(Network network) {
        return new LoadTwoTransformersTapChangersBuilder(network);
    }

    public static BaseLoadBuilder getBaseSvarcBuilder(Network network, String lib) {
        return new BaseLoadBuilder(network, getEquipmentConfig(Categories.BASE_SVARCS, lib));
    }

    public static BaseLoadBuilder getTransformerBuilder(Network network, String lib) {
        return new BaseLoadBuilder(network, getEquipmentConfig(Categories.TRANSFORMERS, lib));
    }

    private static EquipmentConfig getEquipmentConfig(Categories category, String lib) {
        EquipmentConfig equipmentConfig = libs.get(category.getCategoryName()).getOrDefault(lib, null);
        if (equipmentConfig == null) {
            throw new PowsyblException("Dynamic model " + lib +  "not found in category " + category.getCategoryName());
        }
        return equipmentConfig;
    }

    private static Set<String> getLibs(Categories category) {
        return libs.get(category.getCategoryName()).keySet();
    }
}
