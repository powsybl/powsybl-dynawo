package com.powsybl.dynawaltz.builders.buses;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.buses.StandardBus;
import com.powsybl.iidm.network.Network;

public class StandardBusBuilder extends AbstractBusBuilder<StandardBusBuilder> {

    public static final String LIB = "Bus";

    public StandardBusBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), reporter);
    }

    public StandardBusBuilder(Network network) {
        super(network, new EquipmentConfig(LIB));
    }

    @Override
    public DynamicModel build() {
        return isInstantiable() ? new StandardBus(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected StandardBusBuilder self() {
        return this;
    }
}
