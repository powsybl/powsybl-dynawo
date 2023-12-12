package com.powsybl.dynawaltz.builders.buses;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

/**
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public abstract class AbstractBusBuilder<R extends AbstractEquipmentModelBuilder<Bus, R>> extends AbstractEquipmentModelBuilder<Bus, R> {

    protected AbstractBusBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.BUS, reporter);
    }

    protected AbstractBusBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig, IdentifiableType.BUS);
    }

    @Override
    protected Bus findEquipment(String staticId) {
        return network.getBusBreakerView().getBus(staticId);
    }
}
