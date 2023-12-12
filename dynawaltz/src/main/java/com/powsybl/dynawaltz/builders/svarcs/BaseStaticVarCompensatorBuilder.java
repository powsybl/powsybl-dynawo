package com.powsybl.dynawaltz.builders.svarcs;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.svarcs.BaseStaticVarCompensator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.StaticVarCompensator;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class BaseStaticVarCompensatorBuilder extends AbstractEquipmentModelBuilder<StaticVarCompensator, BaseStaticVarCompensatorBuilder> {

    public static final String LIB = "staticVarCompensators";

    //TODO quid des constructeurs avec les EquipmentConfig ?
    public BaseStaticVarCompensatorBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.STATIC_VAR_COMPENSATOR, reporter);
    }

    public BaseStaticVarCompensatorBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig, IdentifiableType.STATIC_VAR_COMPENSATOR);
    }

    @Override
    protected StaticVarCompensator findEquipment(String staticId) {
        return network.getStaticVarCompensator(staticId);
    }

    @Override
    public BaseStaticVarCompensator build() {
        return isInstantiable() ? new BaseStaticVarCompensator(dynamicModelId, getEquipment(), parameterSetId, equipmentConfig.getLib()) : null;
    }

    @Override
    protected BaseStaticVarCompensatorBuilder self() {
        return this;
    }
}
