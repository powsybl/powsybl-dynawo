package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;

public abstract class AbstractNetworkBlackBoxModel implements BlackBoxModel {

    private final String staticId;

    protected AbstractNetworkBlackBoxModel(String staticId) {
        this.staticId = staticId;
    }

    @Override
    public String getStaticId() {
        return staticId;
    }

    @Override
    public String getDynamicModelId() {
        return "";
    }

    @Override
    public String getParameterSetId() {
        return "";
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, BlackBoxModel connected) {
        // Default models not written in dyd
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        // Default models not written in dyd
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        // Default models not written in dyd
    }

    @Override
    public List<Pair<String, String>> getVarsMapping() {
        return Collections.emptyList();
    }

    @Override
    public List<Pair<String, String>> getVarsConnect(BlackBoxModel connectedBbm) {
        return Collections.emptyList();
    }

    @Override
    public List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext dynaWaltzContext) {
        // Default models are only connected to
        return Collections.emptyList();
    }

    @Override
    public List<Pair<String, String>> getAttributesConnectTo() {
        return List.of(
                Pair.of("id2", DynaWaltzXmlConstants.NETWORK),
                Pair.of("name2", getStaticId())
        );
    }
}
