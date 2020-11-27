/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.dynamicmodels;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.xml.DynawoXmlContext;
import com.powsybl.dynawo.xml.MacroConnectorXml;
import com.powsybl.dynawo.xml.MacroStaticReferenceXml;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LoadOneTransformer extends AbstractLoadModel {

    public LoadOneTransformer(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadOneTransformer";
    }

    @Override
    protected void writeReference(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        MacroStaticReferenceXml.writeStaticRef(writer, "transformer_P1Pu_value", "p");
        MacroStaticReferenceXml.writeStaticRef(writer, "transformer_Q1Pu_value", "q");
        MacroStaticReferenceXml.writeStaticRef(writer, "transformer_state", "state");
    }

    @Override
    protected void writeConnector(XMLStreamWriter writer, DynawoXmlContext context) throws XMLStreamException {
        MacroConnectorXml.writeConnect(writer, "transformer_terminal", "@STATIC_ID@@NODE@_ACPIN");
        MacroConnectorXml.writeConnect(writer, "transformer_switchOffSignal1", "@STATIC_ID@@NODE@_switchOff");
        MacroConnectorXml.writeConnect(writer, "load_switchOffSignal1", "@STATIC_ID@@NODE@_switchOff");
    }
}
