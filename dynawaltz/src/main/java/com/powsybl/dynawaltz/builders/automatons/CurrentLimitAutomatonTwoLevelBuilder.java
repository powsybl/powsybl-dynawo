package com.powsybl.dynawaltz.builders.automatons;

import com.powsybl.commons.reporter.Reporter;
import com.powsybl.dynawaltz.builders.DslEquipment;
import com.powsybl.dynawaltz.builders.Reporters;
import com.powsybl.dynawaltz.models.automatons.CurrentLimitTwoLevelsAutomaton;
import com.powsybl.iidm.network.Branch;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.TwoSides;

public class CurrentLimitAutomatonTwoLevelBuilder extends AbstractCurrentLimitAutomatonBuilder<CurrentLimitAutomatonTwoLevelBuilder> {

    public static final String LIB = "CurrentLimitAutomatonTwoLevels";

    protected final DslEquipment<Branch<?>> iMeasurement2;
    protected TwoSides iMeasurement2Side;

    public CurrentLimitAutomatonTwoLevelBuilder(Network network, Reporter reporter) {
        super(network, LIB, reporter, new DslEquipment<>("Quadripole", "iMeasurement1"),
                new DslEquipment<>("Quadripole", "controlledQuadripole1"));
        iMeasurement2 = new DslEquipment<>("Quadripole", "iMeasurement2");
    }

    public CurrentLimitAutomatonTwoLevelBuilder iMeasurement1(String staticId) {
        iMeasurement.addEquipment(staticId, network::getBranch);
        return self();
    }

    public CurrentLimitAutomatonTwoLevelBuilder iMeasurement1Side(TwoSides side) {
        this.iMeasurementSide = side;
        return self();
    }

    public CurrentLimitAutomatonTwoLevelBuilder iMeasurement2(String staticId) {
        iMeasurement2.addEquipment(staticId, network::getBranch);
        return self();
    }

    public CurrentLimitAutomatonTwoLevelBuilder iMeasurement2Side(TwoSides side) {
        this.iMeasurement2Side = side;
        return self();
    }

    @Override
    protected void checkData() {
        super.checkData();
        isInstantiable &= iMeasurement2.checkEquipmentData(reporter);
        if (iMeasurement2Side == null) {
            Reporters.reportFieldNotSet(reporter, "iMeasurement2Side");
            isInstantiable = false;
        }
    }

    @Override
    public CurrentLimitTwoLevelsAutomaton build() {
        return isInstantiable() ? new CurrentLimitTwoLevelsAutomaton(dynamicModelId, parameterSetId,
                iMeasurement.getEquipment(), iMeasurementSide, iMeasurement2.getEquipment(), iMeasurement2Side,
                controlledEquipment.getEquipment(), lib)
                : null;
    }

    @Override
    protected CurrentLimitAutomatonTwoLevelBuilder self() {
        return this;
    }
}
