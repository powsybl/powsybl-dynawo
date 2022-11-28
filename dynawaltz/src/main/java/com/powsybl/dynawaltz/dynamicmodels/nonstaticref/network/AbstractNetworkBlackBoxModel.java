package com.powsybl.dynawaltz.dynamicmodels.nonstaticref.network;

import com.powsybl.dynawaltz.dynamicmodels.AbstractBlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModelWithStaticId;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class AbstractNetworkBlackBoxModel extends AbstractBlackBoxModel implements BlackBoxModelWithStaticId {

    private final String staticId;

    protected AbstractNetworkBlackBoxModel(String staticId) {
        super("", "");
        this.staticId = staticId;
    }

    @Override
    public String getStaticId() {
        return staticId;
    }

    @Override
    public List<Pair<String, String>> getAttributesConnectTo() {
        return List.of(
                Pair.of("id2", DynaWaltzXmlConstants.NETWORK),
                Pair.of("name2", getStaticId())
        );
    }
}
