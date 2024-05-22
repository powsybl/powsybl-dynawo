/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.DumpFileParameters;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.DynaWaltzParameters.SolverType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;

import static com.powsybl.dynawaltz.xml.DynaWaltzConstants.*;
import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class JobsXml extends AbstractXmlDynawaltzWriter {

    private JobsXml() {
        super(JOBS_FILENAME, "jobs");
    }

    public static void write(Path workingDir, DynaWaltzContext context) throws IOException {
        new JobsXml().createXmlFileFromContext(workingDir, context);
    }

    @Override
    public void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "job");
        writer.writeAttribute("name", "Job");
        writeSolver(writer, context);
        writeModeler(writer, context);
        writeSimulation(writer, context);
        writeOutput(writer, context);
        writer.writeEndElement();
    }

    private static void writeSolver(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        DynaWaltzParameters parameters = context.getDynaWaltzParameters();
        writer.writeEmptyElement(DYN_URI, "solver");
        writer.writeAttribute("lib", parameters.getSolverType().equals(SolverType.IDA) ? "dynawo_SolverIDA" : "dynawo_SolverSIM");
        writer.writeAttribute("parFile", DynaWaltzParameters.SOLVER_OUTPUT_PARAMETERS_FILE);
        writer.writeAttribute("parId", parameters.getSolverParameters().getId());
    }

    private static void writeModeler(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        DynaWaltzParameters parameters = context.getDynaWaltzParameters();
        writer.writeStartElement(DYN_URI, "modeler");
        writer.writeAttribute("compileDir", "outputs/compilation");

        writer.writeEmptyElement(DYN_URI, "network");
        writer.writeAttribute("iidmFile", NETWORK_FILENAME);
        writer.writeAttribute("parFile", DynaWaltzParameters.NETWORK_OUTPUT_PARAMETERS_FILE);
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

    private static void writeSimulation(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "simulation");
        writer.writeAttribute("startTime", Integer.toString(context.getParameters().getStartTime()));
        writer.writeAttribute("stopTime", Integer.toString(context.getParameters().getStopTime()));
    }

    private static void writeOutput(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "outputs");
        writer.writeAttribute("directory", "outputs");

        writer.writeEmptyElement(DYN_URI, "dumpInitValues");
        writer.writeAttribute("local", Boolean.toString(false));
        writer.writeAttribute("global", Boolean.toString(false));

        writer.writeEmptyElement(DYN_URI, "timeline");
        writer.writeAttribute("exportMode", context.getDynaWaltzParameters().getTimelineExportMode().name());

        writer.writeEmptyElement(DYN_URI, "finalState");
        writer.writeAttribute("exportIIDMFile", Boolean.toString(context.getDynaWaltzParameters().isWriteFinalState()));
        writer.writeAttribute("exportDumpFile", Boolean.toString(context.getDynaWaltzParameters().getDumpFileParameters().exportDumpFile()));

        if (context.withCurves()) {
            writer.writeEmptyElement(DYN_URI, "curves");
            writer.writeAttribute("inputFile", DynaWaltzConstants.CRV_FILENAME);
            writer.writeAttribute("exportMode", DynaWaltzParameters.ExportMode.CSV.name());
        }

        writer.writeStartElement(DYN_URI, "logs");
        writeAppender(writer);
        writer.writeEndElement();

        writer.writeEndElement();
    }

    private static void writeAppender(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "appender");
        writer.writeAttribute("tag", "");
        writer.writeAttribute("file", "dynawaltz.log");
        writer.writeAttribute("lvlFilter", "DEBUG");
    }
}
