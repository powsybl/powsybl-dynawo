package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.generators.SynchronizedGenerator;
import com.powsybl.dynawaltz.models.generators.SynchronizedGeneratorControllable;
import com.powsybl.iidm.network.Network;

public class SynchronizedGeneratorBuilder extends AbstractGeneratorBuilder<SynchronizedGeneratorBuilder> {

    public SynchronizedGeneratorBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public SynchronizedGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public SynchronizedGenerator build() {
        if (isInstantiable()) {
            if (equipmentConfig.isControllable()) {
                new SynchronizedGeneratorControllable(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib());
            } else {
                new SynchronizedGenerator(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib());
            }
        }
        return null;
    }

    @Override
    protected SynchronizedGeneratorBuilder self() {
        return this;
    }
}
