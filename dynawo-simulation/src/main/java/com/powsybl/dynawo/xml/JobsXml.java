/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynamicsimulation.DynamicSimulationParameters;
import com.powsybl.dynawo.DumpFileParameters;
import com.powsybl.dynawo.DynawoSimulationContext;
import com.powsybl.dynawo.DynawoSimulationParameters;
import com.powsybl.dynawo.DynawoSimulationParameters.SolverType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;

import static com.powsybl.dynawo.xml.DynawoSimulationConstants.*;
import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class JobsXml extends AbstractXmlDynawoSimulationWriter {

    private JobsXml() {
        super(JOBS_FILENAME, "jobs");
    }

    public static void write(Path workingDir, DynawoSimulationContext context) throws IOException {
        new JobsXml().createXmlFileFromContext(workingDir, context);
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException {
        DynawoSimulationParameters parameters = context.getDynawoSimulationParameters();
        writer.writeStartElement(DYN_URI, "job");
        writer.writeAttribute("name", "Job");
        writeSolver(writer, parameters);
        writeModeler(writer, parameters);
        writeSimulation(writer, parameters, context.getParameters());
        writeOutput(writer, parameters, context.withCurves());
        writer.writeEndElement();
    }

    private static void writeSolver(XMLStreamWriter writer, DynawoSimulationParameters parameters) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "solver");
        writer.writeAttribute("lib", parameters.getSolverType().equals(SolverType.IDA) ? "dynawo_SolverIDA" : "dynawo_SolverSIM");
        writer.writeAttribute("parFile", DynawoSimulationParameters.SOLVER_OUTPUT_PARAMETERS_FILE);
        writer.writeAttribute("parId", parameters.getSolverParameters().getId());
    }

    private static void writeModeler(XMLStreamWriter writer, DynawoSimulationParameters parameters) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "modeler");
        writer.writeAttribute("compileDir", "outputs/compilation");

        writer.writeEmptyElement(DYN_URI, "network");
        writer.writeAttribute("iidmFile", NETWORK_FILENAME);
        writer.writeAttribute("parFile", DynawoSimulationParameters.NETWORK_OUTPUT_PARAMETERS_FILE);
        writer.writeAttribute("parId", parameters.getNetworkParameters().getId());

        writer.writeEmptyElement(DYN_URI, "dynModels");
        writer.writeAttribute("dydFile", DYD_FILENAME);

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

    private static void writeSimulation(XMLStreamWriter writer, DynawoSimulationParameters parameters, DynamicSimulationParameters dynamicSimulationParameters) throws XMLStreamException {
        boolean hasCriteriaFile = parameters.hasCriteriaFile();
        if (hasCriteriaFile) {
            writer.writeStartElement(DYN_URI, "simulation");
        } else {
            writer.writeEmptyElement(DYN_URI, "simulation");
        }
        writer.writeAttribute("startTime", Double.toString(dynamicSimulationParameters.getStartTime()));
        writer.writeAttribute("stopTime", Double.toString(dynamicSimulationParameters.getStopTime()));
        writer.writeAttribute("precision", Double.toString(parameters.getPrecision()));
        if (hasCriteriaFile) {
            writer.writeEmptyElement(DYN_URI, "criteria");
            writer.writeAttribute("criteriaFile", parameters.getCriteriaFileName());
            writer.writeEndElement();
        }
    }

    private static void writeOutput(XMLStreamWriter writer, DynawoSimulationParameters parameters, boolean withCurves) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "outputs");
        writer.writeAttribute("directory", "outputs");

        writer.writeEmptyElement(DYN_URI, "dumpInitValues");
        writer.writeAttribute("local", Boolean.toString(false));
        writer.writeAttribute("global", Boolean.toString(false));

        writer.writeEmptyElement(DYN_URI, "timeline");
        writer.writeAttribute("exportMode", parameters.getTimelineExportMode().toString());

        writer.writeEmptyElement(DYN_URI, "finalState");
        writer.writeAttribute("exportIIDMFile", Boolean.toString(parameters.isWriteFinalState()));
        writer.writeAttribute("exportDumpFile", Boolean.toString(parameters.getDumpFileParameters().exportDumpFile()));

        if (withCurves) {
            writer.writeEmptyElement(DYN_URI, "curves");
            writer.writeAttribute("inputFile", DynawoSimulationConstants.CRV_FILENAME);
            writer.writeAttribute("exportMode", DynawoSimulationParameters.ExportMode.CSV.toString());
        }

        writer.writeStartElement(DYN_URI, "logs");
        writeAppender(writer, parameters);
        writer.writeEndElement();

        writer.writeEndElement();
    }

    private static void writeAppender(XMLStreamWriter writer, DynawoSimulationParameters parameters) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "appender");
        writer.writeAttribute("tag", "");
        writer.writeAttribute("file", "dynawo.log");
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
}
