package com.powsybl.dynawaltz.models.wecc;

import com.powsybl.dynawaltz.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.iidm.network.Generator;

import static com.powsybl.dynawaltz.models.utils.BusUtils.getConnectableBusStaticId;

public class SynchronizedWecc extends Wecc implements FrequencySynchronizedModel {

    public SynchronizedWecc(String dynamicModelId, Generator generator, String parameterSetId, String weccLib) {
        super(dynamicModelId, generator, parameterSetId, weccLib);
    }

    @Override
    public String getOmegaRefPuVarName() {
        return weccPrefix + "_omegaRefPu";
    }

    @Override
    public String getRunningVarName() {
        return weccPrefix + "_injector_running";
    }

    @Override
    public String getConnectedBusId() {
        return getConnectableBusStaticId(equipment);
    }
}
