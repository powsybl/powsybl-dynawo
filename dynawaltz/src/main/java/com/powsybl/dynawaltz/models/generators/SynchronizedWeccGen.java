package com.powsybl.dynawaltz.models.generators;

import com.powsybl.dynawaltz.models.frequencysynchronizers.FrequencySynchronizedModel;
import com.powsybl.iidm.network.Generator;

import static com.powsybl.dynawaltz.models.utils.BusUtils.getConnectableBusStaticId;

public class SynchronizedWeccGen extends WeccGen implements FrequencySynchronizedModel {

    public SynchronizedWeccGen(String dynamicModelId, Generator generator, String parameterSetId, String weccLib, String weccPrefix) {
        super(dynamicModelId, generator, parameterSetId, weccLib, weccPrefix);
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

    @Override
    public String getBlackBoxModelId() {
        return getDynamicModelId();
    }
}
