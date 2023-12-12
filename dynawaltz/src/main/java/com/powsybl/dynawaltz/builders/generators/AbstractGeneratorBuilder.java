package com.powsybl.dynawaltz.builders.generators;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.AbstractEquipmentModelBuilder;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 */
public abstract class AbstractGeneratorBuilder<R extends AbstractEquipmentModelBuilder<Generator, R>> extends AbstractEquipmentModelBuilder<Generator, R> {

    protected AbstractGeneratorBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, IdentifiableType.GENERATOR, reporter);
    }

    protected AbstractGeneratorBuilder(Network network, EquipmentConfig equipmentConfig) {
        super(network, equipmentConfig, IdentifiableType.GENERATOR);
    }

    @Override
    protected Generator findEquipment(String staticId) {
        return network.getGenerator(staticId);
    }
}
