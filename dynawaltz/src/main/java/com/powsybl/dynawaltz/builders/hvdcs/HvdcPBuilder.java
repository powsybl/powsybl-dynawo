package com.powsybl.dynawaltz.builders.hvdcs;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.hvdc.HvdcP;
import com.powsybl.dynawaltz.models.hvdc.HvdcPDangling;
import com.powsybl.iidm.network.Network;

public class HvdcPBuilder extends AbstractHvdcBuilder<HvdcPBuilder> {

    public HvdcPBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public HvdcPBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public HvdcP build() {
        if (isInstantiable()) {
            if (equipmentConfig.isDangling()) {
                new HvdcPDangling(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib(), danglingSide);
            } else {
                new HvdcP(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib());
            }
        }
        return null;
    }

    @Override
    protected HvdcPBuilder self() {
        return this;
    }
}
