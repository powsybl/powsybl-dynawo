package com.powsybl.dynawaltz.builders.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.builders.EquipmentModelBuilder;
import com.powsybl.dynawaltz.models.loads.BaseLoad;
import com.powsybl.dynawaltz.models.loads.BaseLoadControllable;
import com.powsybl.iidm.network.Network;

public class BaseLoadBuilder extends AbstractLoadModelBuilder<BaseLoadBuilder> implements EquipmentModelBuilder<DynamicModel, BaseLoadBuilder> {

    public BaseLoadBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public BaseLoadBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public BaseLoad build() {
        if (isInstantiable()) {
            if (equipmentConfig.isControllable()) {
                return new BaseLoadControllable(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib());
            } else {
                return new BaseLoad(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib());
            }
        } else {
            return null;
        }
    }

    @Override
    protected BaseLoadBuilder self() {
        return this;
    }
}
