package com.powsybl.dynawaltz.builders.loads;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.EquipmentConfig;
import com.powsybl.dynawaltz.models.loads.LoadOneTransformer;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public class LoadOneTransformerBuilder extends AbstractLoadModelBuilder<LoadOneTransformerBuilder> {

    public static final String LIB = "LoadOneTransformer";

    //TODO avirer et utilisé uniquement en interne
    public LoadOneTransformerBuilder(Network network, EquipmentConfig equipmentConfig, Reporter reporter) {
        super(network, equipmentConfig, reporter);
    }

    public LoadOneTransformerBuilder(Network network, Reporter reporter) {
        super(network, new EquipmentConfig(LIB), reporter);
    }

    public LoadOneTransformerBuilder(Network network) {
        super(network, new EquipmentConfig(LIB));
    }

    @Override
    public LoadOneTransformer build() {
        return isInstantiable() ? new LoadOneTransformer(dynamicModelId, getEquipment(), parameterSetId) : null;
    }

    @Override
    protected LoadOneTransformerBuilder self() {
        return this;
    }
}
