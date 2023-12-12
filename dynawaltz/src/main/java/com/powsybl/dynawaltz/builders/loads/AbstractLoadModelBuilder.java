package com.powsybl.dynawaltz.builders.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractLoadModelBuilder<R extends AbstractEquipmentModelBuilder<Load, R>> extends AbstractEquipmentModelBuilder<Load, R> {

    protected AbstractLoadModelBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.LOAD, reporter);
    }

    protected AbstractLoadModelBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig, IdentifiableType.LOAD);
    }

    @Override
    protected Load findEquipment(String staticId) {
        return network.getLoad(staticId);
    }

}
