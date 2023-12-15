package com.powsybl.dynawaltz.builders.hvdcs;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.hvdc.HvdcVsc;
import com.powsybl.dynawaltz.models.hvdc.HvdcVscDangling;
import com.powsybl.iidm.network.Network;

public class HvdcVscBuilder extends AbstractHvdcBuilder<HvdcVscBuilder> {

    public HvdcVscBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public HvdcVscBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public HvdcVsc build() {
        if (isInstantiable()) {
            if (equipmentConfig.isDangling()) {
                new HvdcVscDangling(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib(), danglingSide);
            } else {
                new HvdcVsc(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib());
            }
        }
        return null;
    }

    @Override
    protected HvdcVscBuilder self() {
        return this;
    }
}
