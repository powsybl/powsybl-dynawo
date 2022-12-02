package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.DynaWaltzContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

public abstract class AbstractBlackBoxModelWithStaticId extends AbstractBlackBoxModel implements BlackBoxModelWithStaticId {

    private final String staticId;

    protected AbstractBlackBoxModelWithStaticId(String dynamicModelId, String staticId, String parametersId) {
        super(dynamicModelId, parametersId);
        this.staticId = staticId;
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, BlackBoxModel connected) throws XMLStreamException {
        macroConnector.writeMacroConnect(writer, List.of(Pair.of("id1", getDynamicModelId())), connected.getAttributesConnectTo());
    }

    @Override
    public String getStaticId() {
        return staticId;
    }
}
