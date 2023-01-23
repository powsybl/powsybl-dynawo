/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawaltz.xml;

import com.powsybl.commons.PowsyblException;
import com.powsybl.commons.config.PlatformConfig;
import com.powsybl.dynawaltz.DynaWaltzContext;
import com.powsybl.dynawaltz.DynaWaltzParameters;
import com.powsybl.dynawaltz.DynaWaltzParametersDatabase;
import com.powsybl.dynawaltz.models.BlackBoxModel;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
        copy(parameters.getParametersFile(), workingDir, context.getPlatformConfig());
        copy(parameters.getNetwork().getParametersFile(), workingDir, context.getPlatformConfig());
        copy(parameters.getSolver().getParametersFile(), workingDir, context.getPlatformConfig());

        // Write parameterSet that needs to be generated (OmegaRef...)
        Path file = workingDir.resolve(context.getSimulationParFile());
        XmlUtil.write(file, context, "parametersSet", ParametersXml::write);
    }

    private static void copy(String filename, Path workingDir, PlatformConfig platformConfig) throws IOException {
        FileSystem fs = platformConfig.getConfigDir()
                .map(Path::getFileSystem)
                .orElseThrow(() -> new PowsyblException("A configuration directory should be defined"));
        Path source = fs.getPath(filename);
        if (!Files.exists(source)) {
            throw new FileNotFoundException("File not found: " + filename);
        }
        Files.copy(source, workingDir.resolve(source.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
    }

    private static void write(XMLStreamWriter writer, DynaWaltzContext context) throws XMLStreamException {
        for (BlackBoxModel model : context.getBlackBoxModels()) {
            model.writeParameters(writer, context);
        }
    }

    public static void writeParameter(XMLStreamWriter writer, DynaWaltzParametersDatabase.ParameterType type, String name, String value) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "par");
        writer.writeAttribute("type", type.toString());
        writer.writeAttribute("name", name);
        writer.writeAttribute("value", value);
    }
}
