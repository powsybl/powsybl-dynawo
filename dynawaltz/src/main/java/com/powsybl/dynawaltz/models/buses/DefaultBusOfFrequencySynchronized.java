package com.powsybl.dynawaltz.models.buses;

import com.powsybl.dynawaltz.models.defaultmodels.AbstractDefaultModel;

public class DefaultBusOfFrequencySynchronized extends AbstractDefaultModel implements BusOfFrequencySynchronizedModel {

    public DefaultBusOfFrequencySynchronized(String staticId) {
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
