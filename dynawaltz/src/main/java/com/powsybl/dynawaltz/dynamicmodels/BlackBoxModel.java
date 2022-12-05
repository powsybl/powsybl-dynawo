package com.powsybl.dynawaltz.dynamicmodels;

import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface BlackBoxModel {

    String getLib();

    List<Pair<String, String>> getAttributesConnectTo();

}
