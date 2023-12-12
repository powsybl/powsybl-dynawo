package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.generators.GeneratorFictitious;
import com.powsybl.iidm.network.Network;

public class GeneratorFictitiousBuilder extends AbstractGeneratorBuilder<GeneratorFictitiousBuilder>{

    //TODO voir ou mettre la lib en static (utilisé dans le groovy extension; le builder et la classe finale) -> a mettre dnas la classe ?
    // ou garder dans les builder avec la modif des constructeurs derrière et passer tous les constructeur en protected
    public static final String LIB = "GeneratorFictitious";

    public GeneratorFictitiousBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public GeneratorFictitiousBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public GeneratorFictitious build() {
        return isInstantiable() ? new GeneratorFictitious(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected GeneratorFictitiousBuilder self() {
        return this;
    }
}
