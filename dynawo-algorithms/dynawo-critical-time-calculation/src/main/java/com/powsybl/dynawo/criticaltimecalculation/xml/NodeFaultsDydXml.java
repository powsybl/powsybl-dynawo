/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.xml;

import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultEventModels;
import com.powsybl.dynawo.models.BlackBoxModel;
import com.powsybl.dynawo.models.macroconnections.MacroConnect;
import com.powsybl.dynawo.models.macroconnections.MacroConnector;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

/**
 * @author Erwann Goasguen {@literal <erwann.goasguen at rte-france.com>}
 */
public final class NodeFaultsDydXml {
    private NodeFaultsDydXml() {
    }

    public static void write(Path workingDir, List<NodeFaultEventModels> eventModels) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        for (NodeFaultEventModels model : eventModels) {
            Path file = workingDir.resolve(createDydFileName(model));
            NodeFaultXmlUtil.write(file, "dynamicModelsArchitecture", NodeFaultsDydXml::writeEvent, model);
        }
    }

    private static void writeEvent(XMLStreamWriter writer, NodeFaultEventModels model) throws XMLStreamException {
        for (BlackBoxModel ev : model.eventModels()) {
            ev.write(writer, NodeFaultsParXml.createParFileName(model));
        }
        for (MacroConnector mcr : model.macroConnectorsMap().values()) {
            mcr.write(writer);
        }
        for (MacroConnect mc : model.macroConnectList()) {
            mc.write(writer);
        }
    }

    public static String createDydFileName(NodeFaultEventModels model) {
        return model.getId() + ".dyd";
    }
}
