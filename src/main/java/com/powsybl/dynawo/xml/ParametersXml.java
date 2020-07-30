/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.xml;

import static com.powsybl.dynawo.xml.DynawoConstants.OMEGAREF_PAR_FILENAME;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.dyd.AbstractBlackBoxModel;
import com.powsybl.dynawo.dyd.OmegaRef;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class ParametersXml {

    private ParametersXml() {
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);

        Path parametersFile = Paths.get(context.getDynawoParameters().getParametersFile());
        Path file = workingDir.resolve(parametersFile.getFileName().toString());
        XmlUtil.write(file, context, "parametersSet", ParametersXml::write);
    }

    public static void writeOmegaRef(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);

        Path file = workingDir.resolve(OMEGAREF_PAR_FILENAME);
        XmlUtil.write(file, context, "parametersSet", ParametersXml::writeOmegaRef);
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) {
        DynawoXmlContext xmlContext = new DynawoXmlContext(context);

        try {
            for (DynamicModel model : context.getDynamicModels()) {
                AbstractBlackBoxModel dynawoModel = (AbstractBlackBoxModel) model;
                if (model instanceof OmegaRef) {
                    continue;
                }
                dynawoModel.writeParameters(writer, xmlContext);
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeOmegaRef(XMLStreamWriter writer, DynawoContext context) {
        DynawoXmlContext xmlContext = new DynawoXmlContext(context);

        try {
            for (DynamicModel model : context.getDynamicModels()) {
                AbstractBlackBoxModel dynawoModel = (AbstractBlackBoxModel) model;
                if (model instanceof OmegaRef) {
                    dynawoModel.writeParameters(writer, xmlContext);
                }
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }
}
