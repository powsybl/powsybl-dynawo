package com.powsybl.dynawaltz.builders.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.loads.LoadTwoTransformersTapChangers;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class LoadTwoTransformersTapChangersBuilder extends AbstractLoadModelBuilder<LoadTwoTransformersTapChangersBuilder> {

    public static final String LIB = "LoadTwoTransformersTapChangers";

    public LoadTwoTransformersTapChangersBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public LoadTwoTransformersTapChangersBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), reporter);
    }

    public LoadTwoTransformersTapChangersBuilder(Network network) {
        super(network, new EquipmentConfig(LIB));
    }

    @Override
    public LoadTwoTransformersTapChangers build() {
        return isInstantiable() ? new LoadTwoTransformersTapChangers(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected LoadTwoTransformersTapChangersBuilder self() {
        return this;
    }
}
