package com.powsybl.dynawaltz.dynamicmodels.nonstaticref.network;

import com.powsybl.dynawaltz.dynamicmodels.AbstractBlackBoxModelWithStaticId;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public abstract class AbstractNetworkBlackBoxModel extends AbstractBlackBoxModelWithStaticId {

    protected AbstractNetworkBlackBoxModel(String staticId) {
        super("", staticId, "");
    }

    @Override
    public List<Pair<String, String>> getAttributesConnectTo() {
        return List.of(
                Pair.of("id2", DynaWaltzXmlConstants.NETWORK),
                Pair.of("name2", getStaticId())
        );
    }
}
