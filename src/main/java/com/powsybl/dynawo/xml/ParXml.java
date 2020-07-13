/**
 * Copyright (c) 2020, RTE (http://www.rte-france.com)
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.powsybl.dynawo.xml;

import static com.powsybl.dynawo.xml.DynawoConstants.OMEGAREF_PAR_FILENAME;
import static com.powsybl.dynawo.xml.DynawoXmlConstants.DYN_URI;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.powsybl.commons.exceptions.UncheckedXmlStreamException;
import com.powsybl.dynamicsimulation.DynamicModel;
import com.powsybl.dynawo.DynawoContext;
import com.powsybl.dynawo.dyd.AbstractBlackBoxModel;
import com.powsybl.dynawo.dyd.DYNModelOmegaRef;

/**
 * @author Marcos de Miguel <demiguelm at aia.es>
 */
public final class ParXml {

    private static double WEIGHT = 10.0;

    private ParXml() {
    }

    public static void write(Path workingDir, DynawoContext context) throws IOException, XMLStreamException {
        Objects.requireNonNull(workingDir);
        Path file = workingDir.resolve(OMEGAREF_PAR_FILENAME);

        XmlUtil.write(file, context, "parametersSet", ParXml::write);
    }

    private static void write(XMLStreamWriter writer, DynawoContext context) {
        DynawoXmlContext xmlContext = new DynawoXmlContext(context);

        try {
            int index = -1;
            for (DynamicModel model : context.getDynamicModels()) {
                AbstractBlackBoxModel dynawoModel = (AbstractBlackBoxModel) model;
                if (dynawoModel instanceof DYNModelOmegaRef) {
                    index = xmlContext.getIndex(dynawoModel.getLib(), true);
                    if (index == 0) {
                        writer.writeStartElement(DYN_URI, "set");
                        writer.writeAttribute("id", dynawoModel.getParameterSetId());
                    }
                    writeParameter(writer, "DOUBLE", "weight_gen_" + index, Double.toString(WEIGHT));
                }
            }
            if (index >= 0) {
                writeParameter(writer, "INT", "nbGen", Integer.toString(index));
                writer.writeEndElement();
            }
        } catch (XMLStreamException e) {
            throw new UncheckedXmlStreamException(e);
        }
    }

    private static void writeParameter(XMLStreamWriter writer, String type, String name, String value) throws XMLStreamException {
        writer.writeEmptyElement(DYN_URI, "par");
        writer.writeAttribute("type", type);
        writer.writeAttribute("name", name);
        writer.writeAttribute("value", value);
    }

}
