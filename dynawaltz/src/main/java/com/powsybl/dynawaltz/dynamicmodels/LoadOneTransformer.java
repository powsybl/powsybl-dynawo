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
import com.powsybl.dynawaltz.xml.MacroStaticReference;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class LoadOneTransformer extends AbstractLoadModel {

    public static final List<Pair<String, String>> VAR_MAPPING = Arrays.asList(
            Pair.of("transformer_P1Pu_value", "p"),
            Pair.of("transformer_Q1Pu_value", "q"),
            Pair.of("transformer_state", "state"));

    public LoadOneTransformer(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public String getLib() {
        return "LoadOneTransformer";
    }

    @Override
    public List<Pair<String, String>> getVarMapping() {
        return VAR_MAPPING;
    }

    @Override
    protected void writeConnector(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        MacroConnectorXml.writeConnect(writer, "transformer_terminal", "@STATIC_ID@@NODE@_ACPIN");
        MacroConnectorXml.writeConnect(writer, "transformer_switchOffSignal1", "@STATIC_ID@@NODE@_switchOff");
        MacroConnectorXml.writeConnect(writer, "load_switchOffSignal1", "@STATIC_ID@@NODE@_switchOff");
    }
}
