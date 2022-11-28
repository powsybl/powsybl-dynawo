/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.nonstaticref.events;

import com.powsybl.dynamicsimulation.EventModel;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.AbstractBlackBoxModelWithStaticId;
import com.powsybl.dynawaltz.dynamicmodels.BlackBoxModel;
import com.powsybl.dynawaltz.dynamicmodels.MacroConnector;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractBlackBoxEventModel extends AbstractBlackBoxModelWithStaticId implements EventModel, BlackBoxEventModel {

    protected AbstractBlackBoxEventModel(String eventModelId, String staticId, String parameterSetId) {
        super(eventModelId, staticId, parameterSetId);
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        // Write the blackBoxModel object
        writer.writeEmptyElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, BlackBoxModel connected) throws XMLStreamException {
        macroConnector.writeMacroConnect(writer, getAttributesConnectFrom(), connected.getAttributesConnectTo());
    }

    private List<Pair<String, String>> getAttributesConnectFrom() {
        return List.of(Pair.of("id1", getDynamicModelId()));
    }
}
