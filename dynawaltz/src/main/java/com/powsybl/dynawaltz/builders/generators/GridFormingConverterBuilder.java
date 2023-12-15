package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.generators.GridFormingConverter;
import com.powsybl.iidm.network.Network;

public class GridFormingConverterBuilder extends AbstractGeneratorBuilder<GridFormingConverterBuilder> {

    public GridFormingConverterBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public GridFormingConverterBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    @Override
    public GridFormingConverter build() {
        return isInstantiable() ? new GridFormingConverter(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib()) : null;
    }

    @Override
    protected GridFormingConverterBuilder self() {
        return this;
    }
}
