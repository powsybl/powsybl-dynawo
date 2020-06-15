/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static com.powsybl.dynawo.xml.DynawoConstants.*;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class JobsXml {

    private JobsXml() {
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(JOBS_FILENAME);

        XmlUtil.write(file, context, "jobs", JobsXml::write);
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) {
        try {
            writer.writeStartElement(DYN_URI, "job");
            writer.writeAttribute("name", "Job");
            writeSolver(writer, context);
            writeModeler(writer, context);
            writeSimulation(writer, context);
            writeOutput(writer, context);
            writer.writeEndElement();
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeSolver(XMLStreamWriter writer, DynawoContext context) throws XMLStreamException {
        DynawoSimulationParameters parameters = context.getDynawoParameters();
        writer.writeEmptyElement(DYN_URI, "solver");
        writer.writeAttribute("lib", parameters.getSolver().getType().equals(SolverType.IDA) ? "dynawo_SolverIDA" : "dynawo_SolverSIM");
        writer.writeAttribute("parFile", Paths.get(parameters.getSolver().getParametersFile()).getFileName().toString());
        writer.writeAttribute("parId", parameters.getSolver().getParametersId());
    }

    private static void writeModeler(XMLStreamWriter writer, DynawoContext context) throws XMLStreamException {
        DynawoSimulationParameters parameters = context.getDynawoParameters();
        writer.writeStartElement(DYN_URI, "modeler");
        writer.writeAttribute("compileDir", "outputs/compilation");

        writer.writeEmptyElement(DYN_URI, "network");
        writer.writeAttribute("iidmFile", NETWORK_FILENAME);
        writer.writeAttribute("parFile", Paths.get(parameters.getNetwork().getParametersFile()).getFileName().toString());
        writer.writeAttribute("parId", parameters.getNetwork().getParametersId());

        writer.writeEmptyElement(DYN_URI, "dynModels");
        writer.writeAttribute("dydFile", DYD_FILENAME);

        writer.writeEmptyElement(DYN_URI, "precompiledModels");
        writer.writeAttribute("useStandardModels", "true");

        writer.writeEmptyElement(DYN_URI, "modelicaModels");
        writer.writeAttribute("useStandardModels", "false");

        writer.writeEndElement();
    }

    private static void writeSimulation(XMLStreamWriter writer, DynawoContext context) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "simulation");
        writer.writeAttribute("startTime", Integer.toString(context.getParameters().getStartTime()));
        writer.writeAttribute("stopTime", Integer.toString(context.getParameters().getStopTime()));
    }

    private static void writeOutput(XMLStreamWriter writer, DynawoContext context) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "outputs");
        writer.writeAttribute("directory", "outputs");

        writer.writeEmptyElement(DYN_URI, "dumpInitValues");
        writer.writeAttribute("local", "false");
        writer.writeAttribute("global", "false");

        if (context.withCurves()) {
            writer.writeEmptyElement(DYN_URI, "curves");
            writer.writeAttribute("inputFile", DynawoConstants.CRV_FILENAME);
            writer.writeAttribute("exportMode", "CSV");
        }

        writer.writeEmptyElement(DYN_URI, "timeline");
        writer.writeAttribute("exportMode", "TXT");

        writer.writeEmptyElement(DYN_URI, "finalState");
        writer.writeAttribute("exportIIDMFile", "true");
        writer.writeAttribute("exportDumpFile", "false");

        writer.writeStartElement(DYN_URI, "logs");
        writeAppender(writer);
        writer.writeEndElement();

        writer.writeEndElement();
    }

    private static void writeAppender(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "appender");
        writer.writeAttribute("tag", "");
        writer.writeAttribute("file", "dynawo.log");
        writer.writeAttribute("lvlFilter", "DEBUG");
    }
}
