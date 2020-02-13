/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.inputs.xml;

import static com.powsybl.dynawo.inputs.xml.DynawoXmlConstants.DYN_URI;

import java.util.List;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.inputs.model.job.Job;
import com.powsybl.dynawo.inputs.model.job.Modeler;
import com.powsybl.dynawo.inputs.model.job.Outputs;
import com.powsybl.dynawo.inputs.model.job.Simulation;
import com.powsybl.dynawo.inputs.model.job.Solver;
import com.powsybl.dynawo.inputs.model.job.LogAppender;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public class JobsXml implements DynawoCollectionXmlFile {

    @Override
    public String getFilename() {
        return DynawoConstants.JOBS_FILENAME;
    }

    @Override
    public String getCollectionTag() {
        return "jobs";
    }

    @Override
    public void writeCollection(XMLStreamWriter writer, List<?> jobs) throws XMLStreamException {
        Objects.requireNonNull(writer);
        Objects.requireNonNull(jobs);
        for (Object job : jobs) {
            assert job instanceof Job;
            writeJob(writer, (Job) job);
        }
    }

    private static void writeJob(XMLStreamWriter writer, Job job) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "job");
        writer.writeAttribute("name", job.getName());
        writeSolver(writer, job.getSolver());
        writeModeler(writer, job.getModeler());
        writeSimulation(writer, job.getSimulation());
        writeOutput(writer, job.getOutputs());
        writer.writeEndElement();
    }

    private static void writeSolver(XMLStreamWriter writer, Solver solver) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "solver");
        writer.writeAttribute("lib", solver.getLib());
        writer.writeAttribute("parFile", solver.getParFile());
        writer.writeAttribute("parId", solver.getParId());
    }

    private static void writeModeler(XMLStreamWriter writer, Modeler modeler) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "modeler");
        writer.writeAttribute("compileDir", modeler.getCompileDir());
        writer.writeEmptyElement(DYN_URI, "network");
        writer.writeAttribute("iidmFile", modeler.getIidm());
        writer.writeAttribute("parFile", modeler.getParameters());
        writer.writeAttribute("parId", modeler.getParameterId());
        writer.writeEmptyElement(DYN_URI, "dynModels");
        writer.writeAttribute("dydFile", modeler.getDyd());
        String initialState = modeler.getInitialState();
        if (initialState != null) {
            writer.writeEmptyElement(DYN_URI, "initialState");
            writer.writeAttribute("file", initialState);
        }
        writer.writeEmptyElement(DYN_URI, "precompiledModels");
        modelsDir(writer, modeler.getPreCompiledModelsDir());
        writer.writeAttribute("useStandardModels", Boolean.toString(modeler.isUseStandardModelsPreCompiledModels()));
        writer.writeEmptyElement(DYN_URI, "modelicaModels");
        modelsDir(writer, modeler.getModelicaModelsDir());
        writer.writeAttribute("useStandardModels", Boolean.toString(modeler.isUseStandardModelsModelicaModels()));
        writer.writeEndElement();
    }

    private static void modelsDir(XMLStreamWriter writer, String modelicaModelsDir) throws XMLStreamException {
        if (modelicaModelsDir != null) {
            writer.writeAttribute("directory", modelicaModelsDir);
        }
    }

    private static void writeSimulation(XMLStreamWriter writer, Simulation simulation) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "simulation");
        writer.writeAttribute("startTime", Integer.toString(simulation.getStartTime()));
        writer.writeAttribute("stopTime", Integer.toString(simulation.getStopTime()));
        writer.writeAttribute("activateCriteria", Boolean.toString(simulation.isActiveCriteria()));
        double precision = simulation.getPrecision();
        if (precision != 0.0) {
            writer.writeAttribute("precision", Double.toString(precision));
        }
    }

    private static void writeOutput(XMLStreamWriter writer, Outputs outputs) throws XMLStreamException {
        writer.writeStartElement(DYN_URI, "outputs");
        writer.writeAttribute("directory", outputs.getDirectory());
        writer.writeEmptyElement(DYN_URI, "dumpInitValues");
        writer.writeAttribute("local", Boolean.toString(outputs.isDumpLocalInitValues()));
        writer.writeAttribute("global", Boolean.toString(outputs.isDumpGlobalInitValues()));
        writer.writeEmptyElement(DYN_URI, "curves");
        writer.writeAttribute("inputFile", outputs.getCurve());
        writer.writeAttribute("exportMode", outputs.getExportMode());
        writer.writeEmptyElement(DYN_URI, "timeline");
        writer.writeAttribute("exportMode", outputs.getTimeLine());
        String constraints = outputs.getConstraints();
        if (constraints != null) {
            writer.writeEmptyElement(DYN_URI, "constraints");
            writer.writeAttribute("exportMode", constraints);
        }
        boolean exportFinalState = outputs.isExportFinalState();
        if (exportFinalState) {
            writer.writeEmptyElement(DYN_URI, "finalState");
            writer.writeAttribute("exportIIDMFile", Boolean.toString(outputs.isExportIidmFile()));
            writer.writeAttribute("exportDumpFile", Boolean.toString(outputs.isExportDumpFile()));
        }
        writer.writeStartElement(DYN_URI, "logs");
        List<LogAppender> appenders = outputs.getAppenders();
        for (LogAppender appender : appenders) {
            writeAppender(writer, appender);
        }
        writer.writeEndElement();
        writer.writeEndElement();
    }

    private static void writeAppender(XMLStreamWriter writer, LogAppender appender) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "appender");
        writer.writeAttribute("tag", appender.getTag());
        writer.writeAttribute("file", appender.getFile());
        writer.writeAttribute("lvlFilter", appender.getLvlFilter());
    }

}
