package com.powsybl.dynawaltz.builders.buses;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.buses.InfiniteBus;
import com.powsybl.iidm.network.Network;

public class InfiniteBusBuilder extends AbstractBusBuilder<InfiniteBusBuilder> {

    public InfiniteBusBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public InfiniteBusBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public InfiniteBus build() {
        return isInstantiable() ? new InfiniteBus(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib()) : null;
    }

    @Override
    protected InfiniteBusBuilder self() {
        return this;
    }
}
