/**
 *
 * Copyright (c) 2020-2025, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 * SPDX-License-Identifier: MPL-2.0
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynawo.DumpFileParameters;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationParameters.SolverType;
import com.powsybl.dynawo.SimulationTime;
import com.powsybl.dynawo.commons.ExportMode;
import com.powsybl.tools.Version;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.powsybl.dynawo.DynawoSimulationConstants.*;
import static com.powsybl.dynawo.commons.DynawoConstants.NETWORK_FILENAME;
import static com.powsybl.dynawo.commons.DynawoConstants.OUTPUTS_FOLDER;
import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class JobsXml extends AbstractXmlDynawoSimulationWriter<DynawoSimulationContext> {

    private static final String EXPORT_MODE = "exportMode";
    private final Supplier<SimulationTime> simulationTimeSupplier;
    private final String additionalDydFile;

    private JobsXml(String xmlFileName, Supplier<SimulationTime> simulationTimeSupplier) {
        this(xmlFileName, simulationTimeSupplier, null);
    }

    private JobsXml(String xmlFileName, Supplier<SimulationTime> simulationTimeSupplier, String additionalDydFile) {
        super(xmlFileName, "jobs");
        this.simulationTimeSupplier = simulationTimeSupplier;
        this.additionalDydFile = additionalDydFile;
    }

    public static void write(Path workingDir, DynawoSimulationContext context) throws IOException {
        new JobsXml(JOBS_FILENAME, context::getSimulationTime).createXmlFileFromDataSupplier(workingDir, context);
    }

    public static void write(Path workingDir, DynawoSimulationContext context, String additionalDydFile) throws IOException {
        new JobsXml(JOBS_FILENAME, context::getSimulationTime, additionalDydFile).createXmlFileFromDataSupplier(workingDir, context);
    }

    public static void writeFinalStep(Path workingDir, DynawoSimulationContext context) throws IOException {
        new JobsXml(FINAL_STEP_JOBS_FILENAME, context::getFinalStepSimulationTime, FINAL_STEP_DYD_FILENAME)
                .createXmlFileFromDataSupplier(workingDir, context);
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException {
        DynawoSimulationParameters parameters = context.getDynawoSimulationParameters();
        writeAdditionnalInfos(writer, context);
        writer.writeStartElement(DYN_URI, "job");
        writer.writeAttribute("name", context.getNetwork().getNameOrId());
        writeSolver(writer, parameters);
        writeModeler(writer, parameters, additionalDydFile);
        writeSimulation(writer, parameters, simulationTimeSupplier.get());
        writeOutput(writer, context);
        writer.writeEndElement();
    }

    private static void writeSolver(XMLStreamWriter writer, DynawoSimulationParameters parameters) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "solver");
        writer.writeAttribute("lib", parameters.getSolverType().equals(SolverType.IDA) ? "dynawo_SolverIDA" : "dynawo_SolverSIM");
        writer.writeAttribute("parFile", SOLVER_PARAMETERS_FILENAME);
        writer.writeAttribute("parId", parameters.getSolverParameters().getId());
    }

    private static void writeModeler(XMLStreamWriter writer, DynawoSimulationParameters parameters, String additionalDydFile) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "modeler");
        writer.writeAttribute("compileDir", "outputs/compilation");

        writer.writeEmptyElement(DYN_URI, "network");
        writer.writeAttribute("iidmFile", NETWORK_FILENAME);
        writer.writeAttribute("parFile", NETWORK_PARAMETERS_FILENAME);
        writer.writeAttribute("parId", parameters.getNetworkParameters().getId());

        writer.writeEmptyElement(DYN_URI, "dynModels");
        writer.writeAttribute("dydFile", DYD_FILENAME);
        if (additionalDydFile != null) {
            writer.writeEmptyElement(DYN_URI, "dynModels");
            writer.writeAttribute("dydFile", additionalDydFile);
        }

        DumpFileParameters dumpFileParameters = parameters.getDumpFileParameters();
        if (dumpFileParameters.useDumpFile()) {
            writer.writeEmptyElement(DYN_URI, "initialState");
            writer.writeAttribute("file", dumpFileParameters.dumpFile());
        }

        writer.writeEmptyElement(DYN_URI, "precompiledModels");
        writer.writeAttribute("useStandardModels", Boolean.toString(true));

        writer.writeEmptyElement(DYN_URI, "modelicaModels");
        writer.writeAttribute("useStandardModels", Boolean.toString(false));

        writer.writeEndElement();
    }

    private static void writeSimulation(XMLStreamWriter writer, DynawoSimulationParameters parameters, SimulationTime simulationTime) throws XMLStreamException {
        Optional<String> criteriaFileName = parameters.getCriteriaFileName();
        if (criteriaFileName.isPresent()) {
            writer.writeStartElement(DYN_URI, "simulation");
        } else {
            writer.writeEmptyElement(DYN_URI, "simulation");
        }
        writer.writeAttribute("startTime", Double.toString(simulationTime.startTime()));
        writer.writeAttribute("stopTime", Double.toString(simulationTime.stopTime()));
        writer.writeAttribute("precision", Double.toString(parameters.getPrecision()));
        if (criteriaFileName.isPresent()) {
            writer.writeEmptyElement(DYN_URI, "criteria");
            writer.writeAttribute("criteriaFile", criteriaFileName.get());
            writer.writeEndElement();
        }
    }

    private static void writeOutput(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException {
        DynawoSimulationParameters parameters = context.getDynawoSimulationParameters();
        writer.writeStartElement(DYN_URI, "outputs");
        writer.writeAttribute("directory", OUTPUTS_FOLDER);

        writer.writeEmptyElement(DYN_URI, "dumpInitValues");
        writer.writeAttribute("local", Boolean.toString(false));
        writer.writeAttribute("global", Boolean.toString(false));

        if (context.withConstraints()) {
            writer.writeEmptyElement(DYN_URI, "constraints");
            writer.writeAttribute(EXPORT_MODE, ExportMode.XML.toString());
        }

        writer.writeEmptyElement(DYN_URI, "timeline");
        writer.writeAttribute(EXPORT_MODE, parameters.getTimelineExportMode().toString());

        writer.writeEmptyElement(DYN_URI, "finalState");
        writer.writeAttribute("exportIIDMFile", Boolean.toString(true));
        writer.writeAttribute("exportDumpFile", Boolean.toString(parameters.getDumpFileParameters().exportDumpFile()));

        if (context.withCurveVariables()) {
            writer.writeEmptyElement(DYN_URI, "curves");
            writer.writeAttribute("inputFile", CRV_FILENAME);
            writer.writeAttribute(EXPORT_MODE, ExportMode.CSV.toString());
        }

        if (context.withFsvVariables()) {
            writer.writeEmptyElement(DYN_URI, "finalStateValues");
            writer.writeAttribute("inputFile", FSV_FILENAME);
            writer.writeAttribute(EXPORT_MODE, ExportMode.CSV.toString());
        }

        writer.writeStartElement(DYN_URI, "logs");
        writeAppender(writer, parameters);
        writer.writeEndElement();

        writer.writeEndElement();
    }

    private static void writeAppender(XMLStreamWriter writer, DynawoSimulationParameters parameters) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "appender");
        writer.writeAttribute("tag", "");
        writer.writeAttribute("file", LOGS_FILENAME);
        writer.writeAttribute("lvlFilter", parameters.getLogLevelFilter().toString());
        for (DynawoSimulationParameters.SpecificLog log : parameters.getSpecificLogs()) {
            writeSpecificAppender(writer, log);
        }
    }

    private static void writeSpecificAppender(XMLStreamWriter writer, DynawoSimulationParameters.SpecificLog log) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "appender");
        writer.writeAttribute("tag", log.toString());
        writer.writeAttribute("file", log.getFileName());
        writer.writeAttribute("lvlFilter", DynawoSimulationParameters.LogLevel.DEBUG.toString());
    }

    private static void writeAdditionnalInfos(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException {
        String currentDynawoVersion = context.getCurrentDynawoVersion();
        List<String> versions = new ArrayList<>();
        versions.add("powsybl-dynawo: " + currentDynawoVersion);
        if (context.isDefaultConfigVersion()) {
            versions.add("dynawo_version: 1.5.0");
            versions.add("powsybl_version: 7.0.0");
        } else {
            versions.addAll(
                    Version.list()
                            .stream()
                            .map(version -> version.getRepositoryName() + ": " + version.getMavenProjectVersion())
                            .toList()
            );
        }
        for (String comment : versions) {
            writer.writeComment(comment);
        }
    }
}
