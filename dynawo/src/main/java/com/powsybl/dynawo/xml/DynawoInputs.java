/**
 * Copyright (c) 2019, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import static com.powsybl.dynawo.xml.DynawoConstants.CRV_FILENAME;
import static com.powsybl.dynawo.xml.DynawoConstants.DYD_FILENAME;
import static com.powsybl.dynawo.xml.DynawoConstants.JOBS_FILENAME;
import static com.powsybl.dynawo.xml.DynawoConstants.PAR_FILENAME;
import static com.powsybl.dynawo.xml.DynawoConstants.SOLVER_PAR_FILENAME;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_PREFIX;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.EMPTY_PREFIX;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.dynawo.DynawoInputProvider;
import com.powsybl.dynawo.crv.DynawoCurve;
import com.powsybl.dynawo.dyd.DynawoDynamicModel;
import com.powsybl.dynawo.job.DynawoJob;
import com.powsybl.dynawo.par.DynawoParameterSet;
import com.powsybl.dynawo.simulator.DynawoSimulationParameters.SolverParameters;
import com.powsybl.iidm.network.Network;

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
    }

    public static void prepare(Network network, SolverParameters solverParameters, DynawoInputProvider inputProvider,
        Path workingDir) throws IOException, XMLStreamException {
        prepareJobFile(network, solverParameters, inputProvider, workingDir);
        prepareDydFile(network, inputProvider, workingDir);
        prepareParFile(network, inputProvider, workingDir);
        prepareParSolverFile(network, solverParameters, inputProvider, workingDir);
        prepareCrvFile(network, inputProvider, workingDir);
    }

    public static void prepareJobFile(Network network, SolverParameters solverParameters, DynawoInputProvider inputProvider,
        Path workingDir) throws IOException, XMLStreamException {
        Path jobFile = workingDir.resolve(JOBS_FILENAME);
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (Writer jobWriter = Files.newBufferedWriter(jobFile, StandardCharsets.UTF_8)) {
            XMLStreamWriter jobXmlWriter = output.createXMLStreamWriter(jobWriter);
            try {
                List<DynawoJob> jobs = inputProvider.getDynawoJobs(network);

                jobXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");

                jobXmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                jobXmlWriter.writeStartElement(DYN_URI, "jobs");
                jobXmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);
                DynawoJobs.writeJobs(jobXmlWriter, solverParameters, jobs);
                jobXmlWriter.writeEndElement();
                jobXmlWriter.writeEndDocument();
            } finally {
                jobXmlWriter.close();
            }
        }
    }

    public static void prepareDydFile(Network network, DynawoInputProvider inputProvider, Path workingDir)
        throws IOException, XMLStreamException {
        Path dydFile = workingDir.resolve(DYD_FILENAME);
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (Writer dydWriter = Files.newBufferedWriter(dydFile, StandardCharsets.UTF_8)) {
            XMLStreamWriter dydXmlWriter = output.createXMLStreamWriter(dydWriter);
            try {
                List<DynawoDynamicModel> dyds = inputProvider.getDynawoDynamicModels(network);

                dydXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");

                dydXmlWriter.setPrefix(DYN_PREFIX, DYN_URI);
                dydXmlWriter.writeStartElement(DYN_URI, "dynamicModelsArchitecture");
                dydXmlWriter.writeNamespace(DYN_PREFIX, DYN_URI);
                DynawoDynamicModels.writeDynamicModels(dydXmlWriter, dyds);
                dydXmlWriter.writeEndElement();
                dydXmlWriter.writeEndDocument();
            } finally {
                dydXmlWriter.close();
            }
        }
    }

    public static void prepareParFile(Network network, DynawoInputProvider inputProvider, Path workingDir)
        throws IOException, XMLStreamException {
        Path parFile = workingDir.resolve(PAR_FILENAME);
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (Writer parWriter = Files.newBufferedWriter(parFile, StandardCharsets.UTF_8)) {
            XMLStreamWriter parXmlWriter = output.createXMLStreamWriter(parWriter);
            try {
                List<DynawoParameterSet> pars = inputProvider.getDynawoParameterSets(network);

                parXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");

                parXmlWriter.setPrefix(EMPTY_PREFIX, DYN_URI);
                parXmlWriter.writeStartElement("parametersSet");
                parXmlWriter.writeNamespace(EMPTY_PREFIX, DYN_URI);
                DynawoSimulationParameters.writeParameterSets(parXmlWriter, pars);
                parXmlWriter.writeEndElement();
                parXmlWriter.writeEndDocument();
            } finally {
                parXmlWriter.close();
            }
        }
    }

    public static void prepareParSolverFile(Network network, SolverParameters solverParameters,
        DynawoInputProvider inputProvider, Path workingDir) throws IOException, XMLStreamException {
        Path parSolverFile = workingDir.resolve(SOLVER_PAR_FILENAME);
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (Writer parSolverWriter = Files.newBufferedWriter(parSolverFile, StandardCharsets.UTF_8)) {
            XMLStreamWriter parSolverXmlWriter = output.createXMLStreamWriter(parSolverWriter);
            try {
                List<DynawoParameterSet> spars = inputProvider.getDynawoSolverParameterSets(network);

                parSolverXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");

                parSolverXmlWriter.setPrefix(EMPTY_PREFIX, DYN_URI);
                parSolverXmlWriter.writeStartElement("parametersSet");
                parSolverXmlWriter.writeNamespace(EMPTY_PREFIX, DYN_URI);
                DynawoSolverParameters.writeParameterSets(parSolverXmlWriter, solverParameters, spars);
                parSolverXmlWriter.writeEndElement();
                parSolverXmlWriter.writeEndDocument();
            } finally {
                parSolverXmlWriter.close();
            }
        }
    }

    public static void prepareCrvFile(Network network, DynawoInputProvider inputProvider, Path workingDir)
        throws IOException, XMLStreamException {
        Path crvFile = workingDir.resolve(CRV_FILENAME);
        XMLOutputFactory output = XMLOutputFactory.newInstance();
        try (Writer crvWriter = Files.newBufferedWriter(crvFile, StandardCharsets.UTF_8)) {
            XMLStreamWriter crvXmlWriter = output.createXMLStreamWriter(crvWriter);
            try {
                List<DynawoCurve> curves = inputProvider.getDynawoCurves(network);

                crvXmlWriter.writeStartDocument(StandardCharsets.UTF_8.toString(), "1.0");

                crvXmlWriter.setPrefix(EMPTY_PREFIX, DYN_URI);
                crvXmlWriter.writeStartElement("curvesInput");
                crvXmlWriter.writeNamespace(EMPTY_PREFIX, DYN_URI);
                crvXmlWriter.writeComment("Curves for scenario");
                DynawoCurves.writeCurves(crvXmlWriter, curves);
                crvXmlWriter.writeEndElement();
                crvXmlWriter.writeEndDocument();
            } finally {
                crvXmlWriter.close();
            }
        }
    }
}
