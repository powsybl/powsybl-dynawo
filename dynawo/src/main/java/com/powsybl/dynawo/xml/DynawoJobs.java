/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.job.DynawoJob;
import com.powsybl.dynawo.job.DynawoModeler;
import com.powsybl.dynawo.job.DynawoOutputs;
import com.powsybl.dynawo.job.DynawoSimulation;
import com.powsybl.dynawo.job.DynawoSolver;
import com.powsybl.dynawo.job.LogAppender;

import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class DynawoJobs {

    private DynawoJobs() {
    }

    public static void writeJobs(XMLStreamWriter writer, List<DynawoJob> jobs) throws XMLStreamException {
        for (DynawoJob job : jobs) {
            writeJob(writer, job);
        }
    }

    private static void writeJob(XMLStreamWriter writer, DynawoJob job) throws XMLStreamException {
        String jobName = job.getName();
        writer.writeStartElement(DYN_URI, "job");
        writer.writeAttribute("name", jobName);
        writeSolver(writer, job.getSolver());
        writeModeler(writer, job.getModeler());
        writeSimulation(writer, job.getSimulation());
        writeOutput(writer, job.getOutputs());
        writer.writeEndElement();
    }

    private static void writeSolver(XMLStreamWriter writer, DynawoSolver solver) throws XMLStreamException {
        String solverLib = solver.getLib();
        String solverParams = solver.getFile();
        String solverParamsId = solver.getId();
        writer.writeEmptyElement(DYN_URI, "solver");
        writer.writeAttribute("lib", solverLib);
        writer.writeAttribute("parFile", solverParams);
        writer.writeAttribute("parId", solverParamsId);
    }

    private static void writeModeler(XMLStreamWriter writer, DynawoModeler modeler) throws XMLStreamException {
        String compileDir = modeler.getCompileDir();
        String preCompiledModelsDir = modeler.getPreCompiledModelsDir();
        boolean useStandardModelsPreCompiledModels = modeler.isUseStandardModelsPreCompiledModels();
        String modelicaModelsDir = modeler.getModelicaModelsDir();
        boolean useStandardModelsModelicaModels = modeler.isUseStandardModelsModelicaModels();
        String iidmFile = modeler.getIidm();
        String parFile = modeler.getParameters();
        String parId = modeler.getParameterId();
        String dydFile = modeler.getDyd();
        String initialState = modeler.getInitialState();
        writer.writeStartElement(DYN_URI, "modeler");
        writer.writeAttribute("compileDir", compileDir);
        writer.writeEmptyElement(DYN_URI, "network");
        writer.writeAttribute("iidmFile", iidmFile);
        writer.writeAttribute("parFile", parFile);
        writer.writeAttribute("parId", parId);
        writer.writeEmptyElement(DYN_URI, "dynModels");
        writer.writeAttribute("dydFile", dydFile);
        if (initialState != null) {
            writer.writeEmptyElement(DYN_URI, "initialState");
            writer.writeAttribute("file", initialState);
        }
        writer.writeEmptyElement(DYN_URI, "precompiledModels");
        modelsDir(writer, preCompiledModelsDir);
        writer.writeAttribute("useStandardModels", Boolean.toString(useStandardModelsPreCompiledModels));
        writer.writeEmptyElement(DYN_URI, "modelicaModels");
        modelsDir(writer, modelicaModelsDir);
        writer.writeAttribute("useStandardModels", Boolean.toString(useStandardModelsModelicaModels));
        writer.writeEndElement();
    }

    private static void modelsDir(XMLStreamWriter writer, String modelicaModelsDir) throws XMLStreamException {
        if (modelicaModelsDir != null) {
            writer.writeAttribute("directory", modelicaModelsDir);
        }
    }

    private static void writeSimulation(XMLStreamWriter writer, DynawoSimulation simulation) throws XMLStreamException {
        int startTime = simulation.getStartTime();
        int stopTime = simulation.getStopTime();
        boolean activeCriteria = simulation.isActiveCriteria();
        writer.writeEmptyElement(DYN_URI, "simulation");
        writer.writeAttribute("startTime", Integer.toString(startTime));
        writer.writeAttribute("stopTime", Integer.toString(stopTime));
        writer.writeAttribute("activateCriteria", Boolean.toString(activeCriteria));
    }

    private static void writeOutput(XMLStreamWriter writer, DynawoOutputs outputs) throws XMLStreamException {
        String outputDir = outputs.getDirectory();
        boolean dumpLocalInitValues = outputs.isDumpLocalInitValues();
        boolean dumpGlobalInitValues = outputs.isDumpGlobalInitValues();
        String constraints = outputs.getConstraints();
        String timeLine = outputs.getTimeLine();
        boolean exportFinalState = outputs.isExportFinalState();
        boolean exportIidmFile = outputs.isExportIidmFile();
        boolean exportDumpFile = outputs.isExportDumpFile();
        String curvesFile = outputs.getCurve();
        String curvesExportMode = outputs.getExportMode();
        List<LogAppender> appenders = outputs.getAppenders();
        writer.writeStartElement(DYN_URI, "outputs");
        writer.writeAttribute("directory", outputDir);
        writer.writeEmptyElement(DYN_URI, "dumpInitValues");
        writer.writeAttribute("local", Boolean.toString(dumpLocalInitValues));
        writer.writeAttribute("global", Boolean.toString(dumpGlobalInitValues));
        writer.writeEmptyElement(DYN_URI, "curves");
        writer.writeAttribute("inputFile", curvesFile);
        writer.writeAttribute("exportMode", curvesExportMode);
        writer.writeEmptyElement(DYN_URI, "timeline");
        writer.writeAttribute("exportMode", timeLine);
        if (constraints != null) {
            writer.writeEmptyElement(DYN_URI, "constraints");
            writer.writeAttribute("exportMode", constraints);
        }
        if (exportFinalState) {
            writer.writeEmptyElement(DYN_URI, "finalState");
            writer.writeAttribute("exportIIDMFile", Boolean.toString(exportIidmFile));
            writer.writeAttribute("exportDumpFile", Boolean.toString(exportDumpFile));
        }
        writer.writeStartElement(DYN_URI, "logs");
        for (LogAppender appender : appenders) {
            writeAppender(writer, appender);
        }
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private static void writeAppender(XMLStreamWriter writer, LogAppender appender) throws XMLStreamException {
        String tag = appender.getTag();
        String file = appender.getFile();
        String lvlFilter = appender.getLvlFilter();
        writer.writeEmptyElement(DYN_URI, "appender");
        writer.writeAttribute("tag", tag);
        writer.writeAttribute("file", file);
        writer.writeAttribute("lvlFilter", lvlFilter);
    }
}
