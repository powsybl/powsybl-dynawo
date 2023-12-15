package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.generators.SynchronizedWeccGen;
import com.powsybl.dynawaltz.models.generators.WeccGen;
import com.powsybl.iidm.network.Network;

public class WeccBuilder extends AbstractGeneratorBuilder<WeccBuilder> {

    public WeccBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public WeccBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public WeccGen build() {
        if (isInstantiable()) {
            if (equipmentConfig.isSynchronized()) {
                new SynchronizedWeccGen(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib(), equipmentConfig.getInternalModelPrefix());
            } else {
                new WeccGen(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib(), equipmentConfig.getInternalModelPrefix());
            }
        }
        return null;
    }

    @Override
    protected WeccBuilder self() {
        return this;
    }
}
