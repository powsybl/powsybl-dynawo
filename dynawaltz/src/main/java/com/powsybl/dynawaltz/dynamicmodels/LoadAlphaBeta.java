/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroConnectorXml;
import com.powsybl.dynawaltz.xml.MacroStaticReferenceXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LoadAlphaBeta extends AbstractLoadModel {

    public LoadAlphaBeta(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadAlphaBeta";
    }

    @Override
    protected void writeReference(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        MacroStaticReferenceXml.writeStaticRef(writer, "load_PPu", "p");
        MacroStaticReferenceXml.writeStaticRef(writer, "load_QPu", "q");
        MacroStaticReferenceXml.writeStaticRef(writer, "load_state", "state");
    }

    @Override
    protected void writeConnector(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        MacroConnectorXml.writeConnect(writer, "load_terminal", "@STATIC_ID@@NODE@_ACPIN");
        MacroConnectorXml.writeConnect(writer, "load_switchOffSignal1", "@STATIC_ID@@NODE@_switchOff");
    }
}
