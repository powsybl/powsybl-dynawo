/**
 * Copyright (c) 2018, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.DynawoParameterType;
import com.powsybl.dynawo.DynawoProvider;
import com.powsybl.iidm.network.Generator;
import com.powsybl.iidm.network.Load;
import com.powsybl.iidm.network.Network;

import static com.powsybl.dynawo.xml.DynawoConstants.*;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.*;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoInputs {

    public static final String NETWORK = "NETWORK";
    public static final String OMEGA_REF = "OMEGA_REF";
    public static final String IIDM = "IIDM";
    public static final String BOOLEAN = "BOOL";
    public static final String DOUBLE = "DOUBLE";
    public static final String INT = "INT";

    private DynawoInputs() {
        throw new IllegalStateException("Utility class");
    }

    public static void prepare(Network network, DynawoProvider provider, Path workingDir)
        throws XMLStreamException, IOException {
        Path jobFile = workingDir.resolve(JOBS_FILENAME);
        Path dydFile = workingDir.resolve(DYD_FILENAME);
        Path parFile = workingDir.resolve(PAR_FILENAME);
        Path parSolverFile = workingDir.resolve(SOLVER_PAR_FILENAME);
        Path crvFile = workingDir.resolve(CRV_FILENAME);
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (Writer jobWriter = Files.newBufferedWriter(jobFile, StandardCharsets.UTF_8);
            Writer dydWriter = Files.newBufferedWriter(dydFile, StandardCharsets.UTF_8);
            Writer parWriter = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8);
            Writer parSolverWriter = Files.newBufferedWriter(parSolverFile, StandardCharsets.UTF_8);
            Writer crvWriter = Files.newBufferedWriter(crvFile, StandardCharsets.UTF_8)) {
            XMLStreamWriter jobXmlWriter = output.createXMLStreamWriter(jobWriter);
            XMLStreamWriter dydXmlWriter = output.createXMLStreamWriter(dydWriter);
            XMLStreamWriter parXmlWriter = output.createXMLStreamWriter(parWriter);
            XMLStreamWriter parSolverXmlWriter = output.createXMLStreamWriter(parSolverWriter);
            XMLStreamWriter crvXmlWriter = output.createXMLStreamWriter(crvWriter);
            try {
                jobXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                dydXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                parXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                parSolverXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                crvXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");

                jobXmlWriter.writeComment(getCopyrightText());
                dydXmlWriter.writeComment(getCopyrightText());
                parXmlWriter.writeComment(getCopyrightText());
                parSolverXmlWriter.writeComment(getCopyrightText());
                crvXmlWriter.writeComment(getCopyrightText());

                jobXmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                jobXmlWriter.writeStartElement(DYN_URI, "jobs");
                jobXmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);
                DynawoJobs.writeJobs(jobXmlWriter, provider.getDynawoJobs(network));

                dydXmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                dydXmlWriter.writeStartElement(DYN_URI, "dynamicModelsArchitecture");
                dydXmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);
                DynawoDynamicModels.writeDynamicModels(dydXmlWriter, provider.getDynawoDynamicModels(network));

                parXmlWriter.setPrefix(EMPTY_PREFIX, DYN_URI);
                parXmlWriter.writeStartElement("parametersSet");
                parXmlWriter.writeNamespace(EMPTY_PREFIX, DYN_URI);
                DynawoSimulationParameters.writeParameterSets(parXmlWriter, provider.getDynawoParameterSets(network));

                parSolverXmlWriter.setPrefix(EMPTY_PREFIX, DYN_URI);
                parSolverXmlWriter.writeStartElement("parametersSet");
                parSolverXmlWriter.writeNamespace(EMPTY_PREFIX, DYN_URI);
                DynawoSolverParameters.writeParameterSets(parSolverXmlWriter, provider.getDynawoSolverParameterSets(network));

                crvXmlWriter.setPrefix(EMPTY_PREFIX, DYN_URI);
                crvXmlWriter.writeStartElement("curvesInput");
                crvXmlWriter.writeNamespace(EMPTY_PREFIX, DYN_URI);
                crvXmlWriter.writeComment("Curves for scenario");
                DynawoCurves.writeCurves(crvXmlWriter, provider.getDynawoCurves(network));

                int id = DynawoSimulationParameters.getMaxId(provider.getDynawoParameterSets(network)) + 1;
                for (Load l : network.getLoads()) {
                    if (!DynawoDynamicModels.definedDynamicModel(provider.getDynawoDynamicModels(network), l.getId())) {
                        DynawoSimulationParameters.writeDefaultLoad(parXmlWriter, id);
                        DynawoDynamicModels.writeDefaultLoad(dydXmlWriter, l, id++);
                    }
                }
                int gens = DynawoDynamicModels.countGeneratorConnections(provider.getDynawoDynamicModels(network));
                for (Generator g : network.getGenerators()) {
                    if (!DynawoDynamicModels.definedDynamicModel(provider.getDynawoDynamicModels(network), g.getId())) {
                        DynawoSimulationParameters.writeDefaultGenerator(parXmlWriter, id);
                        DynawoDynamicModels.writeDefaultGenerator(dydXmlWriter, g, id++, gens);
                        gens++;
                    }
                }

                if (!DynawoDynamicModels.definedDynamicModel(provider.getDynawoDynamicModels(network), DynawoParameterType.OMEGA_REF.getValue())) {
                    DynawoSimulationParameters.writeDefaultOmegaRefParameterSets(parXmlWriter, network, id);
                    DynawoDynamicModels.writeDefaultOmegaRef(dydXmlWriter, id++);
                }
                jobXmlWriter.writeEndElement();
                jobXmlWriter.writeEndDocument();
                dydXmlWriter.writeEndElement();
                dydXmlWriter.writeEndDocument();
                parXmlWriter.writeEndElement();
                parXmlWriter.writeEndDocument();
                parSolverXmlWriter.writeEndElement();
                parSolverXmlWriter.writeEndDocument();
                crvXmlWriter.writeEndElement();
                crvXmlWriter.writeEndDocument();
            } finally {
                jobXmlWriter.close();
                dydXmlWriter.close();
                parXmlWriter.close();
                parSolverXmlWriter.close();
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
