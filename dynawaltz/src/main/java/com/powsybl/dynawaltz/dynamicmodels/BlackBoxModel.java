/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.dynamicmodels;

import com.powsybl.dynawaltz.xml.DynaWaltzXmlContext;
import com.powsybl.dynawaltz.xml.MacroStaticReference;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 */
public interface BlackBoxModel {
    String getDynamicModelId();

    String getStaticId();

    String getParameterSetId();

    String getLib();

    MacroStaticReference getMacroStaticReference();

    void write(XMLStreamWriter writer, DynaWaltzXmlContext context) throws XMLStreamException;

    void writeParameters(XMLStreamWriter writer, DynaWaltzXmlContext xmlContext) throws XMLStreamException;
}
