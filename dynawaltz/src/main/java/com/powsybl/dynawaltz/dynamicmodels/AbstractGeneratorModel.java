/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroConnectorXml;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Arrays;
import java.util.List;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public abstract class AbstractGeneratorModel extends AbstractBlackBoxModel {

    public static final List<Pair<String, String>> VAR_MAPPING = Arrays.asList(
            Pair.of("generator_PGenPu", "p"),
            Pair.of("generator_QGenPu", "q"),
            Pair.of("generator_state", "state"));

    public AbstractGeneratorModel(String dynamicModelId, String staticId, String parameterSetId) {
        super(dynamicModelId, staticId, parameterSetId);
    }

    @Override
    public List<Pair<String, String>> getVarMapping() {
        return VAR_MAPPING;
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException {
        if (context.getIndex(getLib(), true) == 0) {

            // Write the macroConnector object
            writer.writeStartElement(DYN_URI, "macroConnector");
            writer.writeAttribute("id", MACRO_CONNECTOR_PREFIX + getLib());
            MacroConnectorXml.writeConnect(writer, "generator_terminal", "@STATIC_ID@@NODE@_ACPIN");
            MacroConnectorXml.writeConnect(writer, "generator_switchOffSignal1", "@STATIC_ID@@NODE@_switchOff");
            writer.writeEndElement();
        }

        writeBlackBoxModel(writer, context);

        // Write the connect object
        MacroConnectorXml.writeMacroConnect(writer, MACRO_CONNECTOR_PREFIX + getLib(), getDynamicModelId(), NETWORK);
    }
}
