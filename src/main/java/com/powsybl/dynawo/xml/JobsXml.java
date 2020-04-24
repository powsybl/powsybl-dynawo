/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
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
import java.util.Objects;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverType;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class JobsXml {

    public static final String JOBS_FILENAME = "powsybl_dynawo.jobs";

    private JobsXml() {
    }

    private static String getNamespacePrefix() {
        return DynawoXmlConstants.DYN_PREFIX;
    }

    private static String getNamespaceUri() {
        return DynawoXmlConstants.DYN_URI;
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Objects.requireNonNull(context);
        Path file = workingDir.resolve(JOBS_FILENAME);
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            XMLStreamWriter xmlWriter = output.createXMLStreamWriter(writer);
            try {
                xmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");
                xmlWriter.setPrefix(getNamespacePrefix(), getNamespaceUri());
                xmlWriter.writeStartElement(getNamespaceUri(), "jobs");
                xmlWriter.writeNamespace(getNamespacePrefix(), getNamespaceUri());

                write(xmlWriter, context);

                xmlWriter.writeEndElement();
                xmlWriter.writeEndDocument();
            } finally {
                xmlWriter.close();
            }
        }
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) throws XMLStreamException {
        writer.writeStartElement(getNamespaceUri(), "job");
        writer.writeAttribute("name", "Job");
        writeSolver(writer, context);
        writeModeler(writer, context);
        writeSimulation(writer, context);
        writeOutput(writer);
        writer.writeEndElement();
    }

    private static void writeSolver(XMLStreamWriter writer, DynawoContext context) throws XMLStreamException {
        DynawoSimulationParameters parameters = Objects.requireNonNull(context.getParameters().getExtension(DynawoSimulationParameters.class));
        writer.writeEmptyElement(getNamespaceUri(), "solver");
        writer.writeAttribute("lib", parameters.getSolver().getType().equals(SolverType.IDA) ? "dynawo_SolverIDA" : "dynawo_SolverSIM");
        writer.writeAttribute("parFile", parameters.getSolver().getParametersFile());
        writer.writeAttribute("parId", parameters.getSolver().getParametersId());
    }

    private static void writeModeler(XMLStreamWriter writer, DynawoContext context) throws XMLStreamException {
        DynawoSimulationParameters parameters = Objects.requireNonNull(context.getParameters().getExtension(DynawoSimulationParameters.class));
        writer.writeStartElement(getNamespaceUri(), "modeler");
        writer.writeAttribute("compileDir", "outputs/compilation");

        writer.writeEmptyElement(getNamespaceUri(), "network");
        writer.writeAttribute("iidmFile", "powsybl_dynawo.xiidm");
        writer.writeAttribute("parFile", parameters.getNetwork().getParametersFile());
        writer.writeAttribute("parId", parameters.getNetwork().getParametersId());

        writer.writeEmptyElement(getNamespaceUri(), "dynModels");
        writer.writeAttribute("dydFile", "powsybl_dynawo.dyd");

        writer.writeEmptyElement(getNamespaceUri(), "precompiledModels");
        writer.writeAttribute("useStandardModels", Boolean.toString(true));

        writer.writeEmptyElement(getNamespaceUri(), "modelicaModels");
        writer.writeAttribute("useStandardModels", Boolean.toString(false));

        writer.writeEndElement();
    }

    private static void writeSimulation(XMLStreamWriter writer, DynawoContext context) throws XMLStreamException {
        writer.writeEmptyElement(getNamespaceUri(), "simulation");
        writer.writeAttribute("startTime", Integer.toString(context.getParameters().getStartTime()));
        writer.writeAttribute("stopTime", Integer.toString(context.getParameters().getStopTime()));
    }

    private static void writeOutput(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeStartElement(getNamespaceUri(), "outputs");
        writer.writeAttribute("directory", "outputs");

        writer.writeEmptyElement(getNamespaceUri(), "dumpInitValues");
        writer.writeAttribute("local", Boolean.toString(false));
        writer.writeAttribute("global", Boolean.toString(false));

        writer.writeEmptyElement(getNamespaceUri(), "curves");
        writer.writeAttribute("inputFile", "powsybl_dynawo.crv");
        writer.writeAttribute("exportMode", "CSV");

        writer.writeEmptyElement(getNamespaceUri(), "timeline");
        writer.writeAttribute("exportMode", "TXT");

        writer.writeEmptyElement(getNamespaceUri(), "finalState");
        writer.writeAttribute("exportIIDMFile", Boolean.toString(true));
        writer.writeAttribute("exportDumpFile", Boolean.toString(false));

        writer.writeStartElement(getNamespaceUri(), "logs");
        writeAppender(writer);
        writer.writeEndElement();

        writer.writeEndElement();
    }

    private static void writeAppender(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEmptyElement(getNamespaceUri(), "appender");
        writer.writeAttribute("tag", "");
        writer.writeAttribute("file", "dynawo.log");
        writer.writeAttribute("lvlFilter", "DEBUG");
    }
}
