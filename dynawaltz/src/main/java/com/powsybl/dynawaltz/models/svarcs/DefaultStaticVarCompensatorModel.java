package com.powsybl.dynawaltz.models.svarcs;

import com.powsybl.dynawaltz.models.AbstractInjectionNetworkModel;

public class DefaultStaticVarCompensatorModel extends AbstractInjectionNetworkModel implements StaticVarCompensatorModel {

    public DefaultStaticVarCompensatorModel(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "NetworkStaticVarCompensator";
    }
}
