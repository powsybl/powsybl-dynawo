/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawaltz.DynaWaltzContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public abstract class AbstractBlackBoxModelWithDynamicId extends AbstractBlackBoxModelWithParameterId implements BlackBoxModelWithDynamicId, DynamicModel {

    private final String dynamicModelId;

    protected AbstractBlackBoxModelWithDynamicId(String dynamicModelId, String parameterSetId) {
        super(parameterSetId);
        this.dynamicModelId = Objects.requireNonNull(dynamicModelId);
    }

    public String getDynamicModelId() {
        return dynamicModelId;
    }

    @Override
    public void writeParameters(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        //Empty method to be redefined by specific models
    }

    @Override
    public void writeMacroConnect(XMLStreamWriter writer, DynaWaltzContext context, MacroConnector macroConnector, BlackBoxModel connected) throws XMLStreamException {
        macroConnector.writeMacroConnect(writer, List.of(Pair.of("id1", getDynamicModelId())), connected.getAttributesConnectTo());
    }

    @Override
    public List<Pair<String, String>> getAttributesConnectTo() {
        return List.of(Pair.of("id2", getDynamicModelId()));
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
