package com.powsybl.dynawaltz.builders.buses;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.buses.StandardBus;
import com.powsybl.iidm.network.Network;

public class StandardBusBuilder extends AbstractBusBuilder<StandardBusBuilder> {

    public static final String LIB = "Line";

    public StandardBusBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public StandardBusBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public StandardBus build() {
        return isInstantiable() ? new StandardBus(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected StandardBusBuilder self() {
        return this;
    }
}
