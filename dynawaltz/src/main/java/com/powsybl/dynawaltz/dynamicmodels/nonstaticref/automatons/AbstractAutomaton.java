package com.powsybl.dynawaltz.dynamicmodels.nonstaticref.automatons;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.AbstractBlackBoxModelWithStaticId;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

public abstract class AbstractAutomaton extends AbstractBlackBoxModelWithStaticId {

    protected AbstractAutomaton(String dynamicModelId, String staticId, String parametersId) {
        super(dynamicModelId, staticId, parametersId);
    }

    protected void writeAutomatonBlackBoxModel(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        // Write the blackBoxModel object
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
    }
}
