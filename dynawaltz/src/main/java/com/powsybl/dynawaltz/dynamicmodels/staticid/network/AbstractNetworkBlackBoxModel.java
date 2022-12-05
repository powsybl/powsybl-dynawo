package com.powsybl.dynawaltz.dynamicmodels.staticid.network;

import com.powsybl.dynawaltz.dynamicmodels.staticid.BlackBoxModelWithStaticId;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class AbstractNetworkBlackBoxModel implements BlackBoxModelWithStaticId {

    private final String staticId;

    protected AbstractNetworkBlackBoxModel(String staticId) {
        this.staticId = staticId;
    }

    @Override
    public String getStaticId() {
        return this.staticId;
    }

    @Override
    public List<Pair<String, String>> getAttributesConnectTo() {
        return List.of(
                Pair.of("id2", DynaWaltzXmlConstants.NETWORK),
                Pair.of("name2", getStaticId())
        );
    }
}
