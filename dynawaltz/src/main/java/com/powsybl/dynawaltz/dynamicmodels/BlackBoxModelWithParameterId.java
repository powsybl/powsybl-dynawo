package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.utils.MacroConnector;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

public interface BlackBoxModelWithParameterId extends BlackBoxModel {
    String getParameterSetId();

    void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException;

    void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, BlackBoxModel connected) throws XMLStreamException;

    List<Pair<String, String>> getVarsConnect(BlackBoxModel connected);

    List<BlackBoxModel> getModelsConnectedTo(DynaWaltzContext dynaWaltzContext);
}
