/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels.staticid.staticref;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.dynamicmodels.staticid.AbstractBlackBoxModelWithStaticId;
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import java.util.List;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Luma Zamarreño <zamarrenolm at aia.es>
 */
public abstract class AbstractBlackBoxModelWithStaticRef extends AbstractBlackBoxModelWithStaticId implements BlackBoxModelWithStaticRef {

    private final List<Pair<String, String>> staticRef;

    protected AbstractBlackBoxModelWithStaticRef(String dynamicModelId, String staticId, String parameterSetId, List<Pair<String, String>> staticRef) {
        super(dynamicModelId, staticId, parameterSetId);
        this.staticRef = staticRef;
    }

    @Override
    public List<Pair<String, String>> getStaticRef() {
        return staticRef;
    }

    protected void writeBlackBoxModel(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        // Write the blackBoxModel object
        writer.writeStartElement(DYN_URI, "blackBoxModel");
        writer.writeAttribute("id", getDynamicModelId());
        writer.writeAttribute("lib", getLib());
        writer.writeAttribute("parFile", context.getParFile());
        writer.writeAttribute("parId", getParameterSetId());
        writer.writeAttribute("staticId", getStaticId());
        MacroStaticReference.writeMacroStaticRef(writer, getLib());
        writer.writeEndElement();
    }
}
