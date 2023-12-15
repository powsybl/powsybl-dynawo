package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.models.automatons.CurrentLimitAutomaton;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

public class CurrentLimitAutomatonBuilder extends AbstractCurrentLimitAutomatonBuilder<CurrentLimitAutomatonBuilder> {

    public static final String LIB = "CurrentLimitAutomaton";

    //TODO implementer un constructeur alternatif en protected pour changer de lib pour tous les models à une lib ?
    public CurrentLimitAutomatonBuilder(Network network, String lib, Reporter reporter) {
        super(network, lib, reporter, new DslEquipment<>("Quadripole", "iMeasurement"),
            new DslEquipment<>("Quadripole", "controlledQuadripole"));
    }

    public CurrentLimitAutomatonBuilder iMeasurement(String staticId) {
        iMeasurement.addEquipment(staticId, network::getBranch);
        return self();
    }

    public CurrentLimitAutomatonBuilder iMeasurementSide(TwoSides side) {
        this.iMeasurementSide = side;
        return self();
    }

    @Override
    public CurrentLimitAutomaton build() {
        return isInstantiable() ? new CurrentLimitAutomaton(dynamicModelId, parameterSetId,
                iMeasurement.getEquipment(), iMeasurementSide, controlledEquipment.getEquipment(), lib)
                : null;
    }

    @Override
    protected CurrentLimitAutomatonBuilder self() {
        return this;
    }
}
