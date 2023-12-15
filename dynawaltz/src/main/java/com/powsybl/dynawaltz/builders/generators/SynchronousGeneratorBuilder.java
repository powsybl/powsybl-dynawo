package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.generators.*;
import com.powsybl.iidm.network.Network;

public class SynchronousGeneratorBuilder extends AbstractGeneratorBuilder<SynchronousGeneratorBuilder> {

    public SynchronousGeneratorBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public SynchronousGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig);
    }

    protected EnumGeneratorComponent getGeneratorComponent() {
        boolean aux = equipmentConfig.hasAuxiliary();
        boolean transformer = equipmentConfig.hasTransformer();
        if (aux && transformer) {
            return EnumGeneratorComponent.AUXILIARY_TRANSFORMER;
        } else if (transformer) {
            return EnumGeneratorComponent.TRANSFORMER;
        } else if (aux) {
            throw new PowsyblException("Generator component auxiliary without transformer is not supported");
        }
        return EnumGeneratorComponent.NONE;
    }

    @Override
    public SynchronousGenerator build() {
        if (isInstantiable()) {
            if (equipmentConfig.isControllable()) {
                new SynchronousGeneratorControllable(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib(), getGeneratorComponent());
            } else {
                new SynchronousGenerator(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib(), getGeneratorComponent());
            }
        }
        return null;
    }

    @Override
    protected SynchronousGeneratorBuilder self() {
        return this;
    }
}
