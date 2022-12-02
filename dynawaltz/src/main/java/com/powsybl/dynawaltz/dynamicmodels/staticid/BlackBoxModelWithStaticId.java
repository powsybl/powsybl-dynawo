package com.powsybl.dynawaltz.dynamicmodels.staticid;

import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;

public interface BlackBoxModelWithStaticId extends BlackBoxModel {
    String getStaticId();
}
