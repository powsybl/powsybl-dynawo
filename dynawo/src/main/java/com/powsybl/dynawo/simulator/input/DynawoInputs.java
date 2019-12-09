/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.simulator.input;

import static com.powsybl.dynawo.simulator.DynawoConstants.CRV_FILENAME;
import static com.powsybl.dynawo.simulator.DynawoConstants.DYD_FILENAME;
import static com.powsybl.dynawo.simulator.DynawoConstants.PAR_FILENAME;
import static com.powsybl.dynawo.simulator.DynawoXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawo.simulator.DynawoXmlConstants.DYN_URI;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.iidm.network.Bus;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Line;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoInputs {

    private DynawoInputs() {
        throw new IllegalStateException("Utility class");
    }

    public static void prepare(Network network, Path workingDir) throws XMLStreamException, IOException {
        Path dydFile = workingDir.resolve(DYD_FILENAME);
        Path parFile = workingDir.resolve(PAR_FILENAME);
        Path crvFile = workingDir.resolve(CRV_FILENAME);
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (Writer dydWriter = Files.newBufferedWriter(dydFile, StandardCharsets.UTF_8);
            Writer parWriter = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8);
            Writer crvWriter = Files.newBufferedWriter(crvFile, StandardCharsets.UTF_8)) {
            XMLStreamWriter dydXmlWriter = output.createXMLStreamWriter(dydWriter);
            XMLStreamWriter parXmlWriter = output.createXMLStreamWriter(parWriter);
            XMLStreamWriter crvXmlWriter = output.createXMLStreamWriter(crvWriter);
            try {
                dydXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                parXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                crvXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");

                dydXmlWriter.writeComment(getCopyrightText());
                parXmlWriter.writeComment(getCopyrightText());
                crvXmlWriter.writeComment(getCopyrightText());

                dydXmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                dydXmlWriter.writeStartElement(DYN_URI, "dynamicModelsArchitecture");
                dydXmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);

                parXmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                parXmlWriter.writeStartElement("parametersSet");
                parXmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);

                crvXmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                crvXmlWriter.writeStartElement("curvesInput");
                crvXmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);
                crvXmlWriter.writeComment("Curves for scenario");

                int id = 1;
                DynawoSimulationParameters.writeStartSet(parXmlWriter, id++);
                DynawoSimulationParameters.writeGlobalParameters(parXmlWriter);
                DynawoSimulationParameters.writeEndSet(parXmlWriter);
                for (Bus b : network.getBusBreakerView().getBuses()) {
                    DynawoCurves.writeBusCurve(b, crvXmlWriter);
                }
                for (Load l : network.getLoads()) {
                    DynawoSimulationParameters.writeStartSet(parXmlWriter, id);
                    DynawoSimulationParameters.writeLoadParameters(parXmlWriter);
                    DynawoSimulationParameters.writeEndSet(parXmlWriter);
                    DynawoDynamicsModels.writeLoadDynamicsModels(l, dydXmlWriter, id++);
                    DynawoCurves.writeLoadCurve(l, crvXmlWriter);
                }
                for (Generator g : network.getGenerators()) {
                    DynawoSimulationParameters.writeStartSet(parXmlWriter, id);
                    DynawoSimulationParameters.writeGeneratorParameters(parXmlWriter);
                    DynawoSimulationParameters.writeEndSet(parXmlWriter);
                    DynawoDynamicsModels.writeGeneratorDynamicsModels(g, dydXmlWriter, id++);
                    DynawoCurves.writeGeneratorCurve(g, crvXmlWriter);
                }
                DynawoSimulationParameters.writeStartSet(parXmlWriter, id);
                DynawoSimulationParameters.writeOmegaRefParameters(network, parXmlWriter);
                DynawoSimulationParameters.writeEndSet(parXmlWriter);
                DynawoDynamicsModels.writeOmegaRefDynamicsModels(dydXmlWriter, id++);
                DynawoSimulationParameters.writeStartSet(parXmlWriter, id);
                DynawoSimulationParameters.writeEventParameters(parXmlWriter);
                DynawoSimulationParameters.writeEndSet(parXmlWriter);
                DynawoDynamicsModels.writeEventDisconnectLineDynamicsModels(dydXmlWriter, id++);
                for (Load l : network.getLoads()) {
                    DynawoDynamicsModels.writeLoadConnections(l, dydXmlWriter);
                }
                int grp = 0;
                for (Generator g : network.getGenerators()) {
                    DynawoDynamicsModels.writeGeneratorConnections(g, dydXmlWriter, grp++);
                }
                Optional<Line> line = network.getLineStream().findFirst();
                if (line.isPresent()) {
                    DynawoDynamicsModels.writeEventDisconnectLineConnections(line.get(), dydXmlWriter);
                }
                dydXmlWriter.writeEndElement();
                dydXmlWriter.writeEndDocument();
                parXmlWriter.writeEndElement();
                parXmlWriter.writeEndDocument();
                crvXmlWriter.writeEndElement();
                crvXmlWriter.writeEndDocument();
            } finally {
                dydXmlWriter.close();
                parXmlWriter.close();
                crvXmlWriter.close();
            }
        }
    }

    public static String getCopyrightText() {
        return String.join(System.lineSeparator(),
            "    Copyright (c) 2015-2019, RTE (http://www.rte-france.com)",
            "    See AUTHORS.txt",
            "    All rights reserved.",
            "    This Source Code Form is subject to the terms of the Mozilla Public",
            "    License, v. 2.0. If a copy of the MPL was not distributed with this",
            "    file, you can obtain one at http://mozilla.org/MPL/2.0/.",
            "    SPDX-License-Identifier: MPL-2.0",
            "",
            "    This file is part of Dynawo, an hybrid C++/Modelica open source time domain",
            "    simulation tool for power systems.");
    }
}
