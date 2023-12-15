package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.models.automatons.UnderVoltageAutomaton;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.IdentifiableType;
import com.powsybl.iidm.network.Network;

public class UnderVoltageAutomatonBuilder extends AbstractAutomatonModelBuilder<UnderVoltageAutomatonBuilder> {

    public static final String LIB = "UnderVoltage";

    protected final DslEquipment<Generator> dslGenerator;

    //TODO forcer l'usage d'un reporter dans les contr des builder , gere le no op dans utils
    public UnderVoltageAutomatonBuilder(Network network, Reporter reporter) {
        super(network, LIB, reporter);
        dslGenerator = new DslEquipment<>(IdentifiableType.GENERATOR, "generator");
    }

    public UnderVoltageAutomatonBuilder generator(String staticId) {
        dslGenerator.addEquipment(staticId, network::getGenerator);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= dslGenerator.checkEquipmentData(reporter);
    }

    @Override
    public DynamicModel build() {
        return isInstantiable() ? new UnderVoltageAutomaton(dynamicModelId, parameterSetId, dslGenerator.getEquipment()) : null;
    }

    @Override
    protected UnderVoltageAutomatonBuilder self() {
        return this;
    }
}
