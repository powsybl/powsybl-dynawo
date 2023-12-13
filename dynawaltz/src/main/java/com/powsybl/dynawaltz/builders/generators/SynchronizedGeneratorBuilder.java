package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.builders.buses.AbstractBusBuilder;
import com.powsybl.dynawaltz.models.buses.InfiniteBus;
import com.powsybl.iidm.network.Network;

public class SynchronizedGeneratorBuilder extends AbstractGeneratorBuilder<SynchronizedGeneratorBuilder> {

    public SynchronizedGeneratorBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public SynchronizedGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public InfiniteBus build() {
        return isInstantiable() ? new InfiniteBus(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib()) : null;
    }

    @Override
    protected SynchronizedGeneratorBuilder self() {
        return this;
    }
}
