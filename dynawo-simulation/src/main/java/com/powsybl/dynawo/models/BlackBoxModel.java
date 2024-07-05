/**
 * Copyright (c) 2022, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.models;

import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.models.macroconnections.MacroConnectAttribute;
import com.powsybl.dynawo.models.macroconnections.MacroConnectionsAdder;
import com.powsybl.dynawo.parameters.ParametersSet;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Florian Dupuy {@literal <florian.dupuy at rte-france.com>}
 * @author Laurent Issertial {@literal <laurent.issertial at rte-france.com>}
 */
public interface BlackBoxModel extends Model {
    String getDynamicModelId();

    String getParameterSetId();

    String getLib();

    List<VarMapping> getVarsMapping();

    void createMacroConnections(MacroConnectionsAdder adder);

    List<MacroConnectAttribute> getMacroConnectFromAttributes();

    String getParFile(DynawoSimulationContext context);

    void write(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException;

    void write(XMLStreamWriter writer, String parFileName) throws XMLStreamException;

    void createDynamicModelParameters(DynawoSimulationContext context, Consumer<ParametersSet> parametersAdder);

    void createNetworkParameter(ParametersSet networkParameters);
}
