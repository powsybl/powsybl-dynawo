package com.powsybl.dynawaltz.dynamicmodels.staticid.network;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.staticid.AbstractBlackBoxModelWithStaticId;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.MacroConnector;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamWriter;
import java.util.List;

public abstract class AbstractNetworkBlackBoxModel extends AbstractBlackBoxModelWithStaticId {

    protected AbstractNetworkBlackBoxModel(String staticId) {
        super("", staticId, "");
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, BlackBoxModel connected) {
        // Default models not written in dyd
    }

    @Override
    public List<Pair<String, String>> getAttributesConnectTo() {
        return List.of(
                Pair.of("id2", DynaWaltzXmlConstants.NETWORK),
                Pair.of("name2", getStaticId())
        );
    }
}
