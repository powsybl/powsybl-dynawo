package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.models.TransformerSide;
import com.powsybl.dynawaltz.models.automatons.TapChangerAutomaton;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

public class TapChangerAutomatonBuilder extends AbstractAutomatonModelBuilder<TapChangerAutomatonBuilder> {

    public static final String LIB = "TapChangerAutomaton";

    protected final DslEquipment<Load> dslLoad;
    protected TransformerSide side = TransformerSide.NONE;

    public TapChangerAutomatonBuilder(Network network, Reporter reporter) {
        super(network, LIB, reporter);
        dslLoad = new DslEquipment<>(IdentifiableType.LOAD);
    }

    TapChangerAutomatonBuilder staticId(String staticId) {
        dslLoad.addEquipment(staticId, network::getLoad);
        return self();
    }

    TapChangerAutomatonBuilder side(TransformerSide side) {
        this.side = side;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= dslLoad.checkEquipmentData(reporter);
    }

    @Override
    public DynamicModel build() {
        return isInstantiable() ? new TapChangerAutomaton(dynamicModelId, parameterSetId, dslLoad.getEquipment(), side) : null;
    }

    @Override
    protected TapChangerAutomatonBuilder self() {
        return this;
    }
}
