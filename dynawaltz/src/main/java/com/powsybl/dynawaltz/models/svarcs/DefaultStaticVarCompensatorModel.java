package com.powsybl.dynawaltz.models.svarcs;

import com.powsybl.dynawaltz.models.defaultmodels.AbstractInjectionDefaultModel;

public class DefaultStaticVarCompensatorModel extends AbstractInjectionDefaultModel implements StaticVarCompensatorModel {

    public DefaultStaticVarCompensatorModel(String staticId) {
        super(staticId);
    }

    @Override
    public String getName() {
        return "NetworkStaticVarCompensator";
    }
}
