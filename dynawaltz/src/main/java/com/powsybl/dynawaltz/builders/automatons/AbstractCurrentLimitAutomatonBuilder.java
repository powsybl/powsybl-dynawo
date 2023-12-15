package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

abstract class AbstractCurrentLimitAutomatonBuilder<T extends AbstractAutomatonModelBuilder<T>> extends AbstractAutomatonModelBuilder<T> {

    protected final DslEquipment<Branch<?>> iMeasurement;
    protected TwoSides iMeasurementSide;
    protected final DslEquipment<Branch<?>> controlledEquipment;

    //TODO check accessiblie des class et constructeur des classes abstraites
    protected AbstractCurrentLimitAutomatonBuilder(Network network, String lib, Reporter reporter, DslEquipment<Branch<?>> iMeasurement, DslEquipment<Branch<?>> controlledEquipment) {
        super(network, lib, reporter);
        this.iMeasurement = iMeasurement;
        this.controlledEquipment = controlledEquipment;
    }

    public T controlledQuadripole(String staticId) {
        controlledEquipment.addEquipment(staticId, network::getBranch);
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= controlledEquipment.checkEquipmentData(reporter);
        isInstantiable &= iMeasurement.checkEquipmentData(reporter);
        if (iMeasurementSide == null) {
            Reporters.reportFieldNotSet(reporter, "iMeasurementSide");
            isInstantiable = false;
        }
    }
}
