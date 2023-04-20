/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawaltz.xml;

import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.parameters.ParameterType;
import com.powsybl.dynawaltz.parameters.ParametersSet;
import com.powsybl.dynawaltz.models.BlackBoxModel;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import static com.powsybl.dynawaltz.xml.DynaWaltzXmlConstants.DYN_URI;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class ParametersXml {

    private ParametersXml() {
    }

    public static void write(Path workingDir, DynaWaltzContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);

        DynaWaltzParameters parameters = context.getDynaWaltzParameters();
        write(parameters.getModelsParameters(), DynaWaltzParameters.MODELS_OUTPUT_PARAMETERS_FILE, workingDir);
        write(parameters.getNetwork().getParametersSet(), DynaWaltzParameters.NETWORK_OUTPUT_PARAMETERS_FILE, workingDir);
        write(parameters.getSolver().getParametersSet(), DynaWaltzParameters.SOLVER_OUTPUT_PARAMETERS_FILE, workingDir);

        // Write parameterSet that needs to be generated (OmegaRef...)
        Path file = workingDir.resolve(context.getSimulationParFile());
        XmlUtil.write(file, context, "parametersSet", ParametersXml::write);
    }

    private static void write(ParametersSet parametersSet, String filename, Path workingDir) throws IOException, XMLStreamException {
        parametersSet.write(workingDir.resolve(filename));
    }

    private static void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        for (BlackBoxModel model : context.getBlackBoxModels()) {
            model.writeParameters(writer, context);
        }
    }

    public static void writeParameter(XMLStreamWriter writer, ParameterType type, String name, String value) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "par");
        writer.writeAttribute("type", type.toString());
        writer.writeAttribute("name", name);
        writer.writeAttribute("value", value);
    }

    public static void writeReference(XMLStreamWriter writer, ParameterType type, String name, String origData, String origName) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "reference");
        writer.writeAttribute("type", type.toString());
        writer.writeAttribute("name", name);
        writer.writeAttribute("origData", origData);
        writer.writeAttribute("origName", origName);
    }
}
