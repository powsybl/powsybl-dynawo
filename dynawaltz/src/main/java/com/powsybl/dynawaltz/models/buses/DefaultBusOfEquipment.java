package com.powsybl.dynawaltz.models.buses;

import com.powsybl.dynawaltz.models.defaultmodels.AbstractDefaultModel;

public class DefaultBusOfEquipment extends AbstractDefaultModel implements BusOfEquipmentModel {

    public DefaultBusOfEquipment(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "DefaultBusOfEquipment";
    }

    public String getNumCCVarName() {
        return "@@NAME@@@NODE@_numcc";
    }
}
