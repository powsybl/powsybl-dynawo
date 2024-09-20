/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.powsybl.dynawo.xml;

import com.powsybl.dynamicsimulation.OutputVariable;
import com.powsybl.dynawo.DynawoSimulationContext;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;

import static com.powsybl.dynawo.xml.DynawoSimulationConstants.CRV_FILENAME;
import static com.powsybl.dynawo.xml.DynawoSimulationConstants.FSV_FILENAME;
import static com.powsybl.dynawo.xml.DynawoSimulationXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel {@literal <demiguelm at aia.es>}
 */
public final class OutputVariablesXml extends AbstractXmlDynawoSimulationWriter {

    private final OutputVariable.OutputType outputType;
    private final String xmlElementName;

    private OutputVariablesXml(String xmlFileName, String xmlRootName, String xmlElementName, OutputVariable.OutputType outputType) {
        super(xmlFileName, xmlRootName);
        this.xmlElementName = xmlElementName;
        this.outputType = outputType;
    }

    public static void writeCurve(Path workingDir, DynawoSimulationContext context) throws IOException {
        new OutputVariablesXml(CRV_FILENAME, "curvesInput", "curve", OutputVariable.OutputType.CURVE)
                .createXmlFileFromContext(workingDir, context);
    }

    public static void writeFsv(Path workingDir, DynawoSimulationContext context) throws IOException {
        new OutputVariablesXml(FSV_FILENAME,"finalStateValuesInput", "finalStateValue", OutputVariable.OutputType.FSV)
                .createXmlFileFromContext(workingDir, context);
    }

    @Override
    public void write(XMLStreamWriter writer, DynawoSimulationContext context) throws XMLStreamException {
        for (OutputVariable dynCurve : context.getOutputVariables(outputType)) {
            writer.writeEmptyElement(DYN_URI, xmlElementName);
            writer.writeAttribute("model", dynCurve.getModelId());
            writer.writeAttribute("variable", dynCurve.getVariableName());
        }
    }
}
