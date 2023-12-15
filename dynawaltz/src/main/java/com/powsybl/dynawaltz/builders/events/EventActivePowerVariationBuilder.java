package com.powsybl.dynawaltz.builders.events;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.DslFilteredEquipment;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.dynawaltz.models.events.EventActivePowerVariation;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Injection;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

public class EventActivePowerVariationBuilder extends AbstractEventModelBuilder<Injection<?>, EventActivePowerVariationBuilder> {

    public static final String TAG = "Step";

    protected Double deltaP;

    public EventActivePowerVariationBuilder(Network network, Reporter reporter) {
        //TODO passer en DslEquipment classique ?
        super(network, new DslFilteredEquipment<>("GENERATOR/LOAD", EventActivePowerVariation::isConnectable), TAG, reporter);
    }

    public EventActivePowerVariationBuilder deltaP(double deltaP) {
        this.deltaP = deltaP;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        if (deltaP == null) {
            Reporters.reportFieldNotSet(reporter, "deltaP");
            isInstantiable = false;
        }
    }

    @Override
    protected Injection<?> findEquipment(String staticId) {
        Injection<?> equipment = network.getLoad(staticId);
        return equipment != null ? equipment : network.getGenerator(staticId);
    }

    @Override
    public EventActivePowerVariation build() {
        if (isInstantiable()) {
            if (dslEquipment.getEquipment() instanceof Load load) {
                return new EventActivePowerVariation(load, startTime, deltaP);
            } else if (dslEquipment.getEquipment() instanceof Generator generator) {
                return new EventActivePowerVariation(generator, startTime, deltaP);
            }
        }
        return null;
    }

    @Override
    protected EventActivePowerVariationBuilder self() {
        return this;
    }
}
