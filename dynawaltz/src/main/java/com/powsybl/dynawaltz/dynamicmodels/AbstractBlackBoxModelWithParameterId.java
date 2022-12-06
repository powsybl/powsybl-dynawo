package com.powsybl.dynawaltz.dynamicmodels;

import java.util.Objects;

public abstract class AbstractBlackBoxModelWithParameterId implements BlackBoxModelWithParameterId {

    private final String parameterSetId;

    protected AbstractBlackBoxModelWithParameterId(String parameterSetId) {
        this.parameterSetId = Objects.requireNonNull(parameterSetId);
    }

    @Override
    public String getParameterSetId() {
        return parameterSetId;
    }
}
