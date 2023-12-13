package com.powsybl.dynawaltz.builders.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformers;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class LoadTwoTransformersBuilder extends AbstractLoadModelBuilder<LoadTwoTransformersBuilder> {

    public static final String LIB = "LoadTwoTransformers";

    public LoadTwoTransformersBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public LoadTwoTransformersBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), reporter);
    }

    public LoadTwoTransformersBuilder(Network network) {
        super(network, new EquipmentConfig(LIB));
    }

    @Override
    public LoadTwoTransformers build() {
        return isInstantiable() ? new LoadTwoTransformers(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected LoadTwoTransformersBuilder self() {
        return this;
    }
}
