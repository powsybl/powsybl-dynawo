/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import org.apache.commons.lang3.tuple.Pair;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public interface BlackBoxModel {
    String getDynamicModelId();

    String getStaticId();

    String getParameterSetId();

    String getLib();

    List<Pair<String, String>> getVarsMapping();

    void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException;

    void writeParameters(XMLStreamWriter writer, DynaWaltzXmlContext xmlContext) throws XMLStreamException;

    void writeMacroConnect(XMLStreamWriter writer, BlackBoxModel connected) throws XMLStreamException;

    List<Pair<String, String>> getVarsConnect(BlackBoxModel connected);

    BlackBoxModel getModelConnectedTo(DynaWaltzContext dynaWaltzContext);

    void setMacroConnector(MacroConnector macroConnector);

    MacroConnector getMacroConnector();
}
