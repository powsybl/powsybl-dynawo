/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.models;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawaltz.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawaltz.parameters.ParametersSet;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Florian Dupuy <florian.dupuy at rte-france.com>
 * @author Laurent Issertial <laurent.issertial at rte-france.com>
 */
public interface BlackBoxModel extends Model {
    String getDynamicModelId();

    String getParameterSetId();

    String getLib();

    List<VarMapping> getVarsMapping();

    void createMacroConnections(MacroConnectionsAdder adder);

    List<MacroConnectAttribute> getMacroConnectFromAttributes();

    String getParFile(DynaWaltzContext context);

    void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException;

    void createDynamicModelParameters(DynaWaltzContext context, Consumer<ParametersSet> parametersAdder);

    void createNetworkParameter(ParametersSet networkParameters);
}
