package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.List;

public abstract class AbstractNetworkBlackBoxModel implements BlackBoxModel {
    @Override
    public String getDynamicModelId() {
        return DynaWaltzXmlConstants.NETWORK;
    }

    @Override
    public String getStaticId() {
        return "";
    }

    @Override
    public String getParameterSetId() {
        return "";
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, MacroConnector macroConnector, BlackBoxModel connected) throws XMLStreamException {
        // Default models not written in dyd
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        // Default models not written in dyd
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzXmlContext xmlContext) throws XMLStreamException {
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
    public BlackBoxModel getModelConnectedTo(DynaWaltzContext dynaWaltzContext) {
        // Default models are only connected to
        return null;
    }
}
