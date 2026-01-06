/**
 * Copyright (c) 2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.criticaltimecalculation.nodeFaults;

import com.powsybl.commons.report.ReportNode;
import com.powsybl.dynawo.BlackBoxModelSupplier;
import com.powsybl.dynawo.criticaltimecalculation.xml.NodeFaultsDydXml;
import com.powsybl.dynawo.criticaltimecalculation.xml.NodeFaultsParXml;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultEventData;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultEventModels;
import com.powsybl.dynawo.criticaltimecalculation.nodefaults.NodeFaultEventModelsFactory;
import com.powsybl.dynawo.xml.DynawoTestUtil;
import com.powsybl.iidm.network.Network;
import com.powsybl.iidm.network.test.EurostagTutorialExample1Factory;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;

/**
 * @author Erwann GOASGUEN {@literal <erwann.goasguen at rte-france.com>}
 */
public class NodeFaultsXmlTest extends DynawoTestUtil {

    @Test
    void writeDyds() throws SAXException, IOException, XMLStreamException {
        Network network = EurostagTutorialExample1Factory.create();
        List<NodeFaultEventData> nodeFaultsList = List.of(
                new NodeFaultEventData.Builder(network)
                        .setStaticId("NGEN")
                        .setFaultStartTime(1)
                        .setFaultStopTime(5)
                        .setFaultXPu(0.001)
                        .setFaultRPu(0.001)
                        .build(),
                new NodeFaultEventData.Builder(network)
                        .setStaticId("NLOAD")
                        .setFaultStartTime(1)
                        .setFaultStopTime(2)
                        .setFaultXPu(0.001)
                        .setFaultRPu(0.001)
                        .build()
        );

        BlackBoxModelSupplier bbmSupplier = BlackBoxModelSupplier.createFrom(dynamicModels);
        List<NodeFaultEventModels> nodeFaultEventModels = NodeFaultEventModelsFactory.createFrom(nodeFaultsList,
                network, bbmSupplier,
                n -> n.equalsIgnoreCase("CTC_EventNodeFault"),
                ReportNode.NO_OP);

        NodeFaultsDydXml.write(tmpDir, nodeFaultEventModels);
        NodeFaultsParXml.write(tmpDir, nodeFaultEventModels);
        validate("dyd.xsd", "NodeFault_0.xml", tmpDir.resolve("NodeFault_0.dyd"));
        validate("dyd.xsd", "NodeFault_1.xml", tmpDir.resolve("NodeFault_1.dyd"));
        validate("parameters.xsd", "NodeFault_0_par.xml", tmpDir.resolve("NodeFault_0.par"));
        validate("parameters.xsd", "NodeFault_1_par.xml", tmpDir.resolve("NodeFault_1.par"));
    }
}
