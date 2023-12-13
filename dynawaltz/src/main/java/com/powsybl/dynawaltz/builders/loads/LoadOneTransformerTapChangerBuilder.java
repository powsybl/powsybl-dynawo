package com.powsybl.dynawaltz.builders.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformerTapChanger;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class LoadOneTransformerTapChangerBuilder extends AbstractLoadModelBuilder<LoadOneTransformerTapChangerBuilder> {

    public static final String LIB = "LoadOneTransformerTapChanger";

    public LoadOneTransformerTapChangerBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public LoadOneTransformerTapChangerBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), reporter);
    }

    public LoadOneTransformerTapChangerBuilder(Network network) {
        super(network, new EquipmentConfig(LIB));
    }

    @Override
    public LoadOneTransformerTapChanger build() {
        return isInstantiable() ? new LoadOneTransformerTapChanger(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected LoadOneTransformerTapChangerBuilder self() {
        return this;
    }
}
